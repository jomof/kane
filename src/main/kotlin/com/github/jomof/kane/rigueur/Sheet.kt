package com.github.jomof.kane.rigueur

import com.github.jomof.kane.rigueur.Direction.*
import com.github.jomof.kane.rigueur.functions.*
import com.github.jomof.kane.rigueur.types.AlgebraicType
import java.lang.Integer.max
import kotlin.math.abs
import kotlin.reflect.KProperty

interface CellReferenceExpr : Expr

data class AbsoluteCellReferenceExpr(
    private val coordinate : Coordinate
) : CellReferenceExpr {
    init { track() }
    override fun toString() = coordinateToCellName(coordinate)
}

data class UntypedRelativeCellReference(val offset : Coordinate) : UntypedScalar {
    init { track() }
    override fun toString() = "ref$offset"
    fun up(move : Int = 1) = UntypedRelativeCellReference(offset + Coordinate(column = 0, row = -move))
    fun down(move : Int = 1) = UntypedRelativeCellReference(offset + Coordinate(column = 0, row = move))
    fun left(move : Int = 1) = UntypedRelativeCellReference(offset + Coordinate(column = -move, row = 0))
    fun right(move : Int = 1) = UntypedRelativeCellReference(offset + Coordinate(column = move, row = 0))
    val up get() = up()
    val down get() = down()
    val left get() = left()
    val right get() = right()
    operator fun getValue(nothing: Nothing?, property: KProperty<*>) = run {
        val name = property.name.toUpperCase()
        val source = cellNameToCoordinate(name)
        NamedUntypedAbsoluteCellReference(name, source + offset)
    }

    fun toAbsolute(source : Coordinate) = AbsoluteCellReferenceExpr(source + offset)
}

data class NamedUntypedAbsoluteCellReference(
    override val name : String,
    val coordinate : Coordinate) : UntypedScalar, NamedExpr {
    init { track() }
    override fun toString() = "$name=${coordinateToCellName(coordinate)}"
}

data class CoerceScalar<T:Number>(
    val value : Expr,
    override val type : AlgebraicType<T>
) : ScalarExpr<T> {
    init {
        assert(value !is CoerceScalar<*>) {
            "coerce of coerce?"
        }
        assert(value !is TypedExpr<*> || type != value.type) {
            "coerce to same type? ${type.simpleName}"
        }
        track()
    }
    override fun toString() = "$value"
    fun mapChildren(f: ExprFunction) : ScalarExpr<T> {
        val value = f(value)
        return if (value !== this.value) copy(value = value)
        else this
    }
}

interface Sheet {
    val cells : Map<String, Expr>
    val columns : Int
    val rows : Int
    operator fun get(cell : String) = cells[cell]
}

data class EmptySheet(
    override val cells: Map<String, Expr> = mapOf(),
    override val columns: Int = 0,
    override val rows: Int = 0) : Sheet

data class SheetImpl(
    override val cells: Map<String, Expr>,
) : Sheet {
    override val columns : Int = cells.map { cellNameToCoordinate(it.key).column }.maxOrNull()!! + 1
    override val rows : Int = cells.map { cellNameToCoordinate(it.key).row }.maxOrNull()!! + 1
    override fun toString() = render()
}

class SheetBuilder {
    private val added : MutableList<NamedExpr> = mutableListOf()
    fun up(offset : Int = 1) = UntypedRelativeCellReference(Coordinate(column = 0, row = -offset))
    fun down(offset : Int = 1) = UntypedRelativeCellReference(Coordinate(column = 0, row = offset))
    fun left(offset : Int = 1) = UntypedRelativeCellReference(Coordinate(column = -offset, row = 0))
    fun right(offset : Int = 1) = UntypedRelativeCellReference(Coordinate(column = offset, row = 0))
    val up get() = up()
    val down get() = down()
    val left get() = left()
    val right get() = right()

    private fun getImmediateNamedExprs() : Map<String, NamedExpr> {
        val cells = mutableMapOf<String, NamedExpr>()

        // Add immediate named expressions
        added.forEach { namedExpr ->
            when(namedExpr) {
                is NamedExpr -> cells[namedExpr.name] = namedExpr
                else ->
                    error("${namedExpr.javaClass}")
            }
        }

        return cells
    }

    private fun getEmbeddedNamedExprs(cells : Map<String, NamedExpr>) : Map<String, NamedExpr> {
        val result = cells.toMutableMap()
        var done = false
        while(!done) {
            done = true
            result.values.toList().forEach {
                it.visit { child ->
                    when(child) {
                        is NamedExpr -> {
                            if (!result.containsKey(child.name)) {
                                done = false
                                result[child.name] = child
                            }
                        }
                    }
                }
            }
        }
        return result
    }

