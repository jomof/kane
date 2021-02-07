package com.github.jomof.kane

import org.junit.Test
import java.io.File

class GenerateCode {
    private val infixMathOperators = listOf("plus", "minus", "times", "div")

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
            
            
            """.trimIndent()
        )

        for (op in infixMathOperators) {
            sb.append(
                """
                // $op typesafe infix operators
                operator fun <E : Number> ScalarExpr.$op(right: E) = $op(this, right.toDouble())
                operator fun <E : Number> E.$op(right: ScalarExpr) = $op(this.toDouble(), right)
                operator fun ScalarExpr.$op(right: ScalarExpr) = $op(this, right)
                operator fun <E : Number> MatrixExpr.$op(right: E) = $op(this, right.toDouble())
                operator fun MatrixExpr.$op(right: ScalarExpr) = $op(this, right)
                operator fun <E : Number> E.$op(right: MatrixExpr) = $op(this.toDouble(), right)
                operator fun ScalarExpr.$op(right: MatrixExpr) = $op(this, right)
                operator fun MatrixExpr.$op(right: MatrixExpr) = $op(this, right)
                operator fun ScalarExpr.$op(right: SheetRange) = $op(this, right)
                operator fun SheetRange.$op(right: ScalarExpr) = $op(this, right)
                operator fun <E : Number> SheetRange.$op(right: E) = $op(this, right.toDouble())
                operator fun <E : Number> E.$op(right: SheetRange) = $op(this.toDouble(), right)
                operator fun SheetRange.$op(right: SheetRange) = $op(this, right)
                operator fun MatrixExpr.$op(right: SheetRange) = $op(this, right)
                
                
                """.trimIndent()
            )
        }

        for (func in Kane.unaryStatisticsFunctions) {
            val op = func.meta.simpleName
            val capped = op[0].toUpperCase() + op.substring(1)
            sb.append("// typesafe $op\n")
            sb.append("private val ${op}Func = ${capped}Function()\n")
            sb.append("val $op : AggregatableFunction = ${op}Func\n")
            sb.append(
                """
                fun $op(list : List<ScalarExpr>) : ScalarExpr = ${op}Func(list)
                fun $op(vararg values : ScalarExpr) : ScalarExpr = ${op}Func(values)
                fun $op(list : List<Double>) : Double = ${op}Func(list)
                fun $op(vararg values : Double) : Double = ${op}Func(values)
                fun $op(matrix : MatrixExpr) : ScalarExpr = ${op}Func(matrix)
                fun $op(sheet: Sheet) : Sheet = ${op}Func(sheet)
                fun $op(groupBy: GroupBy) : Sheet = ${op}Func(groupBy)
                fun $op(algebraic : AlgebraicExpr) : ScalarExpr = ${op}Func(algebraic)
                fun $op(range : SheetRange) : ScalarExpr = ${op}Func(range)
                fun $op(expr : Expr) : Expr = ${op}Func(expr)
                
                
                """.trimIndent()
            )
        }

        for (func in Kane.unaryFunctions) {
            val op = func.meta.simpleName
            val capped = op[0].toUpperCase() + op.substring(1)
            sb.append("// typesafe $op\n")
            sb.append("private val ${op}Func = ${capped}Function()\n")
            sb.append("val $op : AlgebraicUnaryScalarFunction = ${op}Func\n")
            sb.append(
                """
                fun $op(matrix : MatrixExpr) : MatrixExpr = ${op}Func(matrix)
                fun $op(scalar : ScalarExpr) : ScalarExpr = ${op}Func(scalar)
                fun $op(scalar : Double) : Double = ${op}Func(scalar)
                
                
                """.trimIndent()
            )
        }
        output.writeText(sb.toString())
    }
}