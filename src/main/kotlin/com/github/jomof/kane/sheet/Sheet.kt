package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.functions.*
import com.github.jomof.kane.types.AlgebraicType
import com.github.jomof.kane.types.DoubleAlgebraicType
import com.github.jomof.kane.types.KaneType
import com.github.jomof.kane.visitor.RewritingVisitor
import com.github.jomof.kane.visitor.visit
import java.lang.Integer.max
import kotlin.math.abs
import kotlin.math.min
import kotlin.reflect.KProperty

data class SheetRangeExpr(val range: SheetRange) : UntypedScalar {
    private val name by lazy { range.toString() }
    fun up(move: Int) = copy(range = range.up(move))
    fun down(move: Int) = copy(range = range.down(move))
    fun left(move: Int) = copy(range = range.left(move))
    fun right(move: Int) = copy(range = range.right(move))
    val up get() = up(1)
    val down get() = down(1)
    val left get() = left(1)
    val right get() = right(1)
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toString() = name
}

data class NamedSheetRangeExpr(
    override val name: Id,
    val range: SheetRangeExpr
) : UntypedScalar, NamedExpr {
    init {
        track()
        assert(name != "increasing" || range.toString() != "Brow-1") {
            "Should have stripped name?"
        }
    }

    override fun toString() = render()

    fun up(move: Int) = range.up(move)
    fun down(move: Int) = range.down(move)
    fun left(move: Int) = range.left(move)
    fun right(move: Int) = range.right(move)
    val up get() = up(1)
    val down get() = down(1)
    val left get() = left(1)
    val right get() = right(1)
}

data class CoerceScalar(
    val value: Expr,
    override val type: AlgebraicType
) : ScalarExpr {
    init {
        assert(value !is ScalarExpr) {
            "coerce of scalar?"
        }
        assert(value !is CoerceScalar) {
            "coerce of coerce?"
        }
        assert(value !is TypedExpr<*> || type != value.type) {
            "coerce to same type? ${type.simpleName}"
        }
        track()
    }

    override fun toString() = render()
    fun copy(value: Expr): ScalarExpr {
        return when {
            value === this.value -> this
            value is ScalarExpr -> value
            else -> CoerceScalar(value, type)
        }
    }
}

data class ColumnDescriptor(val name: Id, val type: AdmissibleDataType<*>?)
data class RowDescriptor(val name: List<Expr>)
data class SheetDescriptor(val limitOutputLines: Int = Int.MAX_VALUE)

data class Cells(private val map: Map<Id, Expr>) {
    init {
        map.forEach { (id, expr) -> Identifier.validate(id) }
    }

    fun <R : Any> mapNotNull(transform: (Map.Entry<Id, Expr>) -> R?): List<R> {
        return map.mapNotNullTo(ArrayList<R>(), transform)
    }

    fun <R : Any> map(transform: (Map.Entry<Id, Expr>) -> R): List<R> {
        return map.map(transform)
    }

    fun filter(predicate: (Map.Entry<Id, Expr>) -> Boolean) = map.filter(predicate).toCells()
    fun mapExprs(transform: (Id, Expr) -> Expr): Cells {
        val mutable = toMutableMap()
        map.forEach { (id, expr) ->
            mutable[id] = transform(id, expr)
        }
        return mutable.toCells()
    }

    fun mapExprsNotNull(transform: (Id, Expr) -> Expr?): Cells {
        val mutable = toMutableMap()
        map.forEach { (id, expr) ->
            val result = transform(id, expr)
            if (result != null) mutable[id] = result
        }
        return mutable.toCells()
    }

    fun forEach(action: (Map.Entry<Id, Expr>) -> Unit) {
        return map.forEach(action)
    }

    fun toMutableMap() = map.toMutableMap()
    fun toMap() = map
    operator fun iterator() = map.iterator()
    fun isEmpty() = map.isEmpty()
    fun isNotEmpty() = map.isNotEmpty()
    fun getValue(key: Id) = get(key) ?: error("No cell named '$key' in cell map")
    operator fun get(key: Id): Expr? {
        Identifier.validate(key)
        return map[key]
    }

    infix operator fun <E : Expr> plus(right: Map<Id, E>): Cells = (toMap() + right).toCells()
}

fun List<Pair<Id, Expr>>.toCells() = Cells(toMap())
fun Map<Id, Expr>.toCells() = Cells(this)

