package com.github.jomof.kane.rigueur

import com.github.jomof.kane.rigueur.functions.*
import com.github.jomof.kane.rigueur.types.AlgebraicType
import com.github.jomof.kane.rigueur.types.zero

class LinearModel<E:Number>(val type : AlgebraicType<E>) {
    private val map = mutableMapOf<ScalarExpr<E>, Slot<E>>()
    private val namedScalarVariables = mutableMapOf<String, NamedScalarVariable<E>>()
    private val namedMatrixVariableElements = mutableMapOf<String, MatrixVariableElement<E>>()
    private val namedMatrixShapes = mutableMapOf<String, MatrixShape<E>>()
    private val namedScalars = mutableMapOf<String, Slot<E>>()
    private val assignments = mutableListOf<Pair<Int, ScalarExpr<E>>>()
    private val slots = mutableListOf<Pair<Slot<E>, String>>()
    private val assignBacks = mutableSetOf<Pair<Int, ScalarExpr<E>>>()

    private fun allocateSlot(expr : ScalarExpr<E>, comment : String) : Slot<E> {
        val slot = Slot(slots.size, expr)
        slots += slot to comment
        return slot
    }

    fun knownSlot(expr:ScalarExpr<E>) : Slot<E> {
        return map.getValue(expr)
    }

    fun registerNamedScalarVariable(expr : NamedScalarVariable<E>) : Slot<E> {
        val slot = namedScalars[expr.name]
        if (slot != null) return slot
        namedScalarVariables[expr.name] = expr
        val result = allocateSlot(expr, expr.toString())
        namedScalars[expr.name] = result
        return  result
    }

    fun slotOfExistingNamedScalarVariable(expr : NamedScalarVariable<E>) : Slot<E> {
        return namedScalars.getValue(expr.name)
    }

    fun registerNamedMatrixShape(name : String, init : () -> MatrixShape<E>) {
        namedMatrixShapes.computeIfAbsent(name) { init() }
    }

    fun registerMatrixVariableElement(expr : MatrixVariableElement<E>) : Slot<E> {
        registerNamedMatrixShape(expr.matrix.name) {
            LinearMatrixShape(expr.matrix.columns, expr.matrix.rows, slots.size, expr.type)
        }
        val name = "${expr.matrix.name}[${expr.column},${expr.row}]"
        return namedScalars.computeIfAbsent(name) {
            namedMatrixVariableElements[name] = expr
            allocateSlot(expr, expr.toString())
        }
    }

    fun slotOfExistingMatrixVariableElement(expr : MatrixVariableElement<E>) : Slot<E> {
        val name = "${expr.matrix.name}[${expr.column},${expr.row}]"
        return namedScalars.getValue(name)
    }

    fun registerNamedScalar(name : String, scalar : ScalarExpr<E>) {
        if (namedScalars.containsKey(name))
            return
        if (map.containsKey(scalar)) {
            namedScalars[name] = map.getValue(scalar)
            return
        }
        if (scalar is Slot) {
            namedScalars[name] = scalar
            return
        }
        computeSlot(scalar) { scalar }
        registerNamedScalar(name, scalar)
    }

    fun getValue(expr : AlgebraicExpr<E>) = when(expr) {
        is ScalarExpr -> map.getValue(expr)
        else -> error("$javaClass")
    }

    fun computeSlot(expr : ScalarExpr<E>, compute:(ScalarExpr<E>) -> ScalarExpr<E>) : Slot<E> {
        if (map.containsKey(expr)) return map.getValue(expr)
        val computed = compute(expr)
        if (computed is Slot) return computed
        if (map.containsKey(computed)) return map.getValue(computed)
        val comment = if ("$computed" == "$expr") "$expr" else "$computed = $expr"
        val slot = allocateSlot(computed, comment)
        assignments += slot.slot to computed
        map[expr] = slot
        map[computed] = slot
        return slot
    }

    fun assignBack(name : String, slot : Slot<E>) {
        val target = namedScalars.getValue(name)
        assignments += target.slot to slot
        assignBacks += target.slot to slot
    }

    fun assignBack(name : String, constant : ConstantScalar<E>) {
        val target = namedScalars.getValue(name)
        assignments += target.slot to constant
        assignBacks += target.slot to constant
    }

    override fun toString(): String {
        val sb = StringBuilder()
        slots.forEach { (slot,comment) ->
            sb.append("# ${slot.slot} : $comment\n")
        }
        assignments.forEach { (slot, computed) ->
            sb.append("$slot <- $computed\n")
        }
        return sb.toString()
    }

