package com.github.jomof.kane

import com.github.jomof.kane.GenerateCode.Shape.*
import org.junit.Test
import java.io.File

class GenerateCode {
    private val infixMathOperators = listOf("plus", "minus", "times", "div")

    private enum class Shape {
        Scalar,
        Matrix,
        Statistic
    }

    private data class Parameter(
        val name: String,
        val kind: Shape
    )

    private data class Operator(
        val name: String,
        val shape: Shape,
        val parameters: List<Parameter>
    )

    private val operators = listOf(
        Operator("Summary", Scalar, listOf(Parameter("value", Scalar))),
        Operator("Summary", Scalar, listOf(Parameter("value", Matrix))),
        Operator("Unary", Scalar, listOf(Parameter("value", Scalar))),
        Operator("Unary", Matrix, listOf(Parameter("value", Matrix))),
        Operator("Binary", Scalar, listOf(Parameter("left", Scalar), Parameter("right", Scalar))),
        Operator("Binary", Matrix, listOf(Parameter("left", Scalar), Parameter("right", Matrix))),
        Operator("Binary", Matrix, listOf(Parameter("left", Matrix), Parameter("right", Scalar))),
        Operator("Binary", Matrix, listOf(Parameter("left", Matrix), Parameter("right", Matrix))),

        Operator("BinarySummary", Scalar, listOf(Parameter("left", Scalar), Parameter("right", Scalar))),
        Operator("BinarySummary", Scalar, listOf(Parameter("left", Matrix), Parameter("right", Scalar))),
    )

    /**
     *
     */
    @Test
    fun exprs() {
        val output = File("src/main/kotlin/com/github/jomof/kane/Exprs.kt").absoluteFile
        val sb = StringBuilder()
        sb.append(
            """
            package com.github.jomof.kane

            import com.github.jomof.kane.impl.*
            import com.github.jomof.kane.impl.types.*
            import kotlin.reflect.KProperty
            
            """.trimIndent()
        )
        sb.append("\n\n")
        fun line(s: String = "") = sb.append("$s\n")
        for (op in operators) {
            val inputShape = op.parameters.joinToString("") { it.kind.toString() }
            val operatorClassName = "Algebraic${op.name}$inputShape${op.shape}"
            line("interface I${operatorClassName}Function {")
            line("    val meta : ${op.name}Op")
            sb.append("    operator fun invoke(")
            for ((index, p) in op.parameters.withIndex()) {
                sb.append("${p.name} : ${p.kind}Expr")
                if (index != op.parameters.size - 1) sb.append(", ")
            }
            sb.append(") : ${op.shape}Expr = Algebraic${op.name}$inputShape${op.shape}(this, ")
            for ((index, p) in op.parameters.withIndex()) {
                sb.append(p.name)
                if (index != op.parameters.size - 1) sb.append(", ")
            }
            line(")")
            sb.append("    fun reduceArithmetic(")
            for ((index, p) in op.parameters.withIndex()) {
                sb.append("${p.name} : ${p.kind}Expr")
                if (index != op.parameters.size - 1) sb.append(", ")
            }
            sb.append(") : ${op.shape}Expr?\n")
            sb.append("    fun doubleOp(")
            for ((index, p) in op.parameters.withIndex()) {
                when (p.kind) {
                    Scalar -> sb.append("${p.name} : Double")
                    Matrix -> sb.append("${p.name} : List<Double>")
                    Statistic -> sb.append("${p.name} : List<Double>")
                }
                if (index != op.parameters.size - 1) sb.append(", ")
            }
            when (op.shape) {
                Scalar -> sb.append(") : Double\n")
                Matrix -> sb.append(") : List<Double>\n")
                Statistic -> sb.append(") : List<Double>\n")
            }
            sb.append("    fun differentiate(")
            for (p in op.parameters) {
                sb.append("${p.name} : ${p.kind}Expr, ")
                sb.append("${p.name}d : ${p.kind}Expr, ")
            }
            sb.append("variable : ScalarExpr")
            sb.append(") : ${op.shape}Expr\n")
            sb.append("    fun type(")
            for ((index, p) in op.parameters.withIndex()) {
                sb.append("${p.name} : AlgebraicType")
                if (index != op.parameters.size - 1) sb.append(", ")
            }
            sb.append(") : AlgebraicType\n")
            line("}")
            line()

            line("data class $operatorClassName(")
            line("    val op : I${operatorClassName}Function,")
            for (p in op.parameters) {
                line("    val ${p.name} : ${p.kind}Expr,")
            }
            line("    val name : Id = anonymous")
            line(") : ${op.shape}Expr {")
            line("    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)")
            line("    override fun toString() = render()")
            sb.append("    fun dup(op : I${operatorClassName}Function = this.op, ")
            for (p in op.parameters) {
                sb.append("${p.name} : ${p.kind}Expr = this.${p.name}, ")
            }
            line("name : Id = this.name) : $operatorClassName {")
            sb.append("        if (op === this.op && ")
            for (p in op.parameters) {
                sb.append("${p.name} === this.${p.name} && ")
            }
            line("name == this.name) return this")
            sb.append("        return $operatorClassName(op, ")
            for (p in op.parameters) {
                sb.append("${p.name}, ")
            }
            line("name)")
            line("    }")
            line("}")
            line()
            /*
            line("/*")
            line("data class Named$operatorClassName(")
            line("    override val name : Id,")
            line("    val expr : $operatorClassName")
            line(") : Named${op.shape}Expr {")
            line("    override fun toString() = render()")
            line("    fun dup(name : Id = this.name, expr : $operatorClassName = this.expr) : Named$operatorClassName {")
            line("        if (name === this.name && expr === this.expr) return this")
            line("        return Named$operatorClassName(name, expr)")
            line("    }")
            line("}")
            line()
            line("*/")
            */
        }
        output.writeText(sb.toString())
    }

    /**
     *
     */
    @Test
    fun typesafe() {
        val output = File("src/main/kotlin/com/github/jomof/kane/TypesafeOperations.kt").absoluteFile
        val sb = StringBuilder()
        sb.append(
            """
            package com.github.jomof.kane

            import com.github.jomof.kane.impl.functions.*
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
                fun $op(vararg values : Number) : Double = ${op}Func.invoke(values)
                fun $op(vararg values : ScalarExpr) : ScalarExpr = ${op}Func.invoke(values)
                //fun $op(vararg values : ScalarExpr) : ScalarExpr = ${op}Func.invoke(values)
                fun $op(vararg values : Any) : ScalarExpr = ${op}Func.invoke(values)
                fun $op(sheet: Sheet) : Sheet = ${op}Func.invoke(sheet)
                fun $op(groupBy: GroupBy) : Sheet = ${op}Func.invoke(groupBy)
                fun $op(scalar : StatisticExpr) : ScalarExpr = ${op}Func.invoke(scalar)
                fun $op(matrix : MatrixExpr) : ScalarExpr = ${op}Func.invoke(matrix)
                fun $op(range : SheetRange) : ScalarExpr = ${op}Func.invoke(range)
                //fun $op(expr : Expr) : Expr = ${op}Func.invoke(expr)
                """.trimIndent()
            )
            sb.append("\n\n")
        }

        for (func in Kane.unaryFunctions) {
            val op = func.meta.simpleName
            val capped = op[0].toUpperCase() + op.substring(1)
            sb.append("// typesafe $op\n")
            sb.append("private val ${op}Func = ${capped}Function()\n")
            sb.append("val $op : AlgebraicUnaryFunction = ${op}Func\n")
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