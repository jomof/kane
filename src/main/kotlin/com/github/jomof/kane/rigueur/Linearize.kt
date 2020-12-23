package com.github.jomof.kane.rigueur

class LinearModel<E:Any> {
    private val map = mutableMapOf<ScalarExpr<E>, Slot<E>>()
    private val namedScalarVariables = mutableMapOf<String, NamedScalarVariable<E>>()
    private val namedMatrixVariableElements = mutableMapOf<String, MatrixVariableElement<E>>()
    private val namedScalars = mutableMapOf<String, Slot<E>>()
    private val assignments = mutableListOf<Pair<Int, ScalarExpr<E>>>()
    private val slots = mutableListOf<Pair<Slot<E>, String>>()

    private fun allocateSlot(expr : ScalarExpr<E>, comment : String) : Slot<E> {
        if (slots.size == 14) {
            "hello"
        }
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

    fun registerMatrixVariableElement(expr : MatrixVariableElement<E>) : Slot<E> {
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

    fun getValue(expr : Expr<E>) = when(expr) {
        is ScalarExpr -> map.getValue(expr)
        else -> error("$javaClass")
    }

    fun computeSlot(expr : ScalarExpr<E>, compute:(ScalarExpr<E>) -> ScalarExpr<E>) : Slot<E> {
        if (map.containsKey(expr)) return map.getValue(expr)
        val computed = compute(expr)
        if (computed is Slot) return computed
        if (map.containsKey(computed)) return map.getValue(computed)
        val slot = allocateSlot(computed, "$computed = $expr")
        assignments += slot.slot to computed
        map[expr] = slot
        map[computed] = slot
        return slot
    }

    fun assignNamedScalar(name : String, slot : Slot<E>) {
        val target = namedScalars.getValue(name)
        assignments += target.slot to slot
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

    fun allocateSpace(init: (Int) -> E) = type.allocArray(slots.size, init)
    fun contains(expr : ScalarExpr<E>) = map.containsKey(expr)
    fun getValue(expr : ScalarExpr<E>) = map.getValue(expr)
    fun setValue(expr : ScalarExpr<E>, slot : Slot<E>) { map[expr] = slot }
    fun scalar(name : String) = namedScalars[name]!!.slot
    fun scalar(expr : NamedScalarVariable<E>) = scalar(expr.name)
    fun scalar(expr : NamedScalar<E>) = scalar(expr.name)

    fun eval(space : Array<E>) {
        assignments.forEach { (output, expr) ->
            space[output] = expr.eval(space)
        }
    }


}

private fun <E:Any> Expr<E>.linearizeExpr(model : LinearModel<E> = LinearModel()) : Expr<E> {
    fun NamedScalar<E>.self() = linearizeExpr(model) as ScalarExpr<E>
    fun ScalarExpr<E>.self() = linearizeExpr(model) as ScalarExpr<E>
    fun MatrixExpr<E>.self() = linearizeExpr(model) as MatrixExpr<E>
    if(this is ScalarExpr && model.contains(this)) return model.getValue(this)
    val result = when(this) {
        is ConstantScalar -> this
        is MatrixVariableElement -> model.slotOfExistingMatrixVariableElement(this)
        is NamedScalarVariable -> model.slotOfExistingNamedScalarVariable(this)
        is UnaryScalar -> model.computeSlot(this) { copy(value = value.self()) }
        is UnaryMatrixScalar -> model.computeSlot(this) { copy(value = value.self()) }
        is BinaryScalar -> model.computeSlot(this) { copy(left = left.self(), right = right.self()) }
        is BinaryScalarMatrix -> toDataMatrix().map { it.self() }
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
                    is NamedMatrix -> it.copy(matrix = it.self())
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
                        model.assignNamedScalar(it.left.name, model.knownSlot(it.right))
                    }
                    is NamedMatrixAssign -> {
                        (it.left.elements zip it.right.elements).forEach { (left, right) ->
                            model.assignNamedScalar("$left", model.knownSlot(right))
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
    if(this is ScalarExpr && result is Slot) model.setValue(this, result as Slot<E>)
    return result
}

private fun <E:Any> Expr<E>.claimScalarVariables(model : LinearModel<E> = LinearModel()) {
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


private fun <E:Any> Expr<E>.claimMatrixVariables(model : LinearModel<E> = LinearModel()) {
    fun NamedScalar<E>.self() = claimMatrixVariables(model)
    fun ScalarExpr<E>.self() = claimMatrixVariables(model)
    fun MatrixExpr<E>.self() = claimMatrixVariables(model)
    when(this) {
        is ConstantScalar -> {}
        is MatrixVariableElement -> model.registerMatrixVariableElement(this)
        is NamedScalarVariable -> {}
        is NamedScalarVariable -> {}
        is UnaryScalar -> value.self()
        is BinaryScalar -> {
            left.self()
            right.self()
        }
        is UnaryMatrixScalar -> value.self()
        is UnaryMatrix -> elements.forEach { it.self() }
        is BinaryMatrix -> elements.forEach { it.self() }
        is BinaryMatrixScalar -> elements.forEach { it.self() }
        is BinaryScalarMatrix -> elements.forEach { it.self() }
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

private fun <E:Any> Expr<E>.eval(space : Array<E>) : E {
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


fun <E:Any> Expr<E>.linearize() : LinearModel<E> {
    val model = LinearModel<E>()
    claimMatrixVariables(model)
    claimScalarVariables(model)
    linearizeExpr(model)
    return model
}