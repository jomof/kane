package com.github.jomof.kane.rigueur

import com.github.jomof.kane.rigueur.types.AlgebraicType
import com.github.jomof.kane.rigueur.types.add

class LinearModel<E:Number> {
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
        return namedScalars.computeIfAbsent(expr.name) {
            namedScalarVariables[expr.name] = expr
            allocateSlot(expr, expr.toString())
        }
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

    private val type : AlgebraicType<E> get() = map.keys.first().type


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
    fun shape(expr : NamedMatrixExpr<E>) = namedMatrixShapes.getValue(expr.name)

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

private fun <E:Number> AlgebraicExpr<E>.linearizeExpr(model : LinearModel<E> = LinearModel()) : AlgebraicExpr<E> {
    fun NamedScalar<E>.self() = linearizeExpr(model) as ScalarExpr<E>
    fun ScalarExpr<E>.self() = linearizeExpr(model) as ScalarExpr<E>
    fun MatrixExpr<E>.self() = linearizeExpr(model) as MatrixExpr<E>
    if(this is ScalarExpr && model.contains(this)) return model.getValue(this)
    val result = when(this) {
        is Slot -> this
        is ConstantScalar -> this
        is MatrixVariableElement -> model.slotOfExistingMatrixVariableElement(this)
        is NamedScalarVariable -> model.slotOfExistingNamedScalarVariable(this)
        is UnaryScalar -> model.computeSlot(this) { copy(value = value.self()) }
        is UnaryMatrixScalar -> model.computeSlot(this) { copy(value = value.self()) }
        is BinaryScalar -> model.computeSlot(this) { copy(left = left.self(), right = right.self()) }
        is BinaryScalarMatrix -> toDataMatrix().map { it.self() }
        is DataMatrix -> map { it.self() }
        is NamedScalar -> {
            val result = model.computeSlot(scalar) { scalar.self() }
            model.registerNamedScalar(name, result)
            result
        }
        is NamedMatrix -> {
            copy(matrix = matrix.toDataMatrix().map { element ->
                val result = model.computeSlot(element) { element.self() }
                //model.registerNamedScalar(name, result)
                result
            })
        }
        is Tableau -> {
            val result = copy(children = children.map {
                when(it) {
                    is NamedScalar -> it.copy(scalar = it.self())
                    is NamedMatrix -> {
                        val matrix = it.matrix.self()
                        val pure = matrix.elements.all { element -> element is Slot }
                        if (!pure) {
                            println("${it.name} had non-slot elements")
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
                        it.copy(right = it.right.self() )
                    is NamedMatrixAssign -> {
                        it.right.elements.forEach { element ->
                            model.computeSlot(element) { element.self() }
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
                        model.assignBack(it.left.name, model.knownSlot(it.right))
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

private fun <E:Number> AlgebraicExpr<E>.claimScalarVariables(model : LinearModel<E> = LinearModel()) {
    fun NamedScalar<E>.self() = claimScalarVariables(model)
    fun ScalarExpr<E>.self() = claimScalarVariables(model)
    fun MatrixExpr<E>.self() = claimScalarVariables(model)
    when(this) {
        is ConstantScalar -> {}
        is MatrixVariableElement -> {}
        is NamedScalarVariable -> model.registerNamedScalarVariable(this)
        is UnaryScalar -> value.self()
        is UnaryMatrixScalar -> value.self()
        is BinaryScalar -> {
            left.self()
            right.self()
        }
        is DataMatrix -> elements.forEach { it.self() }
        is BinaryMatrix -> elements.forEach { it.self() }
        is BinaryMatrixScalar -> elements.forEach { it.self() }
        is BinaryScalarMatrix -> elements.forEach { it.self() }
        is UnaryMatrix -> elements.forEach { it.self() }
        is NamedMatrix-> matrix.self()
        is NamedScalar -> scalar.self()
        is Tableau -> {
            children.map {
                when(it) {
                    is NamedScalar -> it.self()
                    is NamedMatrix -> it.self()
                    is NamedScalarAssign -> it.right.self()
                    is NamedMatrixAssign -> it.right.self()
                    is NamedScalarVariable -> { }
                    is NamedMatrixVariable -> { }
                    else -> error("${it.javaClass}")
                }
            }
        }
        else -> error("$javaClass")
    }
}


private fun <E:Number> AlgebraicExpr<E>.claimMatrixVariables(
    model : LinearModel<E> = LinearModel(),
    seen : MutableSet<String> = mutableSetOf()) {
    fun NamedScalar<E>.self() = claimMatrixVariables(model, seen)
    fun ScalarExpr<E>.self() = claimMatrixVariables(model, seen)
    fun MatrixExpr<E>.self() = claimMatrixVariables(model, seen)
    when(this) {
        is NamedMatrixVariable -> {
            if (!seen.contains(name)) {
                elements.forEach { model.registerMatrixVariableElement(it) }
                seen += name
            }
        }
        is ConstantScalar -> {}
        is MatrixVariableElement -> matrix.self()
        is NamedScalarVariable -> {}
        is UnaryScalar -> value.self()
        is BinaryScalar -> {
            left.self()
            right.self()
        }
        is UnaryMatrixScalar -> value.self()
        is UnaryMatrix -> elements.forEach { it.self() }
        is DataMatrix -> elements.forEach { it.self() }
        is BinaryMatrix -> elements.forEach { it.self() }
        is BinaryMatrixScalar -> elements.forEach { it.self() }
        is BinaryScalarMatrix -> elements.forEach { it.self() }
        is NamedMatrix-> matrix.self()
        is NamedScalar -> scalar.self()
        is Tableau -> {
            children.forEach {
                when(it) {
                    is NamedScalar -> it.self()
                    is NamedMatrix -> it.self()
                    is NamedScalarAssign -> it.right.self()
                    is NamedMatrixAssign -> it.right.self()
                    is NamedScalarVariable -> { }
                    is NamedMatrixVariable -> { }
                    else -> error("${it.javaClass}")
                }
            }
        }
        else ->
            error("$javaClass")
    }
}

private fun <E:Number> AlgebraicExpr<E>.evalNullable(space : Array<E?>) : E {
    return when(this) {
        is ConstantScalar -> value
        is Slot -> space[slot] ?: error("Access of uninitialized slot $slot by $replaces")
        is NamedScalar -> scalar.evalNullable(space)
        is BinaryScalar -> type.binary(op, left.evalNullable(space), right.evalNullable(space))
        is UnaryScalar -> type.unary(op, value.evalNullable(space))
        is UnaryMatrixScalar -> when(op) {
            SUMMATION -> children.map { it.evalNullable(space) }.fold(type.zero) { prior, current -> type.add(prior, current) }
            else -> error("$op")
        }
        else ->
            error("$javaClass")
    }
}

private fun <E:Number> AlgebraicExpr<E>.eval(space : Array<E>) : E {
    return when(this) {
        is ConstantScalar -> value
        is Slot -> space[slot]
        is NamedScalar -> scalar.eval(space)
        is BinaryScalar -> type.binary(op, left.eval(space), right.eval(space))
        is UnaryScalar -> type.unary(op, value.eval(space))
        is UnaryMatrixScalar -> when(op) {
            SUMMATION -> children.map { it.eval(space) }.fold(type.zero) { prior, current -> type.add(prior, current) }
            else -> error("$op")
        }
        else ->
            error("$javaClass")
    }
}

private fun <E:Number> AlgebraicExpr<E>.gatherLeafExpressions() : List<ScalarExpr<E>> {
    fun AlgebraicExpr<E>.terminal() : Boolean = when(this) {
        is ConstantScalar -> true
        is Slot -> true
        is NamedScalarVariable -> true
        is MatrixVariableElement -> true
        is UnaryMatrixScalar -> false
        is BinaryScalar -> false
        is UnaryScalar -> false
        else -> error("$javaClass")
    }

    return foldTopDown(listOf()) { prior, current ->
        prior + when {
            current is UnaryScalar && current.value.terminal() ->
                listOf(current)
            current is BinaryScalar && current.left.terminal() && current.right.terminal() ->
                listOf(current)
            else -> listOf()
        }
    }
}


private fun <E:Number> AlgebraicExpr<E>.gatherNamedExprs() =
    foldTopDown(mutableSetOf<NamedAlgebraicExpr<E>>()) { prior, expr ->
        if (expr is NamedAlgebraicExpr) prior += expr
        prior
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
        is UnaryScalar -> copy(value = value.self())
        is UnaryMatrix -> copy(value = value.self())
        is UnaryMatrixScalar -> copy(value = value.self())
        is BinaryScalarMatrix -> copy(left = left.self(), right = right.self())
        is BinaryMatrixScalar -> copy(left = left.self(), right = right.self())
        is BinaryMatrix -> copy(left = left.self(), right = right.self())
        is BinaryScalar -> copy(left = left.self(), right = right.self())
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

private fun <E:Number> AlgebraicExpr<E>.linearizeExprs(
    exprs : Set<ScalarExpr<E>>,
    model : LinearModel<E>) : AlgebraicExpr<E> {
    fun ScalarExpr<E>.self() = linearizeExprs(exprs, model) as ScalarExpr
    fun MatrixExpr<E>.self() = linearizeExprs(exprs, model) as MatrixExpr
    fun NamedAlgebraicExpr<E>.self() = linearizeExprs(exprs, model) as NamedAlgebraicExpr
    if (exprs.contains(this)) {
        return linearizeExpr(model)
    }
    return when(this) {
        is ConstantScalar -> this
        is MatrixVariableElement -> this
        is NamedScalarVariable -> this
        is NamedMatrixVariable -> this
        is Slot -> this
        is NamedScalarAssign -> copy(right = right.self())
        is NamedMatrixAssign -> copy(right = right.self())
        is NamedMatrix -> copy(matrix = matrix.self())
        is NamedScalar -> copy(scalar = scalar.self())
        is UnaryScalar -> copy(value = value.self())
        is UnaryMatrix -> copy(value = value.self())
        is UnaryMatrixScalar -> copy(value = value.self())
        is BinaryScalarMatrix -> copy(left = left.self(), right = right.self())
        is BinaryMatrixScalar -> copy(left = left.self(), right = right.self())
        is BinaryMatrix -> copy(left = left.self(), right = right.self())
        is BinaryScalar -> copy(left = left.self(), right = right.self())
        is DataMatrix -> map { it.self() }
        is Tableau -> copy(children = children.map { child -> child.self() })
        else -> error("$javaClass")
    }
}

fun <E:Number> AlgebraicExpr<E>.linearize() : LinearModel<E> {
    val names = gatherNamedExprs()
    with(Tableau(names.toList())) {
        val model = LinearModel<E>()
        claimMatrixVariables(model)
        claimScalarVariables(model)
        var stripped = stripNames()
        while (true) {
            val leafs = stripped
                .gatherLeafExpressions()
                .groupBy { it }
                .toList()
                .map { it.first to it.second.size }
                .sortedByDescending { it.second }
            if (leafs.isEmpty()) break
            val max = leafs.first().second
            val maxes = leafs.filter { it.second == max }.map { it.first }.toSet()
            stripped = stripped.linearizeExprs(maxes, model)
        }
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