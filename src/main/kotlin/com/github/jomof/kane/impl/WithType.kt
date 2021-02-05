package com.github.jomof.kane.impl

//
//fun AlgebraicExpr.withType(type : AlgebraicType) = (this as Expr).withType(type) as AlgebraicExpr
//fun ScalarExpr.withType(type : AlgebraicType) = (this as Expr).withType(type) as ScalarExpr
//fun MatrixExpr.withType(type : AlgebraicType) = (this as Expr).withType(type) as MatrixExpr
//
//private fun Expr.withType(type : AlgebraicType) : Expr {
//    return object : RewritingVisitor(assertTypeChange = false) {
//        override fun rewrite(expr: Expr): Expr {
//            if (expr is AlgebraicExpr && expr.type == type) return expr
//            return super.rewrite(expr)
//        }
//        override fun rewrite(expr: NamedScalar) = expr.copy(type = type)
//        override fun rewrite(expr: NamedMatrix) = expr.copy(type = type)
//        override fun rewrite(expr: ConstantScalar) = expr.copy(type = type)
//        override fun rewrite(expr: AlgebraicUnaryScalar) = super.rewrite(expr.copy(type = type))
//        override fun rewrite(expr: AlgebraicBinaryMatrix) = super.rewrite(expr.copy(type = type))
//        override fun rewrite(expr: AlgebraicUnaryMatrix) = super.rewrite(expr.copy(type = type))
//        override fun rewrite(expr: DataMatrix) = super.rewrite(expr.copy(type = type))
//        override fun rewrite(expr: AlgebraicBinaryMatrixScalar) = super.rewrite(expr.copy(type = type))
//        override fun rewrite(expr: AlgebraicDeferredDataMatrix) = super.rewrite(expr.copy(type = type))
//        override fun rewrite(expr: AlgebraicBinaryScalar) = super.rewrite(expr.copy(type = type))
//        override fun rewrite(expr: AlgebraicUnaryScalarStatistic) = super.rewrite(expr.copy(type = type))
//        override fun rewrite(expr: CoerceScalar) = RetypeScalar(expr, expr.type)
//        override fun rewrite(expr: ScalarListExpr) = super.rewrite(expr.copy(type = type))
//        override fun rewrite(expr: AlgebraicBinaryScalarStatistic) = super.rewrite(expr.copy(type = type))
//    }.rewrite(this)
//}
