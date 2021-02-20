package com.github.jomof.kane.impl

import com.github.jomof.kane.*
import com.github.jomof.kane.impl.sheet.*
import kotlin.random.Random

internal const val forceSheetInstantiation = false

class RowDistinctSequence<K>(
    private val sequence: Sequence<Row>,
    private val selector: (Row) -> K
) : Sequence<Row>, ProvidesToSheet, CountableColumns {
    private val sheet by lazy { toSheet(iterator()) }

    init {
        if (forceSheetInstantiation) sheet.rows
    }

    override fun iterator(): Iterator<Row> = RowDistinctIterator(sequence.iterator(), selector)
    override val columns: Int get() = sheet.columns
    override fun toString() = sheet.toString()
    override fun toSheet(): Sheet = sheet
}

private class RowDistinctIterator<T, K>(private val source: Iterator<T>, private val keySelector: (T) -> K) :
    AbstractIterator<T>() {
    private val observed = HashSet<K>()

    override fun computeNext() {
        while (source.hasNext()) {
            val next = source.next()
            val key = keySelector(next)

            if (observed.add(key)) {
                setNext(next)
                return
            }
        }

        done()
    }
}

class ColumnFilteringSequence(
    private val sequence: Sequence<Row>,
    private val predicate: (Int, ColumnDescriptor?) -> Boolean
) : Sequence<Row>, ProvidesToSheet, CountableColumns {
    private val sheet by lazy {
        toSheet(iterator())
    }

    init {
        if (forceSheetInstantiation) sheet.rows
    }

    override fun toSheet(): Sheet = sheet
    override val columns: Int get() = sheet.columns

    override fun toString() = sheet.toString()
    override fun iterator(): Iterator<Row> = object : Iterator<Row> {

        val iterator = sequence.iterator()
        val columnDescriptors = mutableMapOf<Int, ColumnDescriptor>()
        val newToOldColumnMap = mutableMapOf<Int, Int>()
        val columnNameToNewColumnIndex = mutableMapOf<Id, Int>()
        var columnCount = 0
        var initialized = false

        override fun hasNext(): Boolean = iterator.hasNext()

        override fun next(): Row {
            val oldRow = iterator.next()
            if (!initialized) {
                initialized = true
                for (columnOrdinal in 0 until oldRow.columnCount) {
                    val descriptor = oldRow.columnDescriptors[columnOrdinal]
                    if (!predicate(columnOrdinal, descriptor)) continue
                    newToOldColumnMap[columnCount] = columnOrdinal
                    if (descriptor != null) {
                        columnNameToNewColumnIndex[descriptor.name] = columnOrdinal
                        columnDescriptors[columnCount] = descriptor
                    }
                    columnCount++
                }
            }
            val thiz = this
            return object : Row() {
                override val columnCount get() = thiz.columnCount
                override val columnDescriptors get() = thiz.columnDescriptors
                override val rowOrdinal get() = oldRow.rowOrdinal
                override val rowDescriptor get() = oldRow.rowDescriptor
                override val sheetDescriptor get() = oldRow.sheetDescriptor
                override fun get(column: Int) = oldRow[newToOldColumnMap.getValue(column)]
                override fun get(column: String) = oldRow[columnNameToNewColumnIndex.getValue(column)]
            }
        }
    }
}

internal interface RowDropTakeSequence : Sequence<Row> {
    fun drop(n: Int): Sequence<Row>
    fun take(n: Int): Sequence<Row>
}

