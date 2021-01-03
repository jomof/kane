package com.github.jomof.kane.rigueur

import org.junit.Test
import kotlin.random.Random

class FuzzTest {
    @Test
    fun `fuzz matrix expr`() {
        val random = Random(9L)
        repeat(10000) {
            val expr = random.nextMatrixExpr<Double>(3, 3)
//            print("$it-->")
//            println("$expr")
            expr.render()
            expr.reduceArithmetic()
        }
    }

    @Test
    fun `fuzz matrix float expr`() {
        val random = Random(9L)
        repeat(10000) {
            val expr = random.nextMatrixExpr<Float>(3, 3)
            expr.render()
            expr.reduceArithmetic()
        }
    }

    @Test
    fun `fuzz named scalar expr`() {
        val random = Random(9L)
        repeat(10000) {
            val expr = random.nextNamedScalarExpr<Double>()
            expr.render()
            expr.reduceArithmetic()
        }
    }

//    @Test
    fun `fuzz named algebraic expr`() {
        val random = Random(9L)
        repeat(10000) {
            val expr = random.nextNamedAlgebraicExpr<Double>()
            expr.render()
            val reduced = expr.reduceArithmetic()
            val tab = tableauOf(reduced)

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
                tableauOf(expr).linearize()
            } catch (e : Throwable) {
                println(tableauOf(expr))
                tableauOf(expr).linearize()
                throw e.cause ?: e
            }

        }
    }

    @Test
    fun `fuzz algebraic expr`() {
        val random = Random(9L)
        repeat(10000) {
            val expr = random.nextAlgebraicExpr<Double>()
            expr.render()
            expr.memoizeAndReduceArithmetic()
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
        val x by constant(0.80607)+constant(0.85835)+constant(0.28762)
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
        val a by matrixVariable<Double>(1,2) { -1.0 }
        val ax by assign(matrixOf(1,2, 17.0, 19.0) to a)
        println(ax.linearize())
    }

    @Test
    fun `named matrix directly in linearize`() {
        val a by matrixOf(1,1) { constant(-1.0) }
        println(a.linearize())
    }

    @Test
    fun `xxx`() {
        val a by matrixVariable<Double>(2, 2)
        val numbers = matrixOf(2,2) { constant(1.0) }
        //val c by pow(a, tanh(0.9777 * pow(numbers, a)))
        /*
        c=pow(a,tanh(0.9777*pow(0.82056|0.67258|0.46508|0.77273
            0.19365|0.85567|0.30867|0.19204
            0.7162|0.21897|0.76465|0.90392
            0.80911|0.20081|0.65123|0.63573,a)))
         */
    }
}