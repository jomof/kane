package com.github.jomof.kane

import com.github.jomof.kane.functions.d
import com.github.jomof.kane.functions.times
import com.github.jomof.kane.impl.*
import com.github.jomof.kane.impl.sheet.*
import com.github.jomof.kane.impl.types.kaneDouble
import kotlin.math.abs


fun Sheet.minimize(
    target: String,
    variables: List<String>,
    learningRate: Double = 0.001
): Sheet {
    val resolvedVariables = mutableMapOf<Id, NamedScalarVariable>()
    val variableIds = variables.map { Identifier.normalizeUserInput(it) }

    // Turn each 'variable' into a NamedScalarVariable
    variableIds.forEach { cell ->
        resolvedVariables[cell] = namedVariableOf(cell)
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
            variable - times(learningRate, diff)
        ).eval()
    }

    // Create the model, allocate space for it, and iterate. Break when the target didn't move much
    val model = Tableau(resolvedVariables.values + diffs + namedTarget + assignBacks).linearize()
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
        "Target '$target' reduced from ${kaneDouble.render(originalTarget)} to ${
            kaneDouble.render(
                priorTarget
            )
        }"
    )

    // Extract the computed values and plug them back into a new sheet
    val new = cells.toMutableMap()
    resolvedVariables.values.forEach {
        val value = model.shape(it).ref(space)
        assert(new.containsKey(it.name))
        new[it.name] = variable(value.value)
    }
    return copy(cells = new.toCells())
}