    private fun replaceRelativeReferences(cells : Map<String, NamedExpr>) : Map<String, NamedExpr> {
        return cells
            .map { (name, expr) -> expr.replaceRelativeCellReferences() }
            .map { it.name to it }.toMap()
    }

    private fun convertCellNamesToUpperCase(cells : Map<String, NamedExpr>) : Map<String, NamedExpr> {
        return cells
            .map { (name, expr) -> expr.replaceExpr {
                when(it) {
                    is NamedExpr -> {
                        val upper = it.name.toUpperCase()
                        if (looksLikeCellName(upper)) {
                            when (it) {
                                is NamedMatrix<*> -> it.copy(name = upper)
                                is NamedScalar<*> -> it.copy(name = upper)
                                is NamedValueExpr<*> -> it.copy(name = upper)
                                is NamedUntypedAbsoluteCellReference -> it.copy(name = upper)
                                else -> error("${it.javaClass}")
                            }
                        } else it
                    }
                    else -> it
                }
            } as NamedExpr }
            .map { it.name to it }.toMap()
    }

    private fun <E:Number> extractScalarizedMatrixElement(
        matrix : MatrixExpr<E>,
        coordinate : Coordinate) : ScalarExpr<E> {
        return when(matrix) {
            is AlgebraicBinaryMatrixScalar -> {
                val left = extractScalarizedMatrixElement(matrix.left, coordinate)
                AlgebraicBinaryScalar(matrix.op, left, matrix.right)
            }
            is AlgebraicBinaryScalarMatrix -> {
                val right = extractScalarizedMatrixElement(matrix.right, coordinate)
                AlgebraicBinaryScalar(matrix.op, matrix.left, right)
            }
            is AlgebraicBinaryMatrix -> {
                val left = extractScalarizedMatrixElement(matrix.left, coordinate)
                val right = extractScalarizedMatrixElement(matrix.right, coordinate)
                AlgebraicBinaryScalar(matrix.op, left, right)
            }
            is AlgebraicUnaryMatrix -> {
                val value = extractScalarizedMatrixElement(matrix.value, coordinate)
                AlgebraicUnaryScalar(matrix.op, value)
            }
            is NamedMatrix -> {
                assert(looksLikeCellName(matrix.name))
                val baseCoordinate = cellNameToCoordinate(matrix.name)
                val offsetCoordinate = baseCoordinate + coordinate
                val offsetCellName = coordinateToCellName(offsetCoordinate)
                val unnamed = matrix[coordinate]
                NamedScalar(name = offsetCellName, unnamed)
            }
            is DataMatrix -> matrix[coordinate]
            else ->
                error("${matrix.javaClass}")
        }
    }

    private fun scalarizeMatrixes(cells : Map<String, NamedExpr>) : Map<String, NamedExpr> {
        val result = cells.toMutableMap()
        var done = false
        while(!done) {
            done = true
            result.toList().forEach { (name, expr) ->
                if (expr is NamedMatrix<*> && looksLikeCellName(name)) {
                    val upperLeft = cellNameToCoordinate(name)
                    expr.matrix.coordinates.map { offset ->
                        val finalCoordinate = upperLeft + offset
                        val finalCellName = coordinateToCellName(finalCoordinate)
                        val extracted = extractScalarizedMatrixElement(expr.matrix, offset)
                        result[finalCellName] = NamedScalar(finalCellName, extracted)
                    }
                    done = false
                }
            }
        }

        return result
    }


    private fun expandUnaryOperationMatrixes(cells : Map<String, NamedExpr>) : Map<String, NamedExpr> {
        return cells
            .map { (name, expr) -> expr.replaceExpr {
                when(it) {
                    is AlgebraicUnaryMatrixScalar<*> -> {
                        val data = DataMatrix(it.value.columns, it.value.rows, it.value.coordinates.map { offset ->
                            val extracted = extractScalarizedMatrixElement(it.value, offset)
                            extracted as ScalarExpr<*>
                        })
                        val reduced = it.op.reduceArithmetic(data) as ScalarExpr<*>
                        reduced
                    }
                    else -> it
                }
            } as NamedExpr }
            .map { it.name to it }.toMap()
    }

