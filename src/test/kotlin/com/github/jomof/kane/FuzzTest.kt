package com.github.jomof.kane

import com.github.jomof.kane.functions.plus
import com.github.jomof.kane.types.DoubleAlgebraicType
import org.junit.Test
import kotlin.random.Random

class FuzzTest {
    @Test
    fun `fuzz matrix expr`() {
        val random = Random(9L)
        repeat(10000) {
            val expr = random.nextMatrixExpr(3, 3)
//            print("$it-->")
//            println("$expr")
            expr.render()
            expr.reduceArithmetic()
            expr.elements.forEach { }
        }
    }

    @Test
    fun `fuzz matrix float expr`() {
        val random = Random(9L)
        repeat(10000) {
            val expr = random.nextMatrixExpr(3, 3)
            expr.render()
            expr.reduceArithmetic()
        }
    }

    @Test
    fun `fuzz named scalar expr`() {
        val random = Random(9L)
        repeat(10000) {
            val expr = random.nextNamedScalarExpr()
            expr.render()
            expr.reduceArithmetic()
        }
    }

//    @Test
    fun `fuzz named algebraic expr`() {
        val random = Random(9L)
        repeat(10000) {
            val expr = random.nextNamedAlgebraicExpr()
            expr.render()
            val reduced = expr.reduceArithmetic()
            val tab = tableauOf(DoubleAlgebraicType.kaneType, reduced)

            try {
                val model = tab.linearize()
                val space = model.allocateSpace()
                model.eval(space)
            } catch (e : Throwable) {
                println(tab)
                val model = tab.linearize()
                model.allocateSpace()
                throw e.cause ?: e
            }

            try {
                tableauOf(DoubleAlgebraicType.kaneType, expr).linearize()
            } catch (e : Throwable) {
                println(tableauOf(DoubleAlgebraicType.kaneType, expr))
                tableauOf(DoubleAlgebraicType.kaneType, expr).linearize()
                throw e.cause ?: e
            }

        }
    }

    @Test
    fun `fuzz algebraic expr`() {
        val random = Random(9L)
        repeat(10000) {
            val expr = random.nextAlgebraicExpr()
            expr.render()
            try {
                expr.memoizeAndReduceArithmetic()
            } catch (e:Throwable) {
                expr.memoizeAndReduceArithmetic()
                throw e
            }
        }
    }

    @Test
    fun `fuzz expr`() {
        val random = Random(9L)
        repeat(10000) {
            val expr = random.nextExpr()
            expr.render()
        }
    }

    @Test
    fun `triple associative was infinitely reorganized`() {
        val x by constant(0.80607) + constant(0.85835) + constant(0.28762)
        x.reduceArithmetic().assertString("x=1.95204")
    }

    @Test
    fun `linearize failure`() {
        val a by variable(0.0)
        val c by constant(0.0)
        val ax by assign(c to a)
        println(ax.linearize())
    }

    @Test
    fun `linearize constant assign failure`() {
        val a by variable(0.0)
        val ax by assign(constant(19.0) to a)
        println(ax.linearize())
    }

    @Test
    fun `linearize constant matrix assign failure`() {
        val a by matrixVariable(1,2) { -1.0 }
        val ax by assign(matrixOf(1,2, 17.0, 19.0) to a)
        println(ax.linearize())
    }

    @Test
    fun `named matrix directly in linearize`() {
        val a by matrixOf(1,1) { constant(-1.0) }
        println(a.linearize())
    }
}