interface Sheet : Expr {
    val columnDescriptors: Map<Int, ColumnDescriptor>
    val rowDescriptors: Map<Int, RowDescriptor>
    val cells: Cells
    val sheetDescriptor: SheetDescriptor
    val columns: Int
    val rows: Int

    companion object {
        private data class SheetImpl(
            override val columnDescriptors: Map<Int, ColumnDescriptor>,
            override val rowDescriptors: Map<Int, RowDescriptor>,
            override val cells: Cells,
            override val sheetDescriptor: SheetDescriptor,
            override val columns: Int,
            override val rows: Int
        ) : Sheet {
            override fun toString() = render()
        }

        private val emptySheet = SheetImpl(mapOf(), mapOf(), Cells(mapOf()), SheetDescriptor(), 0, 0)
        fun of(
            columnDescriptors: Map<Int, ColumnDescriptor>,
            rowDescriptors: Map<Int, RowDescriptor>,
            sheetDescriptor: SheetDescriptor,
            cells: Cells
        ): Sheet {
            if (cells.isEmpty() &&
                columnDescriptors.isEmpty() &&
                rowDescriptors.isEmpty() &&
                sheetDescriptor == SheetDescriptor()
            ) return emptySheet
            if (cells.isEmpty()) return SheetImpl(columnDescriptors, rowDescriptors, cells, sheetDescriptor, 0, 0)
            val columnsFromCells: Int = cells
                .toMap()
                .filter { it.key is Coordinate }
                .map { Identifier.column(it.key) }.maxOrNull()!! + 1
            val columnsColumnDescriptors: Int = columnDescriptors
                .map { it.key }.maxOrNull()?:0 + 1
            val rows : Int = cells
                .toMap()
                .filter { it.key is Coordinate }
                .map { Identifier.row(it.key) }.maxOrNull()!! + 1
            return SheetImpl(columnDescriptors, rowDescriptors, cells, sheetDescriptor, max(columnsFromCells, columnsColumnDescriptors), rows)
        }
    }

    fun copy(
        columnDescriptors: Map<Int, ColumnDescriptor> = this.columnDescriptors,
        rowDescriptors: Map<Int, RowDescriptor> = this.rowDescriptors,
        cells: Cells = this.cells,
        sheetDescriptor: SheetDescriptor = this.sheetDescriptor
    ) = of(columnDescriptors, rowDescriptors, sheetDescriptor, cells)

    /**
     * Limit the number of output lines when rendering the sheet
     */
    fun limitOutputLines(limit: Int) = copy(sheetDescriptor = sheetDescriptor.copy(limitOutputLines = limit))

    /**
     * Create a new sheet with cell values changed.
     */
    fun copy(vararg assignments: Pair<String, Any>): Sheet {
        val sb = toBuilder()
        assignments.forEach { (name, any) ->
            sb.add(convertAnyToNamedExpr(Identifier.normalizeUserInput(name), any))
        }
        return sb.build()
    }

    /**
     * Create a new sheet with cell values changed.
     */
    fun copy(init: SheetBuilderImpl.() -> List<NamedExpr>): Sheet {
        val sheet = toBuilder()
        init(sheet).forEach { sheet.add(it) }
        return sheet.build()
    }


    fun toBuilder(): SheetBuilderImpl {
        val named = cells.map { (name, expr) -> expr.toNamed(name) }
        return SheetBuilderImpl(
            columnDescriptors = columnDescriptors,
            rowDescriptors = rowDescriptors,
            sheetDescriptor = sheetDescriptor,
            added = named
        )
    }
}

