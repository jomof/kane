package com.github.jomof.kane.impl

import com.github.jomof.kane.ProvidesColumnDescriptors
import com.github.jomof.kane.Row
import com.github.jomof.kane.impl.sheet.ColumnDescriptor

fun Sequence<Row>.getColumnDescriptors(): Map<Int, ColumnDescriptor> =
    when (this) {
        is ProvidesColumnDescriptors -> columnDescriptors
        is RowDropSequence -> source.getColumnDescriptors()
        is RowTakeSequence -> source.getColumnDescriptors()
        is RowSubSequence -> source.getColumnDescriptors()
        is RowShufflingSequence -> source.getColumnDescriptors()
        is RowFilteringSequence -> source.getColumnDescriptors()
        is RowDistinctSequence<*> -> source.getColumnDescriptors()
        else ->
            error("$javaClass")
    }