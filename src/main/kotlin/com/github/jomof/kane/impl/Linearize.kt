package com.github.jomof.kane.impl

import com.github.jomof.kane.*
import com.github.jomof.kane.impl.ComputableIndex.MoveableIndex
import com.github.jomof.kane.impl.sheet.CoerceScalar
import com.github.jomof.kane.impl.sheet.Sheet
import com.github.jomof.kane.impl.sheet.SheetRangeExpr
import com.github.jomof.kane.impl.types.AlgebraicType
import com.github.jomof.kane.impl.types.kaneDouble
import com.github.jomof.kane.impl.visitor.RewritingVisitor
import com.github.jomof.kane.impl.visitor.visit


class LinearModel(val type : AlgebraicType) {
    private val map = mutableMapOf<ScalarExpr, Slot>()
    private val namedScalarVariables = mutableMapOf<Id, ScalarVariable>()
    private val namedMatrixVariableElements = mutableMapOf<Id, MatrixVariableElement>()
    private val namedMatrixShapes = mutableMapOf<Id, MatrixShape>()
    private val namedScalars = mutableMapOf<Id, Slot>()
    private val randomVariables = mutableMapOf<RandomVariableExpr, Slot>()
    private val assignments = mutableListOf<Pair<Int, ScalarExpr>>()
    private val slots = mutableListOf<Pair<Slot, String>>()
    private val assignBacks = mutableSetOf<Pair<Int, ScalarExpr>>()

    private fun allocateSlot(expr: ScalarExpr, comment: String): Slot {
        val slot = Slot(slots.size, expr)
        slots += slot to comment
        return slot
    }

    fun knownSlot(expr: ScalarExpr): Slot {
        return map.getValue(expr)
    }

    fun registerNamedScalarVariable(expr: ScalarVariable): Slot {
        val slot = namedScalars[expr.name]
        if (slot != null) return slot
        namedScalarVariables[expr.name] = expr
        val result = allocateSlot(expr, expr.toString())
        namedScalars[expr.name] = result
        return result
    }

    fun registerRandomVariable(expr : RandomVariableExpr) : Slot {
        val slot = randomVariables[expr]
        if (slot != null) return slot
        val result = allocateSlot(expr, expr.toString())
        randomVariables[expr] = result
        return  result
    }

    fun slotOfExistingNamedScalarVariable(expr: ScalarVariable): Slot {
        return namedScalars.getValue(expr.name)
    }

    fun slotOfExistingRandomVariable(expr : RandomVariableExpr) : Slot {
        return randomVariables.getValue(expr)
    }

    fun registerNamedMatrixShape(name: Id, init: () -> MatrixShape) {
        namedMatrixShapes.computeIfAbsent(name) { init() }
    }

    fun registerMatrixVariableElement(expr : MatrixVariableElement) : Slot {
        registerNamedMatrixShape(expr.matrix.name) {
            LinearMatrixShape(expr.matrix.columns, expr.matrix.rows, slots.size, kaneDouble)
        }
        val name = "${expr.matrix.name}[${expr.column},${expr.row}]"
        return namedScalars.computeIfAbsent(name) {
            namedMatrixVariableElements[name] = expr
            allocateSlot(expr, expr.toString())
        }
    }

    fun slotOfExistingMatrixVariableElement(expr : MatrixVariableElement) : Slot {
        val name = "${expr.matrix.name}[${expr.column},${expr.row}]"
        return namedScalars.getValue(name)
    }