    private fun splitNames(cells : Map<String, Expr>) : Map<String, Expr> {
        return cells
            .map { (name, expr) ->
                name to when(expr) {
                    is NamedScalar<*> -> when(expr.scalar) {
                        is NamedScalar<*> -> expr.scalar
                        is NamedExpr -> error("${expr.scalar.javaClass}")
                        else -> expr.scalar
                    }
                    is NamedExpr -> error("${expr.javaClass}")
                    else ->
                        expr
                }
            }
            .toMap()
    }

//    private fun Expr.replaceNamedScalarWithCellReference(outerName : String) : Expr {
//        fun <E:Number> ScalarExpr<E>.self() = replaceNamedScalarWithCellReference(outerName) as ScalarExpr<E>
//        return when {
//            this is NamedScalar<*> && name != outerName ->
//                CoerceScalar(AbsoluteCellReferenceExpr(cellNameToCoordinate(name)), type)
//            this is NamedScalar<*> -> copy(scalar = )
//            else -> error("$javaClass")
//        }
//    }

    private fun replaceNamesWithCellReferences(cells : Map<String, NamedExpr>) : Map<String, Expr> {
        return cells
            .map { (name, expr) ->
                name to when(expr) {
                    is NamedScalar<*> -> {
                        val replaced = expr.replaceExpr {
                            when {
                                it is NamedScalar<*> && it.name != name ->
                                    CoerceScalar(AbsoluteCellReferenceExpr(cellNameToCoordinate(it.name)), it.type)
                                else -> it
                            }
                        } as ScalarExpr<*>
                        replaced
                    }
                    is NamedValueExpr<*> -> expr.toValueExpr()
                    is NamedUntypedAbsoluteCellReference -> AbsoluteCellReferenceExpr(expr.coordinate)
                    else -> error("${expr.javaClass}")
                }
            }
            .toMap()
    }

    fun build() : Sheet {
        if (added.isEmpty()) return EmptySheet()
        val immediate = getImmediateNamedExprs()
        val withEmbedded = getEmbeddedNamedExprs(immediate)
        val upperCased = convertCellNamesToUpperCase(withEmbedded)
        val scalarized = scalarizeMatrixes(upperCased)
        val relativeReferencesReplaced = replaceRelativeReferences(scalarized)
        val unaryMatrixesExpanded = expandUnaryOperationMatrixes(relativeReferencesReplaced)
        val namesReplaced = replaceNamesWithCellReferences(unaryMatrixesExpanded)
        val splitNamed = splitNames(namesReplaced)
        return SheetImpl(splitNamed)
    }

    fun add(vararg add : NamedExpr) {
        added += add
    }
}

fun Sheet.eval() : Sheet {
    val cells = cells.toMutableMap()

    // Replace variables with their initial value
    cells.forEach { (cell, expr) ->
        if (expr is ScalarVariable<*>) {
            cells[cell] = constant(expr.initial, expr.type)
        }
    }

    // Expand cell references
    var done = false
    var remainingDepth = 5
    while(!done && remainingDepth > 0) {
        done = true
        --remainingDepth
        if (remainingDepth == 0) break
        for ((cell, expr) in cells) {
            val replaced = expr.replaceExpr(TOP_DOWN) {
                when {
                    it is CoerceScalar<*> -> {
                        when(it.value) {
                            is CellReferenceExpr -> {
                                val tag = "${it.value}"
                                val result = when(val lookup = cells[tag]) {
                                    null -> it
                                    is AbsoluteCellReferenceExpr -> it
                                    is TypedExpr<*> -> {
                                        if (lookup.type == it.type) lookup
                                        else (CoerceScalar(lookup, it.type))
                                    }
                                    else -> error("${lookup.javaClass}")
                                }
                                result
                            }
                            else -> it
                        }
                    }
                    it is AbsoluteCellReferenceExpr -> it
                    it is CellReferenceExpr ->
                        error("Not covered by coerce above?")
                    else -> it
                }
            }
            cells[cell] = replaced
        }
    }

    if (remainingDepth == 0) return this

    // Reduce arithmetic
    cells.forEach { (cell, expr) ->
        cells[cell] = when(expr) {
            is AlgebraicExpr<*> -> expr.memoizeAndReduceArithmetic()
            else -> expr
        }
    }
    return SheetImpl(cells)
}

