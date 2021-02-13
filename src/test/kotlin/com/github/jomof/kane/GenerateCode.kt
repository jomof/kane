package com.github.jomof.kane

import org.junit.Test
import java.io.File

class GenerateCode {
    private val infixMathOperators = listOf("plus", "minus", "times", "div")

    /**
     *
     */
    @Test
    fun test() {
        val output = File("src/main/kotlin/com/github/jomof/kane/TypesafeOperations.kt").absoluteFile
        val sb = StringBuilder()
        sb.append(
            """
            package com.github.jomof.kane

            import com.github.jomof.kane.functions.*
            import com.github.jomof.kane.impl.functions.*
            import com.github.jomof.kane.impl.sheet.*
            import com.github.jomof.kane.impl.*
            """.trimIndent()
        )
        sb.append("\n\n")

        for (op in infixMathOperators) {
            sb.append(
                """
                // $op typesafe infix operators
                operator fun Number.$op(right: ScalarExpr) = $op(this.toDouble(), right)
                operator fun Number.$op(right: MatrixExpr) = $op(this.toDouble(), right)                
                operator fun Number.$op(right: SheetRange) = $op(this.toDouble(), right)

                operator fun ScalarExpr.$op(right: Number) = $op(this, right.toDouble())
                operator fun ScalarExpr.$op(right: ScalarExpr) = $op(this, right)
                operator fun ScalarExpr.$op(right: MatrixExpr) = $op(this, right)
                operator fun ScalarExpr.$op(right: SheetRange) = $op(this, right)
                
                operator fun MatrixExpr.$op(right: Number) = $op(this, right.toDouble())
                operator fun MatrixExpr.$op(right: ScalarExpr) = $op(this, right)
                operator fun MatrixExpr.$op(right: MatrixExpr) = $op(this, right)
                operator fun MatrixExpr.$op(right: SheetRange) = $op(this, right)
                
                operator fun SheetRange.$op(right: Number) = $op(this, right.toDouble())
                operator fun SheetRange.$op(right: ScalarExpr) = $op(this, right)
                operator fun SheetRange.$op(right: MatrixExpr) = $op(this, right)
                operator fun SheetRange.$op(right: SheetRange) = $op(this, right)
                """.trimIndent()
            )
            sb.append("\n\n")
        }

        for (func in Kane.unaryStatisticsFunctions) {
            val op = func.meta.simpleName
            val capped = op[0].toUpperCase() + op.substring(1)
            sb.append("// typesafe $op\n")
            sb.append("private val ${op}Func = ${capped}Function()\n")
            sb.append("val $op : AggregatableFunction = ${op}Func\n")
            sb.append(
                """
                fun $op(vararg values : Number) : Double = ${op}Func.call(values)
                fun $op(vararg values : ScalarExpr) : ScalarExpr = ${op}Func.call(values)
                fun $op(vararg values : Any) : ScalarExpr = ${op}Func.call(values)
                fun $op(sheet: Sheet) : Sheet = ${op}Func.call(sheet)
                fun $op(groupBy: GroupBy) : Sheet = ${op}Func.call(groupBy)
                fun $op(scalar : ScalarExpr) : ScalarExpr = ${op}Func.call(scalar)
                fun $op(matrix : MatrixExpr) : ScalarExpr = ${op}Func.call(matrix)
                fun $op(range : SheetRange) : ScalarExpr = ${op}Func.call(range)
                fun $op(expr : Expr) : Expr = ${op}Func.call(expr)
                """.trimIndent()
            )
            sb.append("\n\n")
        }

        for (func in Kane.unaryFunctions) {
            val op = func.meta.simpleName
            val capped = op[0].toUpperCase() + op.substring(1)
            sb.append("// typesafe $op\n")
            sb.append("private val ${op}Func = ${capped}Function()\n")
            sb.append("val $op : AlgebraicUnaryScalarFunction = ${op}Func\n")
            sb.append(
                """
                /**
                 * $op function
                 * [![](https://jomof.github.io/kane/figures/$op-profile.svg)]
                 */
                fun $op(matrix : NamedMatrix) : MatrixExpr = ${op}Func(matrix as MatrixExpr)
                fun $op(matrix : DataMatrix) : MatrixExpr = ${op}Func(matrix as MatrixExpr)
                fun $op(matrix : MatrixExpr) : MatrixExpr = ${op}Func(matrix)
                fun $op(scalar : ScalarExpr) : ScalarExpr = ${op}Func(scalar)
                fun $op(scalar : Double) : Double = ${op}Func(scalar)
                
                
                """.trimIndent()
            )
        }
        output.writeText(sb.toString())
    }
}