class SheetBuilderImpl(
    columnDescriptors: Map<Int, ColumnDescriptor> = mapOf(),
    rowDescriptors: Map<Int, RowDescriptor> = mapOf(),
    val sheetDescriptor: SheetDescriptor = SheetDescriptor(),
    added: List<NamedExpr> = listOf()
) : SheetBuilder {
    internal val columnDescriptors: MutableMap<Int, ColumnDescriptor> = columnDescriptors.toMutableMap()
    private val rowDescriptors: MutableMap<Int, RowDescriptor> = rowDescriptors.toMutableMap()
    private val added: MutableList<NamedExpr> = added.toMutableList()

    override fun column(range: String): SheetBuilderRange {
        val parsed = parseRange(range)
        assert(parsed is ColumnRange || parsed is NamedColumnRange)
        return SheetBuilderRange(this, parsed)
    }

    fun getImmediateNamedExprs(): Cells {
        val cells = mutableMapOf<Id, Expr>()

        // Add immediate named expressions
        added.forEach { namedExpr ->
            cells[Identifier.normalizeUserInput(namedExpr.name)] = namedExpr.toUnnamed()
        }

        return cells.toCells()
    }

    private fun getEmbeddedNamedExprs(cells: Cells): Cells {
        val result = cells.toMutableMap()
        var done = false
        while (!done) {
            done = true
            result.values.toList().forEach {
                it.visit { child ->
                    when (child) {
                        is NamedExpr -> {
                            if (!result.containsKey(child.name)) {
                                done = false
                                result[child.name] = child.toUnnamed()
                            }
                        }
                    }
                }
            }
        }
        return result.toCells()
    }

    private fun replaceRelativeReferences(cells: Cells): Cells {
        return cells
            .map { (name, expr) ->
                val base = if (name is Coordinate) name else coordinate(0, 0)
                name to expr.replaceRelativeCellReferences(base)
            }
            .toCells()
    }

    private fun convertCellNamesToUpperCase(cells: Cells): Cells {
        return cells
            .map { (name, expr) ->
                val upper = Identifier.string(name).toUpperCase()
                val new = if (looksLikeCellName(upper)) cellNameToCoordinate(upper) else name
                new to expr.convertCellNamesToUpperCase()
            }
            .map { it.first to it.second }
            .toCells()
    }

    private fun scalarizeMatrixes(cells: Cells): Cells {
        val result = cells.toMutableMap()
        var done = false
        while (!done) {
            done = true
            result.toList().forEach { (name, expr) ->
                if (!(name is Coordinate)) {
                    when (expr) {
                        is MatrixExpr,
                        is Tiling<*> -> {
                            done = false
                            val cellsCoordinates =
                                result.map { it.key }.filter { it is Coordinate }.map { Identifier.coordinate(it) }
                            val columns = cellsCoordinates.map { it.column }.toSet()
                            val existingColumns = (columnDescriptors.keys + columns).sorted()
                            done = false
                            for (next in 0 until Int.MAX_VALUE) {
                                if (!existingColumns.contains(next)) {
                                    columnDescriptors[next] = ColumnDescriptor(name, null)
                                    val allocatedColumnBase = coordinate(next, 0)
                                    result[allocatedColumnBase] = expr
                                    result.remove(name)
                                    break
                                }
                            }
                        }
                    }
                } else {
                    when (expr) {
                        is ScalarExpr,
                        is ValueExpr<*>,
                        is SheetRangeExpr,
                        is AlgebraicBinaryRangeStatistic -> {
                        }
                        is MatrixExpr -> {
                            var replacedOurName = false
                            expr.coordinates.map { offset ->
                                val finalCoordinate = name + offset
                                val extracted = extractScalarizedMatrixElement(expr, offset)
                                    .slideScalarizedCellsRewritingVisitor(name, offset)
                                if (finalCoordinate == name) {
                                    replacedOurName = true
                                }
                                result[finalCoordinate] = extracted
                            }
                            assert(replacedOurName) {
                                "Should have replaced our own name in $name"
                            }
                            done = false
                        }
                        is Tiling<*> -> {
                            coordinatesOf(expr.columns, expr.rows).map { offset ->
                                val finalCoordinate = name + offset
                                result[finalCoordinate] = expr.getUnnamedElement(offset)
                            }
                        }
                        else -> error("${expr.javaClass}")
                    }
                }
            }
        }

        return result.toCells()
    }

    private fun expandUnaryOperations(cells: Cells) = cells
        .mapExprs { _, expr ->
            expr.expandUnaryOperations()
        }

    private fun replaceNamesWithCellReferences(cells: Cells) = cells
        .mapExprs { name, expr ->
            expr.replaceNamesWithCellReferences(name)
        }

    fun build(): Sheet {
        val immediate = getImmediateNamedExprs()
        val debuilderized = removeBuilderPrivateExpressions(immediate)
        val noNamedColumns = convertNamedColumnsToColumnRanges(debuilderized)
        val withEmbedded = getEmbeddedNamedExprs(noNamedColumns)
        val upperCased = convertCellNamesToUpperCase(withEmbedded)
        val scalarized = scalarizeMatrixes(upperCased)
        val scalarizeRanges = scalarizeRanges(scalarized)
        val relativeReferencesReplaced = replaceRelativeReferences(scalarizeRanges)
        val unaryMatrixesExpanded = expandUnaryOperations(relativeReferencesReplaced)
        val namesReplaced = replaceNamesWithCellReferences(unaryMatrixesExpanded)
        namesReplaced.forEach { (name, expr) ->
            assert(name is Coordinate || expr !is SheetRangeExpr) {
                "Should have eliminated $name"
            }
        }
        return Sheet.of(columnDescriptors, rowDescriptors, sheetDescriptor, namesReplaced)
    }

    internal fun add(vararg add: NamedExpr) {
        added += add
    }

    fun set(cell : Coordinate, value : Any, type : KaneType<*>) {
        val name = CellRange.moveable(cell).toString()
        add(
            if (value is Double) NamedScalar(cell, constant(value, type as AlgebraicType))
            else NamedValueExpr(cell, value, type as KaneType<Any>)
        )
    }

    fun column(index: Int, name: String, type: AdmissibleDataType<*>? = null) {
        columnDescriptors[index] = ColumnDescriptor(name, type)
    }

    override fun nameColumn(column: Int, name: Id) {
        columnDescriptors[column] = ColumnDescriptor(name, null)
    }

    override fun nameRow(row: Int, name: Id) {
        rowDescriptors[row] = RowDescriptor(listOf(constant(Identifier.string(name))))
    }

    override fun nameRow(row: Int, name: List<Expr>) {
        rowDescriptors[row] = RowDescriptor(name)
    }
}