internal class RowSubSequence(
    private val sequence: Sequence<Row>,
    private val startIndex: Int,
    private val endIndex: Int
) : Sequence<Row>, RowDropTakeSequence, ProvidesToSheet, CountableColumns {
    private val sheet by lazy { toSheet(iterator()) }
    override fun toSheet(): Sheet = sheet
    override fun toString() = sheet.toString()
    override val columns: Int get() = sequence.columns

    init {
        if (forceSheetInstantiation) sheet.rows
    }

    init {
        require(startIndex >= 0) { "startIndex should be non-negative, but is $startIndex" }
        require(endIndex >= 0) { "endIndex should be non-negative, but is $endIndex" }
        require(endIndex >= startIndex) { "endIndex should be not less than startIndex, but was $endIndex < $startIndex" }
    }

    private val count: Int get() = endIndex - startIndex

    override fun drop(n: Int): Sequence<Row> =
        if (n >= count) emptySequence() else RowSubSequence(sequence, startIndex + n, endIndex)

    override fun take(n: Int): Sequence<Row> =
        if (n >= count) this else RowSubSequence(sequence, startIndex, startIndex + n)

    override fun iterator() = object : Iterator<Row> {

        val iterator = sequence.iterator()
        var position = 0

        // Shouldn't be called from constructor to avoid premature iteration
        private fun drop() {
            while (position < startIndex && iterator.hasNext()) {
                iterator.next()
                position++
            }
        }

        override fun hasNext(): Boolean {
            drop()
            return (position < endIndex) && iterator.hasNext()
        }

        override fun next(): Row {
            drop()
            if (position >= endIndex)
                throw NoSuchElementException()
            position++
            return iterator.next()
        }
    }
}

internal class RowTakeSequence(
    private val sequence: Sequence<Row>,
    private val count: Int
) : Sequence<Row>, RowDropTakeSequence, ProvidesToSheet, CountableColumns {
    private val sheet by lazy { toSheet(iterator()) }
    override fun toSheet(): Sheet = sheet
    override fun toString() = sheet.toString()
    override val columns: Int get() = sequence.columns

    init {
        require(count >= 0) { "count must be non-negative, but was $count." }
        if (forceSheetInstantiation) sheet.rows
    }

    override fun drop(n: Int): Sequence<Row> = if (n >= count) emptySequence() else RowSubSequence(sequence, n, count)
    override fun take(n: Int): Sequence<Row> = if (n >= count) this else RowTakeSequence(sequence, n)

    override fun iterator(): Iterator<Row> = object : Iterator<Row> {
        var left = count
        val iterator = sequence.iterator()

        override fun next(): Row {
            if (left == 0)
                throw NoSuchElementException()
            left--
            return iterator.next()
        }

        override fun hasNext(): Boolean {
            return left > 0 && iterator.hasNext()
        }
    }
}

internal class RowDropSequence(
    private val sequence: Sequence<Row>,
    private val count: Int
) : Sequence<Row>, RowDropTakeSequence, ProvidesToSheet, CountableColumns {
    private val sheet by lazy { toSheet(iterator()) }
    override fun toSheet(): Sheet = sheet
    override fun toString() = sheet.toString()
    override val columns: Int get() = sequence.columns

    init {
        require(count >= 0) { "count must be non-negative, but was $count." }
        if (forceSheetInstantiation) sheet.rows
    }

    override fun drop(n: Int): Sequence<Row> =
        (count + n).let { n1 -> if (n1 < 0) RowDropSequence(this, n) else RowDropSequence(sequence, n1) }

    override fun take(n: Int): Sequence<Row> =
        (count + n).let { n1 -> if (n1 < 0) RowTakeSequence(this, n) else RowSubSequence(sequence, count, n1) }

    override fun iterator(): Iterator<Row> = object : Iterator<Row> {
        val iterator = sequence.iterator()
        var left = count

        private fun drop() {
            while (left > 0 && iterator.hasNext()) {
                iterator.next()
                left--
            }
        }

        override fun next(): Row {
            drop()
            return iterator.next()
        }

        override fun hasNext(): Boolean {
            drop()
            return iterator.hasNext()
        }
    }
}

internal class RowShufflingSequence(
    private val sequence: Sequence<Row>,
    private val random: Random
) : Sequence<Row>, ProvidesToSheet, CountableColumns {
    private val sheet by lazy { doShuffle().toSheet() }
    override val columns: Int get() = sequence.columns

    init {
        if (forceSheetInstantiation) sheet.rows
    }

    override fun iterator(): Iterator<Row> = sheet.iterator()
    override fun toSheet() = sheet
    override fun toString() = sheet.toString()

    private fun doShuffle(): Sequence<Row> = sequence<Row> {
        val buffer = sequence.toMutableList()
        while (buffer.isNotEmpty()) {
            val j = random.nextInt(buffer.size)
            val last = buffer.removeLast()
            val value = if (j < buffer.size) buffer.set(j, last) else last
            yield(value)
        }
    }
}