    fun allocateSpace() : Array<E> {
        val result = type.allocArray(slots.size) { type.zero }
        namedScalarVariables.forEach { (name, variable) ->
            val slot = namedScalars.getValue(name)
            result[slot.slot] = variable.initial
        }
        namedMatrixVariableElements.forEach { (name, variable) ->
            val slot = namedScalars.getValue(name)
            result[slot.slot] = variable.initial
        }
        return result
    }

    fun contains(expr : ScalarExpr<E>) = map.containsKey(expr)
    fun getValue(expr : ScalarExpr<E>) = map.getValue(expr)
    fun setValue(expr : ScalarExpr<E>, slot : Slot<E>) { map[expr] = slot }
    fun shape(expr : NamedScalarExpr<E>) = EmbeddedScalarShape(namedScalars.getValue(expr.name).slot)
    fun shape(expr : NamedMatrixVariable<E>) = namedMatrixShapes.getValue(expr.name)
    fun shape(expr : NamedMatrixExpr<E>) =
        namedMatrixShapes.getValue(expr.name)

    fun eval(space : Array<E>) {
        assignments.forEach { (output, expr) ->
            space[output] = expr.eval(space)
        }
    }

    fun eval(space : Array<E>, protect : MatrixShape<E>) {
        assignments.forEach { (output, expr) ->
            if(!protect.owns(output)) {
                space[output] = expr.eval(space)
            }
        }
    }

    fun check() {
        // Walk assignments and make sure there aren't any
        val space = type.allocNullableArray(slots.size) { null }
        namedMatrixVariableElements.forEach {
            val slot = map[it.value]!!
            space[slot.slot] = type.zero
        }
        assignments.forEach { (output, expr) ->
            if (space[output] != null && !assignBacks.contains(output to expr)) {
                error("Slot $output was already initialized")
            }
            space[output] = expr.evalNullable(space)
        }
        space.indices.forEach { index ->
            if(space[index] == null) {
                error("Index $index was not assigned")
            }
        }
    }
}

private fun <E:Number> AlgebraicExpr<E>.linearizeExpr(model : LinearModel<E> = LinearModel(type)) : AlgebraicExpr<E> {
    if(this is ScalarExpr && model.contains(this)) return model.getValue(this)
    val result = when(this) {
        is Slot -> this
        is ConstantScalar -> this
        is MatrixVariableElement -> model.slotOfExistingMatrixVariableElement(this)
        is NamedScalarVariable -> model.slotOfExistingNamedScalarVariable(this)
        is AlgebraicUnaryScalar -> model.computeSlot(this) { copy(value = value.linearizeExpr(model) as ScalarExpr<E>) }
        is AlgebraicUnaryMatrixScalar -> model.computeSlot(this) { copy(value = value.linearizeExpr(model) as MatrixExpr<E>) }
        is AlgebraicBinaryScalar -> model.computeSlot(this) {
            copy(left = left.linearizeExpr(model) as ScalarExpr<E>,
                right = right.linearizeExpr(model) as ScalarExpr<E>) }
        is DataMatrix -> map { it.linearizeExpr(model) as ScalarExpr<E> }
        is NamedScalar -> {
            val result = model.computeSlot(scalar) { scalar.linearizeExpr(model) as ScalarExpr<E> }
            model.registerNamedScalar(name, result)
            result
        }
        is NamedMatrix -> {
            copy(matrix = matrix.toDataMatrix().map { element ->
                val result = model.computeSlot(element) { element.linearizeExpr(model) as ScalarExpr<E>}
                //model.registerNamedScalar(name, result)
                result
            })
        }
        is Tableau -> {
            val result = copy(children = children.map {
                when(it) {
                    is NamedScalar -> it.copy(scalar = it.linearizeExpr(model) as ScalarExpr<E>)
                    is NamedMatrix -> {
                        val matrix = it.matrix.linearizeExpr(model) as MatrixExpr<E>
                        val pure = matrix.elements.all { element -> element is Slot }
                        if (!pure) {
                            LookupOrConstantsMatrixShape(it.columns, it.rows, matrix.elements, it.type)
                        } else {
                            val slots = matrix.elements.map { element -> (element as Slot).slot }.toList()
                            val start = slots[0]
                            val linear = slots.indices.all { index -> slots[index] != start + index }
                            model.registerNamedMatrixShape(it.name) {
                                if (linear) {
                                    LinearMatrixShape(it.columns, it.rows, start, it.type)
                                } else {
                                    LookupMatrixShape(it.columns, it.rows, slots, it.type)
                                }
                            }
                        }
                        it.copy(matrix = matrix)
                    }
                    is NamedScalarAssign ->
                        it.copy(right = it.right.linearizeExpr(model) as ScalarExpr<E> )
                    is NamedMatrixAssign -> {
                        it.right.elements.forEach { element ->
                            model.computeSlot(element) { element.linearizeExpr(model) as ScalarExpr<E> }
                        }
                        it
                    }
                    is NamedScalarVariable -> it
                    is NamedMatrixVariable -> it
                    else -> error("${it.javaClass}")
                }
            })
            // Assignments after everything else
            children.forEach {
                when(it) {
                    is NamedScalar -> { }
                    is NamedMatrix -> { }
                    is NamedScalarVariable -> { }
                    is NamedMatrixVariable -> { }
                    is NamedScalarAssign -> {
                        when(it.right) {
                            is ConstantScalar -> model.assignBack(it.left.name, it.right)
                            else -> model.assignBack(it.left.name, model.knownSlot(it.right))
                        }
                    }
                    is NamedMatrixAssign -> {
                        (it.left.elements zip it.right.elements).forEach { (left, right) ->
                            model.assignBack("$left", model.knownSlot(right))
                        }
                    }
                    else ->
                        error("${it.javaClass}")
                }
            }
            result
        }
        else ->
            error("$javaClass")
    }
    if(this is ScalarExpr && result is Slot) model.setValue(this, result)
    return result
}