    fun registerNamedScalar(name: Id, scalar: ScalarExpr) {
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

    fun getValue(expr : AlgebraicExpr) = when(expr) {
        is ScalarExpr -> map.getValue(expr)
        else -> error("$javaClass")
    }

    fun computeSlot(expr : ScalarExpr, compute:(ScalarExpr) -> ScalarExpr) : Slot {
        assert(expr !is AlgebraicSummaryScalarScalar)
        assert(expr !is AlgebraicSummaryMatrixScalar)
        assert(expr !is AlgebraicBinarySummaryScalarScalarScalar)
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

    fun assignBack(name: Id, slot: Slot) {
        val target = namedScalars.getValue(name)
        assignments += target.slot to slot
        assignBacks += target.slot to slot
    }

    fun assignBack(name: Id, constant: ConstantScalar) {
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

    fun allocateSpace() : DoubleArray {
        val result = DoubleArray(slots.size) { 0.0 }
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

    fun contains(expr: ScalarExpr) = map.containsKey(expr)
    fun getValue(expr: ScalarExpr) = map.getValue(expr)
    fun setValue(expr: ScalarExpr, slot: Slot) {
        map[expr] = slot
    }

    fun shape(name: String) = EmbeddedScalarShape(namedScalars.getValue(name).slot)
    fun shape(expr: ScalarExpr) =
        EmbeddedScalarShape(namedScalars.getValue(expr.name).slot)

    fun shape(expr: MatrixVariable) = namedMatrixShapes.getValue(expr.name)
    fun shape(expr: MatrixExpr) = namedMatrixShapes.getValue(expr.name)
    fun slotCount(): Int = slots.size

    fun eval(space: DoubleArray) {
        assignments.forEach { (output, expr) ->
            space[output] = expr.evalSpace(space)
        }
    }

    fun eval(space : DoubleArray, protect : MatrixShape) {
        assignments.forEach { (output, expr) ->
            if(!protect.owns(output)) {
                space[output] = expr.evalSpace(space)
            }
        }
    }
}

private fun AlgebraicExpr.linearizeExpr(model: LinearModel = LinearModel(kaneDouble)): AlgebraicExpr {
    if (this is ScalarExpr && model.contains(this)) return model.getValue(this)

    val result = when {
        this is Slot -> this
        this is ConstantScalar -> this
        this is RetypeScalar -> copy(scalar = scalar.linearizeExpr(model) as ScalarExpr)
        this is AlgebraicSummaryScalarScalar -> dup(value = value.linearizeExpr(model) as StatisticExpr)
        this is AlgebraicSummaryMatrixScalar -> dup(value = value.linearizeExpr(model) as MatrixExpr)
        this is AlgebraicBinarySummaryScalarScalarScalar -> dup(
            left = left.linearizeExpr(model) as ScalarExpr,
            right = right.linearizeExpr(model) as ScalarExpr,
        )
        this is MatrixVariableElement -> model.slotOfExistingMatrixVariableElement(this)
        this is ScalarVariable -> model.slotOfExistingNamedScalarVariable(this)
        this is RandomVariableExpr -> model.slotOfExistingRandomVariable(this)
        this is AlgebraicUnaryScalarScalar -> model.computeSlot(this) { dup(value = value.linearizeExpr(model) as ScalarExpr) }
        this is AlgebraicBinaryScalarScalarScalar -> model.computeSlot(this) {
            dup(
                left = left.linearizeExpr(model) as ScalarExpr,
                right = right.linearizeExpr(model) as ScalarExpr
            )
        }
        this is DataMatrix -> map { it.linearizeExpr(model) as ScalarExpr }
        this is NamedScalar -> {
            if (scalar !is AlgebraicBinarySummaryScalarScalarScalar
                && scalar !is AlgebraicSummaryScalarScalar
                && scalar !is AlgebraicSummaryMatrixScalar
            ) {
                val result = model.computeSlot(scalar) { scalar.linearizeExpr(model) as ScalarExpr }
                model.registerNamedScalar(name, result)
                result
            } else copy(scalar = scalar.linearizeExpr(model) as ScalarExpr)
        }
        this is NamedMatrix -> {
            copy(matrix = matrix.toDataMatrix().map { element ->
                val result = model.computeSlot(element) { element.linearizeExpr(model) as ScalarExpr }
                //model.registerNamedScalar(name, result)
                result
            })
        }
        this is Tableau -> {
            val result = copy(children = children.map {
                when {
                    it is NamedScalar -> it.copy(scalar = it.linearizeExpr(model) as ScalarExpr)
                    it is ScalarExpr -> it.linearizeExpr(model).withNameOf(it)
                    it is DataMatrix -> {
                        val matrix = it.linearizeExpr(model) as MatrixExpr
                        val pure = matrix.elements.all { element -> element is Slot }
                        if (!pure) {
                            LookupOrConstantsMatrixShape(it.columns, it.rows, matrix.elements, kaneDouble)
                        } else {
                            val slots = matrix.elements.map { element -> (element as Slot).slot }.toList()
                            val start = slots[0]
                            val linear = slots.indices.all { index -> slots[index] != start + index }
                            model.registerNamedMatrixShape(it.name) {
                                if (linear) {
                                    LinearMatrixShape(it.columns, it.rows, start, kaneDouble)
                                } else {
                                    LookupMatrixShape(it.columns, it.rows, slots, kaneDouble)
                                }
                            }
                        }
                        matrix.withNameOf(it)
                    }
                    it is NamedMatrix -> {
                        val matrix = it.matrix.linearizeExpr(model) as MatrixExpr
                        val pure = matrix.elements.all { element -> element is Slot }
                        if (!pure) {
                            LookupOrConstantsMatrixShape(it.columns, it.rows, matrix.elements, kaneDouble)
                        } else {
                            val slots = matrix.elements.map { element -> (element as Slot).slot }.toList()
                            val start = slots[0]
                            val linear = slots.indices.all { index -> slots[index] != start + index }
                            model.registerNamedMatrixShape(it.name) {
                                if (linear) {
                                    LinearMatrixShape(it.columns, it.rows, start, kaneDouble)
                                } else {
                                    LookupMatrixShape(it.columns, it.rows, slots, kaneDouble)
                                }
                            }
                        }
                        it.copy(matrix = matrix)
                    }
                    it is ScalarAssign ->
                        it.dup(right = it.right.linearizeExpr(model) as ScalarExpr)
                    it is MatrixAssign -> {
                        it.right.elements.forEach { element ->
                            model.computeSlot(element) { element.linearizeExpr(model) as ScalarExpr }
                        }
                        it
                    }
                    it is ScalarVariable -> it
                    it is MatrixVariable -> it
                    else -> error("${it.javaClass}")
                }
            })
            // Assignments after everything else
            children.forEach {
                when (it) {
                    is NamedScalar -> {
                    }
                    is NamedMatrix -> {
                    }
                    is ScalarVariable -> {
                    }
                    is MatrixVariable -> {
                    }
                    is ScalarAssign -> {
                        when (it.right) {
                            is ConstantScalar -> model.assignBack(it.left.name, it.right)
                            else -> model.assignBack(it.left.name, model.knownSlot(it.right))
                        }
                    }
                    is MatrixAssign -> {
                        (it.left.elements zip it.right.elements).forEach { (left, right) ->
                            model.assignBack("$left", model.knownSlot(right))
                        }
                    }
                    is ScalarExpr -> {
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
    if (result is ScalarExpr && hasName()) {
        model.registerNamedScalar(name, result)
    }
    if (this is ScalarExpr && result is Slot) model.setValue(this, result)
    return result
}

private fun AlgebraicExpr.claimScalarVariables(model: LinearModel = LinearModel(kaneDouble)) {
    visit {
        when (it) {
            is ScalarAssign -> model.registerNamedScalarVariable(it.left)
            is ScalarVariable -> model.registerNamedScalarVariable(it)
            is RandomVariableExpr -> model.registerRandomVariable(it)
        }
    }
}


private fun AlgebraicExpr.claimMatrixVariables(model: LinearModel = LinearModel(kaneDouble)) {
    val seen = mutableSetOf<Id>()
    visit {
        if (it is MatrixVariableElement) {
            model.registerMatrixVariableElement(it)
        }
        if (it is MatrixAssign) {
            if (!seen.contains(it.name)) {
                seen += it.name
                it.left.elements.forEach { element ->
                    model.registerMatrixVariableElement(element)
                }
            }
        }
        if (it is MatrixVariable) {
            if (!seen.contains(it.name)) {
                seen += it.name
                it.elements.forEach { element ->
                    model.registerMatrixVariableElement(element)
                }
            }
        }
    }
}

private fun AlgebraicExpr.evalSpace(space : DoubleArray) : Double {
    return when (this) {
        is ConstantScalar -> value
        is Slot -> space[slot]
        is NamedScalar -> scalar.evalSpace(space)
        is AlgebraicBinaryScalarScalarScalar -> op.doubleOp(left.evalSpace(space), right.evalSpace(space))
        is AlgebraicUnaryScalarScalar -> op.doubleOp(value.evalSpace(space))
        is RetypeScalar -> getConstant()
        else ->
            error("$javaClass")
    }
}

private fun Expr.gatherNamedExprs(): Set<AlgebraicExpr> {
    val result = mutableSetOf<AlgebraicExpr>()
    visit { expr ->
        when {
            expr is AlgebraicExpr && expr.hasName() -> result += expr
            expr is Sheet -> {
                expr.cells.forEach { (name, cell) ->
                    when (cell) {
                        is NamedAlgebraicExpr -> result += cell
                        is AlgebraicExpr -> result += cell.toNamed(name)
                        else ->
                            error("${cell.javaClass}")
                    }
                }
            }
        }
    }
    return result
}

private fun AlgebraicExpr.stripNames() : AlgebraicExpr {
    fun ScalarExpr.self() = stripNames() as ScalarExpr
    fun MatrixExpr.self() = toDataMatrix().stripNames() as MatrixExpr
    return when (this) {
        is ConstantScalar -> this
        is MatrixVariableElement -> this
        is ScalarVariable -> this
        is MatrixVariable -> this
        is DiscreteUniformRandomVariable -> this
        is RetypeScalar -> scalar.self()
        is NamedMatrix -> matrix.self()
        is NamedScalar -> scalar.self()
        is AlgebraicSummaryScalarScalar -> dup(value = value)
        is AlgebraicUnaryScalarScalar -> {
            val value = value.self()
            if (this.value !== value) dup(value = value)
            else this
        }
        is AlgebraicBinaryScalarScalarScalar -> dup(left = left, right = right)
        is AlgebraicBinarySummaryScalarScalarScalar -> dup(left = left, right = right)
        is DataMatrix -> map { it.self() }
        is Tableau -> copy(children = children.map { child ->
            when (child) {
                is NamedScalar -> child.copy(scalar = child.scalar.self())
                is ScalarVariable -> child
                is MatrixVariable -> child
                is NamedMatrix -> child.copy(matrix = child.matrix.self())
                is MatrixAssign -> child.copy(right = child.right.self())
                is ScalarAssign -> child.dup(right = child.right.self())
                is ScalarExpr -> child.self().withNameOf(child)
                is MatrixExpr -> child.self().withNameOf(child)
                else ->
                    error("${child.javaClass}")
            }})

        else ->
            error("$javaClass")
    }
}

private fun Expr.terminal(): Boolean =
    canGetConstant() || when (this) {
        is Slot -> true
        is ScalarVariable -> true
        is MatrixVariableElement -> true
        is DiscreteUniformRandomVariable -> true
        is AlgebraicBinaryScalarScalarScalar -> false
        is AlgebraicUnaryScalarScalar -> false
        is RetypeScalar -> false
        else ->
            error("$javaClass")
    }

fun AlgebraicExpr.rollUpCommonSubexpressions(model : LinearModel) : AlgebraicExpr {
    return object : RewritingVisitor() {
        override fun rewrite(expr: AlgebraicUnaryScalarScalar): Expr {
            if (expr.value.terminal()) return (expr.linearizeExpr(model) as ScalarExpr).withNameOf(expr)
            return super.rewrite(expr)
        }

        override fun rewrite(expr: AlgebraicBinaryScalarScalarScalar): Expr {
            if (expr.left.terminal() && expr.right.terminal()) return (expr.linearizeExpr(model) as ScalarExpr).withNameOf(
                expr
            )
            return super.rewrite(expr)
        }

        override fun rewrite(expr: RetypeScalar): Expr {
            return super.rewrite(expr.scalar)
        }

        override fun rewrite(expr: SheetRangeExpr): Expr {
            return when (expr.rangeRef) {
                is CellRangeRef -> {
                    // done = false
                    expr
                }
                else ->
                    error("${expr.rangeRef.javaClass}")
            }
        }
    }.algebraic(this)
}

fun Expr.replaceSheetRanges(): Expr {
    return object : RewritingVisitor() {
        override fun rewrite(expr: CoerceScalar): Expr {
            if (expr.value is SheetRangeExpr) {
                if (
                    expr.value.rangeRef is CellRangeRef &&
                    expr.value.rangeRef.column is MoveableIndex &&
                    expr.value.rangeRef.row is MoveableIndex
                ) {
                    return ScalarVariable(
                        0.0,
                        expr.value.rangeRef.toCoordinate()
                    )
                }
                error("${expr.value.rangeRef.javaClass}")
            }

            return super.rewrite(expr)
        }
    }.rewrite(this)
}

fun Expr.linearize(): LinearModel {
    val names = replaceSheetRanges().gatherNamedExprs().toList().map { it.eval() }
    with(Tableau(names)) {
        val model = LinearModel(kaneDouble)
        claimMatrixVariables(model)
        claimScalarVariables(model)
        val stripped = stripNames()
        val rolledUp = stripped.rollUpCommonSubexpressions(model)
        rolledUp.linearizeExpr(model)
        return model
    }
}

fun ScalarExpr.toFunc(v1: ScalarVariable): (Double) -> Double {
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

fun LinearModel.toFunc(space: DoubleArray, v1: MatrixExpr, out: MatrixExpr): (Matrix) -> Matrix {
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