class RowFilteringSequence(
    private val sequence: Sequence<Row>,
    private val sendWhen: Boolean = true,
    private val predicate: (Int, Row) -> Boolean
) : Sequence<Row>, ProvidesToSheet, CountableColumns {
    private val sheetDelegate = lazy { toSheet(iterator()) }
    private val sheet by sheetDelegate
    override val columns: Int get() = sequence.columns

    init {
        if (forceSheetInstantiation) sheet.rows
    }

    fun filter(predicate: (Row) -> Boolean) = RowFilteringSequence(sequence, sendWhen) { index, row ->
        this.predicate(index, row) && predicate(row)
    }

    override fun iterator(): Iterator<Row> = run {
        // If sheet happens to be available then use it
        if (sheetDelegate.isInitialized()) sheet.iterator()
        // Otherwise, iterate without fully instantiating
        else object : Iterator<Row> {
            val iterator = sequence.iterator()
            var nextState: Int = -1 // -1 for unknown, 0 for done, 1 for continue
            var nextItem: Row? = null
            var index = 0

            private fun calcNext() {
                while (iterator.hasNext()) {
                    val item = iterator.next()
                    if (predicate(index++, item) == sendWhen) {
                        nextItem = item
                        nextState = 1
                        return
                    }

                }
                nextState = 0
            }

            override fun next(): Row {
                if (nextState == -1)
                    calcNext()
                if (nextState == 0)
                    throw NoSuchElementException()
                val result = nextItem
                nextItem = null
                nextState = -1
                return result!!
            }

            override fun hasNext(): Boolean {
                if (nextState == -1)
                    calcNext()
                return nextState == 1
            }
        }
    }

    override fun toSheet() = sheet
    override fun toString() = sheet.toString()
}


val Sequence<Row>.columns
    get() = when (this) {
        is CountableColumns -> columns
        else -> error("$javaClass")
    }

fun Collection<Row>.toSheet(): Sheet = toSheet(iterator())

fun Sequence<Row>.toSheet(): Sheet = when (this) {
    is Sheet -> this
    else -> toSheet(iterator())
}

fun Sequence<Row>.eval(): Sheet = ((toSheet() as Expr).eval() as Sheet).showExcelColumnTags(false)

private fun toSheet(
    iterator: Iterator<Row>
): Sheet {
    Kane.metrics.sheetInstatiations++
    val cells = mutableMapOf<Id, Expr>()
    var rowNumber = 0
    val columnDescriptors = mutableMapOf<Int, ColumnDescriptor>()
    val rowDescriptors = mutableMapOf<Int, RowDescriptor>()
    var sheetDescriptor = SheetDescriptor()
    iterator.forEach { row ->
        sheetDescriptor = row.sheetDescriptor
        val rowDescriptor = row.rowDescriptor
        if (rowDescriptor != null) {
            rowDescriptors[rowNumber + 1] = rowDescriptor
        } else if (row.rowOrdinal != rowNumber) {
            rowDescriptors[rowNumber + 1] = RowDescriptor(listOf(constant(row.rowOrdinal + 1)))
        }
        (0 until row.columnCount).forEach { column ->
            if (!columnDescriptors.containsKey(column) && row.columnDescriptors.containsKey(column)) {
                columnDescriptors[column] = row.columnDescriptors.getValue(column)
            }
            val any = row[column]
            if (any != null) {
                val oldCoordinate = coordinate(column, row.rowOrdinal)
                val newCoordinate = coordinate(column, rowNumber)
                val expr = convertAnyToExpr(any).unfreeze(oldCoordinate).freeze(newCoordinate)
                cells[newCoordinate] = expr
            }
        }
        ++rowNumber
    }
    return Sheet.create(
        cells = cells.toCells(),
        rowDescriptors = rowDescriptors,
        columnDescriptors = columnDescriptors,
        sheetDescriptor = sheetDescriptor
    )
}