private fun <E:Number> AlgebraicExpr<E>.claimScalarVariables(model : LinearModel<E> = LinearModel(type)) {
    visit {
        if (it is NamedScalarAssign<*>)
            model.registerNamedScalarVariable(it.left as NamedScalarVariable<E>)
        if (it is NamedScalarVariable<*>)
            model.registerNamedScalarVariable(it as NamedScalarVariable<E>)
    }
}


private fun <E:Number> AlgebraicExpr<E>.claimMatrixVariables(model : LinearModel<E> = LinearModel(type)) {
    val seen = mutableSetOf<String>()
    visit {
        if (it is MatrixVariableElement<*>) {
            model.registerMatrixVariableElement(it as MatrixVariableElement<E>)
        }
        if (it is NamedMatrixAssign<*>) {
            if (!seen.contains(it.name)) {
                seen += it.name
                it.left.elements.forEach { element ->
                    model.registerMatrixVariableElement(element as MatrixVariableElement<E>)
                }
            }
        }
        if (it is NamedMatrixVariable<*>) {
            if (!seen.contains(it.name)) {
                seen += it.name
                it.elements.forEach { element ->
                    model.registerMatrixVariableElement(element as MatrixVariableElement<E>)
                }
            }
        }
    }
}

private fun <E:Number> AlgebraicExpr<E>.evalNullable(space : Array<E?>) : E {
    return when(this) {
        is ConstantScalar -> value
        is Slot -> space[slot] ?: error("Access of uninitialized slot $slot by $replaces")
        is NamedScalar -> scalar.evalNullable(space)
        is AlgebraicUnaryScalar -> op(value.evalNullable(space))
        is AlgebraicBinaryScalar -> op(left.evalNullable(space), right.evalNullable(space))
        else -> error("$javaClass")
    }
}

private fun <E:Number> AlgebraicExpr<E>.eval(space : Array<E>) : E {
    return when(this) {
        is ConstantScalar -> value
        is Slot -> space[slot]
        is NamedScalar -> scalar.eval(space)
        is AlgebraicBinaryScalar -> op(left.eval(space), right.eval(space))
        is AlgebraicUnaryScalar -> op(value.eval(space))
        else ->
            error("$javaClass")
    }
}


private fun <E:Number> AlgebraicExpr<E>.gatherNamedExprs() : Set<NamedAlgebraicExpr<E>> {
    val result = mutableSetOf<NamedAlgebraicExpr<E>>()
    visit { expr ->
        if (expr is NamedAlgebraicExpr<*>) result += (expr as NamedAlgebraicExpr<E>)
    }
    return result
}

private fun <E:Number> AlgebraicExpr<E>.stripNames() : AlgebraicExpr<E> {
    fun <E:Number> ScalarExpr<E>.self() = stripNames() as ScalarExpr
    fun <E:Number> MatrixExpr<E>.self() = toDataMatrix().stripNames() as MatrixExpr
    return when(this) {
        is ConstantScalar -> this
        is MatrixVariableElement -> this
        is NamedScalarVariable -> this
        is NamedMatrixVariable -> this
        is NamedMatrix -> matrix.self()
        is NamedScalar -> scalar.self()
        is AlgebraicUnaryScalar -> {
            val value = value.self()
            if (this.value !== value) copy(value = value)
            else this
        }
        is AlgebraicBinaryScalar -> copy(left = left, right = right)
        is DataMatrix -> map { it.self() }
        is Tableau -> copy(children= children.map { child ->
            when(child) {
                is NamedScalar -> child.copy(scalar = child.scalar.self())
                is NamedScalarVariable -> child
                is NamedMatrixVariable -> child
                is NamedMatrix -> child.copy(matrix = child.matrix.self())
                is NamedMatrixAssign -> child.copy(right = child.right.self())
                is NamedScalarAssign -> child.copy(right = child.right.self())
                else ->
                    error("${child.javaClass}")
            }})

        else -> error("$javaClass")
    }
}