fun Sheet.minimize(
    target : String,
    variables : List<String>) : Sheet {
    val resolvedVariables = mutableMapOf<String, NamedScalarVariable<Double>>()

    // Turn each 'variable' into a NamedScalarVariable
    variables.forEach { cell ->
        val reduced = when(val value = cells.getValue(cell)) {
            is AlgebraicExpr<*> ->
                value.memoizeAndReduceArithmetic()
            else ->
                error("${value.javaClass}")
        }
        val variable = when(reduced) {
            is ConstantScalar -> NamedScalarVariable(cell, reduced.value as Double, reduced.type as AlgebraicType<Double>)
            is ScalarVariable -> NamedScalarVariable(cell, reduced.initial as Double, reduced.type as AlgebraicType<Double>)
            else -> error("$cell (${cells.getValue(cell)}) is not a constant")
        }
        resolvedVariables[cell] = variable
    }

    // Follow all expressions from 'target'
    println("Expanding target expression")
    var targetExpr = cells.getValue(target)
    var done = false
    while(!done) {
        done = true
        val next = targetExpr.replaceExpr(TOP_DOWN) {
            when(it) {
                is CoerceScalar<*> -> {
                    when(it.value) {
                        is ScalarVariable<*> -> {
                            // This variable is not being optimized so treat it as constant
                            constant(it.value.initial)
                        }
                        is ScalarExpr<*> -> it.value
                        is AbsoluteCellReferenceExpr -> {
                            val ref = "$it"
                            val result = resolvedVariables[ref]?:cells.getValue(ref)
                            done = false
                            if (result is TypedExpr<*> && result.type == it.type) result
                            else it.copy(value = result)
                        }
                        else -> error("${it.value.javaClass}")
                    }
                }
                is AbsoluteCellReferenceExpr -> {
                    val ref = "$it"
                    val result = resolvedVariables[ref]?:cells.getValue(ref)
                    done = false
                    result
                }
                else -> it
            }
        } as ScalarExpr<Double>
        targetExpr = next
    }
    if (targetExpr !is ScalarExpr<*>) {
        error("'$target' was not a numeric expression")
    }
    val reduced = (targetExpr as ScalarExpr<Double>).reduceArithmetic()
    val namedTarget : NamedScalarExpr<Double> = NamedScalar(target, reduced)

    // Differentiate target with respect to each variable
    println("Differentiating target expression with respect to change variables")
    val diffs = resolvedVariables.map { (name, variable) ->
        val name = "d$target/d${variable.name}"
        val derivative = differentiate(d(namedTarget)/d(variable)).reduceArithmetic()
        NamedScalar(name, derivative)
    }

    // Assign back variables updated by a delta of their respective derivative
    val assignBacks = (resolvedVariables.values zip diffs).map { (variable, diff) ->
        NamedScalarAssign("update(${variable.name})", variable, variable - multiply(0.01, diff)).reduceArithmetic()
    }

    // Create the model, allocate space for it, and iterate. Break when the target didn't move much
    println("Linearizing equations")
    val model = Tableau(resolvedVariables.values + diffs + namedTarget + assignBacks).linearize()
    val space = model.allocateSpace()
    var priorTarget = model.shape(namedTarget).ref(space).value
    println("Minimizing")
    for(i in 0 until 100000) {
        val sb = StringBuilder()
        sb.append(resolvedVariables.values.joinToString(" ") {
            val value = model.shape(it).ref(space)
            "${it.name}: $value"
        })
        model.eval(space)
        val newTarget = model.shape(namedTarget).ref(space).value
        if (abs(newTarget-priorTarget) < 0.0000000000001)
            break
        priorTarget = newTarget
    }

    // Extract the computed values and plug them back into a new sheet
    val new = cells.toMutableMap()
    resolvedVariables.values.forEach {
        val value = model.shape(it).ref(space)
        assert(new.containsKey(it.name))
        new[it.name] = variable(value.value, (cells[it.name] as AlgebraicExpr<Double>).type)
    }
    return SheetImpl(new)
}

fun Sheet.render() : String {
    if (cells.isEmpty()) return ""
    val sb = StringBuilder()
    val widths = Array(columns + 1) { 0 }


    // Calculate widths
    for(row in 0 until rows) {
        widths[0] = max(widths[0], "$row".length)
        for(column in 0 until columns) {
            val cell = coordinateToCellName(column, row)
            val value = cells[cell]?.toString() ?: ""
            widths[column + 1] = max(widths[column + 1], value.length)
            widths[column + 1] = max(widths[column + 1], indexToColumnName(column).length)
        }
    }

    // Column headers
    widths.indices.forEach { index ->
        val width = widths[index]
        if (index == 0) sb.append(" ".repeat(width) + " ")
        else sb.append(padCenter(indexToColumnName(index - 1), width) + " ")
    }
    sb.append("\n")

    // Dashes headers
    widths.indices.forEach { index ->
        val width = widths[index]
        if (index == 0) sb.append(" ".repeat(width) + " ")
        else sb.append("-".repeat(widths[index]) + " ")
    }
    sb.append("\n")

    // Data
    for(row in 0 until rows) {
        sb.append(padLeft("${row+1}", widths[0]) + " ")
        for(column in 0 until columns) {
            val cell = coordinateToCellName(column, row)
            val value = cells[cell]?.toString() ?: ""
            sb.append(padLeft(value, widths[column + 1]) + " ")
        }
        if (row != rows - 1) sb.append("\n")
    }
    return "$sb"
}

