package com.github.jomof.kane.rigueur


private fun Any.unsafeReplace(type : AlgebraicType<*>?, f : (Any) -> Any) : Any {
    with(f(this)) {
        return when (this) {
            is BinaryScalar<*> -> BinaryScalar(this.op, left = left.scalarReplace(f), right = right.scalarReplace(f))
            is NamedScalar<*> -> NamedScalar(name, scalar.scalarReplace(f))
            is ConstantScalar<*>,
            is NamedScalarVariable<*>,
            is CoerceCellAlgebraic<*> -> this
            else -> error("$javaClass")
        }
    }
}

private fun <E:Any> ScalarExpr<E>.scalarReplace(f: (Any) -> Any): ScalarExpr<Any> {
   return unsafeReplace(type, f) as ScalarExpr<Any>
}

fun UntypedExpr.replaceUntypedExpr(f: (Any) -> UntypedExpr) = unsafeReplace(null) { f(it) } as UntypedExpr
fun TypedExpr.replaceTypedExpr(f: (Any) -> TypedExpr) = unsafeReplace(type) { f(it) } as TypedExpr
fun <E:Any> ScalarExpr<E>.replace(f: (ScalarExpr<E>) -> ScalarExpr<E>) = unsafeReplace(type) { f(it  as ScalarExpr<E>) }  as ScalarExpr<E>
