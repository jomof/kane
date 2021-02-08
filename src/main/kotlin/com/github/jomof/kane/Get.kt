package com.github.jomof.kane

import com.github.jomof.kane.impl.MatrixVariableElement
import com.github.jomof.kane.impl.NamedMatrixVariable
import com.github.jomof.kane.impl.getMatrixElement

operator fun NamedMatrixVariable.get(column: Int, row: Int): MatrixVariableElement = getMatrixElement(column, row)
operator fun MatrixExpr.get(column: Int, row: Int): ScalarExpr = getMatrixElement(column, row)