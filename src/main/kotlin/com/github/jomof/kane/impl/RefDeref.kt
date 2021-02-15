package com.github.jomof.kane.impl

import com.github.jomof.kane.Expr
import com.github.jomof.kane.MatrixExpr
import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.impl.sheet.Sheet
import com.github.jomof.kane.impl.visitor.DifferenceVisitor
import com.github.jomof.kane.impl.visitor.SheetRewritingVisitor

data class ScalarReference(val name: Id) : ScalarExpr {
    override fun toString() = Identifier.string(name)
}

data class MatrixReference(val name: Id) : MatrixExpr {
    override fun toString() = Identifier.string(name)
}

private class MakeReferenced : SheetRewritingVisitor() {
    override fun rewrite(expr: Expr): Expr {
        val result = super.rewrite(expr)
        if (result is ScalarReference) return result
        if (result is MatrixReference) return result
        if (result is NamedScalarVariable) return result
        if (!inSheet()) return result
        if (result.hasName()) {
            val name = result.name
            if (!cells.containsKey(name)) {
                putCell(name, result.toUnnamed())
            }
            when (result) {
                is ScalarExpr -> return ScalarReference(name)
                is MatrixExpr -> return MatrixReference(name)
                else -> error("${result.javaClass}")
            }
        }
        return result
    }
}

private class MakeDereferenced : SheetRewritingVisitor() {
    override fun rewrite(expr: ScalarReference): Expr {
        val lookup = cells[expr.name]!!
        return rewrite(lookup).toNamed(expr.name)
    }

    override fun rewrite(expr: MatrixReference): Expr {
        return rewrite(cells[expr.name]!!).toNamed(expr.name)
    }
}

private val makeReferenced = MakeReferenced()
private val makeDereferenced = MakeDereferenced()

fun Sheet.makeReferenced() = makeReferenced.sheet(this)
fun Expr.makeReferenced() = makeReferenced.rewrite(this)
fun Sheet.makeDereferenced() = makeDereferenced.sheet(this)
fun Expr.makeDereferenced() = makeDereferenced.rewrite(this)