private fun Sheet.variable(cell: Id): NamedScalarVariable {
    val reduced = when (val value = cells.getValue(cell)) {
        is AlgebraicExpr -> value.eval()
        else ->
            error("${value.javaClass}")
    }
    return when (reduced) {
        is ConstantScalar -> NamedScalarVariable(cell, reduced.value, reduced.type)
        is ScalarVariable -> NamedScalarVariable(cell, reduced.initial, reduced.type)
        else -> error("$cell (${cells.getValue(cell)}) is not a constant")
    }
}

private fun Sheet.replaceNamesWithVariables(variables: Map<Id, NamedScalarVariable>): Sheet {
    return object : RewritingVisitor() {
        override fun rewrite(expr: NamedScalar): Expr {
            return variables[expr.name] ?: super.rewrite(expr)
        }
    }.rewrite(this) as Sheet
}

fun Sheet.minimize(
    target: String,
    variables: List<String>,
    learningRate: Double = 0.001
): Sheet {
    val resolvedVariables = mutableMapOf<Id, NamedScalarVariable>()
    val variableIds = variables.map { Identifier.normalizeUserInput(it) }

    // Turn each 'variable' into a NamedScalarVariable
    variableIds.forEach { cell ->
        resolvedVariables[cell] = variable(cell)
    }

    // Replace names with variables
    val namesReplaced = replaceNamesWithVariables(resolvedVariables)

    // Follow all expressions from 'target'
    val reducedArithmetic = namesReplaced.eval(
        reduceVariables = true,
        excludeVariables = variableIds.toSet()
    )
    //val reducedArithmetic = reduceArithmetic(variables.toSet())
    println("Expanding target expression '$target'")
    val lookup = reducedArithmetic.cells + resolvedVariables
    val targetId = Identifier.normalizeUserInput(target)
    val targetExpr = reducedArithmetic.cells.getValue(targetId).expandNamedCells(lookup)
    if (targetExpr !is ScalarExpr) {
        error("'$target' was not a numeric expression")
    }
    val reduced = targetExpr.eval()
    val namedTarget: NamedScalarExpr = NamedScalar(targetId, reduced)

    // Differentiate target with respect to each variable
    println("Differentiating target expression $target with respect to change variables: ${variableIds.joinToString(" ")}")
    val diffs = resolvedVariables.map { (_, variable) ->
        val name = "d$target/d${variable.name}"
        val derivative = differentiate(d(namedTarget) / d(variable)).eval()
        NamedScalar(name, derivative)
    }

    // Assign back variables updated by a delta of their respective derivative
    val assignBacks = (resolvedVariables.values zip diffs).map { (variable, diff) ->
        NamedScalarAssign(
            "update(${variable.name})",
            variable,
            variable - multiply(learningRate, diff)
        ).eval()
    }

    // Create the model, allocate space for it, and iterate. Break when the target didn't move much
    val model = Tableau(resolvedVariables.values + diffs + namedTarget + assignBacks, diffs[0].type).linearize()
    val space = model.allocateSpace()
    println("Plan has ${model.slotCount()} common sub-expressions and uses ${space.size * 8} bytes")
    model.eval(space)
    var priorTarget: Double = Double.MAX_VALUE
    val originalTarget: Double = model.shape(namedTarget).ref(space).value
    println("Minimizing")
    for (i in 0 until 100000) {
//        val sb = StringBuilder()
//        sb.append(resolvedVariables.values.joinToString(" ") {
//            val value = model.shape(it).ref(space)
//            "${it.name}: $value"
//        })
//        println(sb)
        model.eval(space)
        val newTarget = model.shape(namedTarget).ref(space).value
        if (abs(newTarget - priorTarget) < 0.00000000000001)
            break
        priorTarget = newTarget
    }

    println(
        "Target '$target' reduced from ${DoubleAlgebraicType.kaneType.render(originalTarget)} to ${
            DoubleAlgebraicType.kaneType.render(
                priorTarget
            )
        }"
    )

    // Extract the computed values and plug them back into a new sheet
    val new = cells.toMutableMap()
    resolvedVariables.values.forEach {
        val value = model.shape(it).ref(space)
        assert(new.containsKey(it.name))
        new[it.name] = variable(value.value, (cells[it.name] as AlgebraicExpr).type)
    }
    return copy(cells = new.toCells())
}

