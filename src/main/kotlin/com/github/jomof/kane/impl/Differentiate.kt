package com.github.jomof.kane.impl

import com.github.jomof.kane.*
import com.github.jomof.kane.impl.functions.AlgebraicUnaryFunction
import com.github.jomof.kane.impl.functions.d
import com.github.jomof.kane.impl.visitor.RewritingVisitor
import kotlin.reflect.KProperty

internal fun diff(expr: ScalarExpr, variable: ScalarExpr): ScalarExpr {
    if (variable is NamedScalar) return diff(expr, variable.scalar)
    val result: ScalarExpr = when (expr) {
        variable -> ConstantScalar(1.0)
        is NamedScalar -> {
            diff(expr.scalar, variable)
        }
        is MatrixVariableElement -> ConstantScalar(0.0)
        is ScalarVariableExpr -> ConstantScalar(0.0)
        is ConstantScalar -> ConstantScalar(0.0)
        is ScalarVariable -> ConstantScalar(0.0)
        is AlgebraicUnaryScalarScalar -> {
            val reduced = expr.op.reduceArithmetic(expr.value)
            if (reduced != null) diff(reduced, variable)
            else {
                val vd = diff(expr.value, variable)
                expr.op.differentiate(expr.value, vd, variable)
            }
        }
        is AlgebraicBinaryScalarScalarScalar -> {
            val p1d = diff(expr.left, variable)
            val p2d = diff(expr.right, variable)
            expr.op.differentiate(expr.left, p1d, expr.right, p2d, variable)
        }
        is AlgebraicSummaryMatrixScalar -> {
            diff(expr.eval(), variable)
        }
        is AlgebraicSummaryScalarScalar -> {
            diff(expr.eval(), variable)
        }
        is RetypeScalar -> diff(expr.scalar, variable)
        else ->
            error("${expr.javaClass}")
    }
    return result
}

internal fun differentiate(expr: AlgebraicExpr): AlgebraicExpr {
    fun ScalarExpr.tryD() = when {
        this is AlgebraicUnaryScalarScalar && op == d -> value
        else -> null
    }

    fun MatrixExpr.tryD() = when {
        this is AlgebraicUnaryMatrixMatrix && op == d -> value
        else -> null
    }

    fun ScalarExpr.diffOr(): ScalarExpr? {
        when (this) {
            is AlgebraicBinaryScalarScalarScalar -> {
                if (op != com.github.jomof.kane.impl.functions.div) return null
                val dleft = left.tryD() ?: return null
                val dright = right.tryD() ?: return null
                return diff(dleft, dright)
            }
            else -> return null
        }
    }

    fun MatrixExpr.diffOr(): MatrixExpr? {
        if (this !is AlgebraicBinaryScalarMatrixMatrix) return null
        if (op != com.github.jomof.kane.impl.functions.div) return null
        val dleft = left.tryD() ?: return null
        val dright = right.tryD() ?: return null

        return matrixOf(right.columns, right.rows) { (column, row) ->
            diff(dleft, dright[column, row])
        }
    }

    fun ScalarExpr.self() = differentiate(this)
    fun MatrixExpr.self() = differentiate(this)
    with(expr) {
        val result = when (this) {
            is ScalarAssign -> {
                val right = right.self()
                if (this.right !== right) dup(right = right)
                else expr
            }
            is MatrixAssign -> {
                val right = right.self()
                if (this.right !== right) dup(right = right)
                else this
            }
            is NamedMatrix -> {
                val diff = matrix.diffOr()
                if (diff != null) copy(name = "${name}'", matrix = diff)
                else {
                    val matrix = matrix.self()
                    if (this.matrix !== matrix) copy(matrix = matrix)
                    else this
                }
            }
            is NamedScalar -> {
                val diff = scalar.diffOr()
                if (diff != null) copy(name = "${name}'", scalar = diff)
                else {
                    val scalar = scalar.self()
                    if (this.scalar !== scalar) copy(scalar = scalar)
                    else this
                }
            }
            is AlgebraicUnaryScalarScalar -> dup(value = value.self())
            is AlgebraicBinaryScalarScalarScalar -> {
                diffOr() ?: dup(left = left, right = right)
            }
            is AlgebraicBinaryMatrixMatrixMatrix -> {
                diffOr() ?: dup(left = left, right = right)
            }
            is AlgebraicBinaryScalarMatrixMatrix -> {
                diffOr() ?: run {
                    val left = left.self()
                    val right = right.self()
                    if (this.left !== left || this.right !== right) dup(left = left, right = right)
                    else this
                }
            }
            else ->
                error("${expr.javaClass}")
        }
        return result.withNameOf(expr) { "${expr.name}'" }
    }
}


internal fun differentiate(expr: ScalarExpr, variable: ScalarExpr) =
    differentiate(d(expr) / d(variable)).eval().toFunc(variable)

internal fun differentiate(expr: ScalarExpr): ScalarExpr = (differentiate(expr as AlgebraicExpr) as ScalarExpr).eval()
internal fun differentiate(expr: MatrixExpr): MatrixExpr = (differentiate(expr as AlgebraicExpr) as MatrixExpr).eval()

internal data class UserDefinedFunction(
    override val meta: UnaryOp,
    val function: ScalarExpr,
    val variable: ScalarExpr
) : AlgebraicUnaryFunction {
    private class Replacer(
        val variable: ScalarExpr,
        val replacement: ScalarExpr
    ) : RewritingVisitor() {
        override fun scalar(expr: ScalarExpr): ScalarExpr {
            if (expr == variable) return super.scalar(replacement)
            return super.scalar(expr)
        }
    }

    private fun replace(replacement: ScalarExpr) = Replacer(variable, replacement).scalar(function)

    override fun reduceArithmetic(value: ScalarExpr) = replace(value)
    override fun doubleOp(value: Double) = replace(constant(value)).eval().getConstant()
    override fun differentiate(expr: ScalarExpr, exprd: ScalarExpr, variable: ScalarExpr) = TODO()
    operator fun getValue(nothing: Nothing?, property: KProperty<*>) =
        copy(
            meta = UnaryOp(
                "${property.name}:($variable)->$function",
                "${property.name}"
            )
        )

    override fun toString() = meta.simpleName
}

private fun ScalarExpr.toFunc(variable: ScalarExpr):
        UserDefinedFunction {
    val name = if (hasName()) Identifier.string(name) else "($variable)->$this"
    return UserDefinedFunction(UnaryOp(name, name), this, variable)
}