private fun Expr.terminal() : Boolean = when(this) {
    is ConstantScalar<*> -> true
    is Slot<*> -> true
    is NamedScalarVariable<*> -> true
    is MatrixVariableElement<*> -> true
    is AlgebraicBinaryScalar<*> -> false
    is AlgebraicUnaryScalar<*> -> false
    else -> error("$javaClass")
}

fun <E:Number> AlgebraicExpr<E>.rollUpCommonSubexpressions(model : LinearModel<E>) : AlgebraicExpr<E> {
    return when {
        this is AlgebraicUnaryScalar && value.terminal() ->
            linearizeExpr(model) as ScalarExpr<E>
        this is AlgebraicBinaryScalar && left.terminal() && right.terminal() ->
            linearizeExpr(model) as ScalarExpr<E>
        this is ConstantScalar -> this
        this is MatrixVariableElement -> this
        this is NamedScalarVariable -> this
        this is NamedMatrixVariable -> this
        this is Slot -> this
        this is NamedScalar -> {
            val scalar = scalar.rollUpCommonSubexpressions(model) as ScalarExpr<E>
            if (this.scalar !== scalar) (copy(scalar = scalar) as ScalarExpr<E>).rollUpCommonSubexpressions(model)
            else this
        }
        this is AlgebraicUnaryScalar -> {
            val value = value.rollUpCommonSubexpressions(model) as ScalarExpr<E>
            if (this.value !== value) copy(value = value).rollUpCommonSubexpressions(model)
            else this
        }
        this is AlgebraicBinaryScalar -> {
            val left = left.rollUpCommonSubexpressions(model) as ScalarExpr<E>
            val right = right.rollUpCommonSubexpressions(model) as ScalarExpr<E>
            if (this.left !== left || this.right !== right) copy(left = left, right = right).rollUpCommonSubexpressions(model)
            else this
        }
        this is NamedScalarAssign -> {
            val right = right.rollUpCommonSubexpressions(model) as ScalarExpr<E>
            if (this.right !== right) copy(right = right).rollUpCommonSubexpressions(model)
            else this
        }
        this is NamedMatrix -> {
            val matrix = matrix.rollUpCommonSubexpressions(model) as MatrixExpr<E>
            if (this.matrix !== matrix) copy(matrix = matrix)
            else this
        }
        this is NamedMatrixAssign -> {
            val right = right.rollUpCommonSubexpressions(model) as MatrixExpr<E>
            if (this.right !== right) copy(right = right)
            else this
        }
        this is DataMatrix -> map { it.rollUpCommonSubexpressions(model) as ScalarExpr<E> }
        this is Tableau -> copy(children = children.map { child ->
            child.rollUpCommonSubexpressions(model) as NamedAlgebraicExpr<E>
        })
        else -> error("$javaClass")
    }
}

fun <E:Number> AlgebraicExpr<E>.linearize() : LinearModel<E> {
    val names = gatherNamedExprs().toList().map { it.reduceArithmetic() }
    with(Tableau(names)) {
        val model = LinearModel(type)
        claimMatrixVariables(model)
        claimScalarVariables(model)
        println("Stripping names")
        var stripped = stripNames()
        println("Consolidating common sub-expressions")
        stripped = stripped.rollUpCommonSubexpressions(model)
        stripped.linearizeExpr(model)
        return model
    }
}

fun <E:Number> NamedScalarExpr<E>.toFunc(v1 : NamedScalarVariable<E>) : (E) -> E {
    val model = linearize()
    val space = model.allocateSpace()
    val v1ref = model.shape(v1).ref(space)
    val thisRef = model.shape(this).ref(space)

    return { p1 ->
        v1ref.set(p1)
        model.eval(space)
        thisRef.value
    }
}

fun <E:Number> LinearModel<E>.toFunc(space : Array<E>, v1 : NamedMatrixExpr<E>, out :  NamedMatrixExpr<E>) : (Matrix<E>) -> Matrix<E> {
    val clone = space.clone()
    val v1shape = shape(v1)
    val v1ref = v1shape.ref(clone)
    val outRef = shape(out).ref(clone)

    return { p1 ->
        v1ref.set(p1)
        eval(clone, protect = v1shape)
        outRef
    }
}