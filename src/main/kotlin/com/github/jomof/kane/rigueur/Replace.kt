package com.github.jomof.kane.rigueur


private fun Any.unsafeReplace(type : AlgebraicType<*>?, f : (Any) -> Any) : Any {
    with(f(this)) {
        fun <E:Any> ScalarExpr<E>.self(f: (Any) -> Any) = unsafeReplace(type, f) as ScalarExpr<Any>
        fun <E:Any> MatrixExpr<E>.self(f: (Any) -> Any) = unsafeReplace(type, f) as MatrixExpr<Any>
        val result = when (this) {
            is UnaryScalar<*> -> UnaryScalar(op, value = value.self(f))
            is UnaryMatrix<*> -> UnaryMatrix(op, value = value.self(f))
            is UnaryMatrixScalar<*> -> UnaryMatrixScalar(op, value = value.self(f))
            is BinaryMatrix<*> -> BinaryMatrix(
                op,
                rows = rows,
                columns = columns,
                left = left.self(f),
                right = right.self(f))
            is BinaryMatrixScalar<*> -> BinaryMatrixScalar(
                op,
                rows = rows,
                columns = columns,
                left = left.self(f),
                right = right.self(f))
            is BinaryScalarMatrix<*> -> BinaryScalarMatrix(
                op,
                rows = rows,
                columns = columns,
                left = left.self(f),
                right = right.self(f))
            is BinaryScalar<*> -> BinaryScalar(op, left = left.self(f), right = right.self(f))
            is NamedScalar<*> -> NamedScalar(name, scalar.self(f))
            is NamedMatrix<*> -> NamedMatrix(name, matrix.self(f))
            is DataMatrix<*> -> DataMatrix(
                columns = columns,
                rows = rows,
                elements = elements.map { it.self(f) })
            is NamedMatrixVariable<*>,
            is MatrixVariableElement<*>,
            is ConstantScalar<*>,
            is NamedScalarVariable<*>,
            is CoerceCellAlgebraic<*> -> this
            else -> error("$javaClass")
        }
        return result
    }
}


fun TypedExpr.replaceTypedExpr(f: (Any) -> TypedExpr) = unsafeReplace(type) { f(it) } as TypedExpr
fun <E:Any> Expr<E>.replaceExpr(f: (Expr<E>) -> Expr<E>) = unsafeReplace(type) { f(it  as Expr<E>) }  as Expr<E>
fun <E:Any> ScalarExpr<E>.replace(f: (ScalarExpr<E>) -> ScalarExpr<E>) = unsafeReplace(type) { f(it  as ScalarExpr<E>) }  as ScalarExpr<E>