fun Sheet.render() : String {
    if (cells.isEmpty()) return ""
    val sb = StringBuilder()
    val widths = Array(columns + 1) {
        if (it > 0) {
            val name = columnDescriptors[it - 1]?.name
            if (name == null) 0
            else Identifier.width(name)
        } else 0
    }

    fun colName(column: Int) = columnDescriptors[column]?.name ?: indexToColumnName(column)
    fun rowName(row: Int) = rowDescriptors[row]?.name?.joinToString(" ") ?: "$row"

    // Effective row count for formatting
    val rows = min(rows, sheetDescriptor.limitOutputLines)

    // Calculate width of row number column
    for(row in 0 until rows + 1) {
        widths[0] = max(widths[0], rowName(row).length)
    }

    // Calculate widths of column headers
    for(column in 0 until columns) {
        widths[column + 1] = max(widths[column + 1], Identifier.width(colName(column)))
    }

    // Calculate widths
    for(row in 0 until rows) {
        for(column in 0 until columns) {
            val cell = coordinate(column, row)
            val value = cells[cell]?.toString() ?: ""
            widths[column + 1] = max(widths[column + 1], value.length)
        }
    }

    // Column headers
    widths.indices.forEach { index ->
        val width = widths[index]
        if (index == 0) sb.append(" ".repeat(width) + " ")
        else {
            val columnName = colName(index - 1)
            sb.append(padCenter(Identifier.string(columnName), width) + " ")
        }
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
        sb.append(padLeft(rowName(row+1), widths[0]) + " ")
        for(column in 0 until columns) {
            val cell = coordinate(column, row)
            val value = cells[cell]?.toString() ?: ""
            sb.append(padLeft(value, widths[column + 1]) + " ")
        }
        if (row != rows - 1) sb.append("\n")
    }
    if (this.rows > rows) {
        sb.append("\n...and ${this.rows-rows} more rows")
    }

    // Non-cell data
    val nonCells = cells.toMap().filter { !(it.key is Coordinate) }
    if (nonCells.isNotEmpty()) {
        nonCells.toList().sortedBy { Identifier.string(it.first) }.forEach {
            if (it.second !is SheetRangeExpr) {
                sb.append("\n${it.first}=${it.second}")
            }
        }
    }
    return "$sb"
}

// Construction
fun sheetOf(init: SheetBuilder.() -> List<NamedExpr>): Sheet {
    val builder = SheetBuilderImpl()
    init(builder as SheetBuilder).forEach { builder.add(it) }
    return builder.build()
}

