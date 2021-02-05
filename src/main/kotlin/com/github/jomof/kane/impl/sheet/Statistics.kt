package com.github.jomof.kane.impl.sheet

import com.github.jomof.kane.eval
import com.github.jomof.kane.impl.Coordinate
import com.github.jomof.kane.impl.StreamingSamples
import com.github.jomof.kane.impl.canGetConstant
import com.github.jomof.kane.impl.getConstant


internal fun Sheet.columnStatistics(): List<StreamingSamples> {
    val evaled = eval()
    val samples = (0 until evaled.columns).map { StreamingSamples() }
    cells.forEach { (name, expr) ->
        if (name is Coordinate) {
            if (expr.canGetConstant()) {
                samples[name.column].insert(expr.getConstant())
            }
        }
    }
    return samples
}