// Construction
fun sheetOf(init : SheetBuilder.() -> Unit) : Sheet {
    val sheet = SheetBuilder()
    init(sheet)
    return sheet.build()
}


// Cell naming
fun coordinateToCellName(column : Int, row : Int) : String {
    if (column < 0) return "#REF!"
    if (row < 0) return "#REF!"
    return "${indexToColumnName(column)}${row+1}"
}

fun coordinateToCellName(coord : Coordinate) = coordinateToCellName(coord.column, coord.row)

fun columnNameToIndex(column : String) : Int {
    var result = 0
    for(c in column) {
        assert(c in 'A'..'Z')
        result *= 26
        result += (c - 'A' + 1)
    }
    return result - 1
}


fun indexToColumnName(column : Int) : String {
    assert(column >= 0)
    return when (column) {
        in 0..25 -> ('A' + column).toString()
        else -> indexToColumnName(column / 26 - 1) + indexToColumnName(column % 26)
    }
}

fun looksLikeCellName(tag : String) : Boolean {
    if (tag.length < 2) return false
    if (tag[0] !in 'A'..'Z') return false
    if (tag.last() !in '0'..'9') return false
    return true
}

fun cellNameToCoordinate(tag : String) : Coordinate {
    assert(looksLikeCellName(tag))
    val column = StringBuilder()
    val row = StringBuilder()
    for(i in tag.indices) {
        val c = tag[i]
        if (c !in 'A'..'Z') break
        column.append(c)
    }
    for(i in column.length until tag.length) {
        val c = tag[i]
        assert(c in '0'..'9') {
            error("Could not convert $tag to a cell name")
        }
        row.append(c)
    }
    assert(row.isNotEmpty()) {
        "Cell name '$tag' did not have a number row part"
    }
    return Coordinate(
        column = columnNameToIndex(column.toString()),
        row = row.toString().toInt() - 1
    )
}



private fun <E:Number> MatrixExpr<E>.replaceRelativeCellReferencesInMatrix(coordinate : Coordinate) : MatrixExpr<E> {
   return replaceRelativeCellReferences(coordinate, type) as MatrixExpr<E>
}
private fun <E:Number> ScalarExpr<E>.replaceRelativeCellReferencesInScalar(coordinate : Coordinate) : ScalarExpr<E> {
    return replaceRelativeCellReferences(coordinate, type) as ScalarExpr<E>
}

private fun Expr.replaceRelativeCellReferences(coordinate : Coordinate, type : AlgebraicType<*>) : Expr =
    replaceExpr(TOP_DOWN) {
        when(it) {
            is NamedMatrix<*> -> {
                val coordinate = cellNameToCoordinate(it.name.toUpperCase())
                NamedMatrix(name = it.name, matrix = it.matrix.replaceRelativeCellReferencesInMatrix(coordinate))
            }
            is NamedScalar<*> -> {
                val coordinate = cellNameToCoordinate(it.name.toUpperCase())
                NamedScalar(name = it.name, scalar =  it.scalar.replaceRelativeCellReferencesInScalar(coordinate))
            }
            is CoerceScalar<*> ->
                when(it.value) {
                    is UntypedRelativeCellReference -> {
                        CoerceScalar(AbsoluteCellReferenceExpr(coordinate + it.value.offset), type)
                    }
                    is AbsoluteCellReferenceExpr -> it
                    else -> error("${it.value.javaClass}")
                }

            is UntypedRelativeCellReference ->
                // Should be caught when there is a type context
                error("${it.javaClass}")
            else -> it
        }
    }

private fun Expr.replaceRelativeCellReferences() : Expr {
    return replaceExpr(TOP_DOWN) {
        when(it) {
            is NamedMatrix<*> -> {
                val coordinate = cellNameToCoordinate(it.name.toUpperCase())
                it.replaceRelativeCellReferences(coordinate, it.type)
            }
            is NamedScalar<*> -> {
                val coordinate = cellNameToCoordinate(it.name.toUpperCase())
                it.replaceRelativeCellReferences(coordinate, it.type)
            }
            is UntypedRelativeCellReference ->
                error("untyped relative cell reference")
            else -> it
        }
    }
}

private fun NamedExpr.replaceRelativeCellReferences() : NamedExpr =
    (this as Expr).replaceRelativeCellReferences() as NamedExpr
