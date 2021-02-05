package com.github.jomof.kane.types

import com.github.jomof.kane.AlgebraicExpr
import com.github.jomof.kane.Expr
import com.github.jomof.kane.RetypeMatrix
import com.github.jomof.kane.RetypeScalar
import com.github.jomof.kane.sheet.Sheet
import com.github.jomof.kane.sheet.SheetRangeExpr
import com.github.jomof.kane.sheet.columnType
import java.math.BigDecimal
import java.math.RoundingMode

abstract class KaneType<E:Any>(val java : Class<E>) {
    open val simpleName : String get() = java.simpleName
    abstract fun render(value: E): String
    override fun toString() = simpleName
}
abstract class AlgebraicType(java : Class<Double>) : KaneType<Double>(java) {
    abstract fun coerceFrom(value : Number) : Double
}

class StringKaneType : KaneType<String>(String::class.java) {
    override fun render(value: String) = value
    companion object {
        val kaneType = StringKaneType()
    }
}

class IntegerKaneType : KaneType<Int>(Int::class.java) {
    override fun render(value: Int) = "$value"

    companion object {
        val kaneType = IntegerKaneType()
    }
}

class ObjectKaneType : KaneType<Any>(Any::class.java) {
    override fun render(value: Any) = "$value"

    companion object {
        val kaneType = StringKaneType()
    }
}

val <T:Any> Class<T>.kaneType: KaneType<T>
    get() {
        return when (this) {
            Double::class.java -> kaneDouble
            String::class.java -> StringKaneType.kaneType
            Integer::class.java -> IntegerKaneType.kaneType
            Object::class.java -> ObjectKaneType.kaneType
            else ->
                error("$this")
        } as KaneType<T>
    }

class DoubleAlgebraicType(
    private val prefix: String = "",
    private val trimLeastSignificantZeros: Boolean = true,
    private val precision: Int = 5
) : AlgebraicType(Double::class.java) {
    override val simpleName = "double"
    override fun render(value: Double): String {
        if (value.isInfinite()) return "Infinite"
        if (value.isNaN()) return ""
        val result = BigDecimal(value).setScale(precision, RoundingMode.HALF_EVEN).toString()
        val trimmed = if (result.contains(".") && trimLeastSignificantZeros)
            result.trimEnd('0').trimEnd('.')
        else result
        return prefix + trimmed
    }

    override fun coerceFrom(value: Number) = value.toDouble()
}

val kaneDouble = DoubleAlgebraicType()

val AlgebraicExpr.algebraicType: AlgebraicType
    get() = when (this) {
        is RetypeScalar -> type
        is RetypeMatrix -> type
        else -> kaneDouble
    }

/**
 * Get the type of an expression.
 */
val Expr.type: KaneType<*>
    get() = when (this) {
        is Sheet -> if (columns == 1) columnType(0).type else String::class.java.kaneType
        is SheetRangeExpr -> String::class.java.kaneType
        is AlgebraicExpr -> algebraicType
        else -> error("$javaClass")
    }
