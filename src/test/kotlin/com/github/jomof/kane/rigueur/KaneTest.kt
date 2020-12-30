@file:Suppress("UNCHECKED_CAST")
package com.github.jomof.kane.rigueur

import com.github.jomof.kane.rigueur.types.*
import org.junit.Test
import java.io.File
import java.util.*
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.pow

class KaneTest {

    private fun Double.near(expected:Double) : Boolean {
        val diff = (this - expected).pow(2.0)
        return diff < 0.000001
    }

    private fun Double.assertNear(expected:Double) {
        assert(near(expected)) {
            "$this was not close enough to $expected"
        }
    }

    @Test
    fun `dont check in tracking enable`() {
        errorIfTrackingEnabled()
    }

    @Test
    fun `data matrix basics`() {
        val m1 by matrixOf(3,2,
            1.0, 2.0, 3.0,
            4.0, 5.0, 6.0)
        m1.assertString("""
            m1
            ------
            1|2|3
            4|5|6
        """.trimIndent())
        val mvar2 by matrixVariable<Double>(3, 4)
        val m2 by mvar2.toDataMatrix()
        m2.assertString("""
            m2
            ------
            mvar2[0,0]|mvar2[1,0]|mvar2[2,0]
            mvar2[0,1]|mvar2[1,1]|mvar2[2,1]
            mvar2[0,2]|mvar2[1,2]|mvar2[2,2]
            mvar2[0,3]|mvar2[1,3]|mvar2[2,3]
        """.trimIndent())
        val tableau = tableauOf(m1,m2)
        tableau.assertString("""
            m1
            ------
            1|2|3
            4|5|6
            
            m2
            ------
            mvar2[0,0]|mvar2[1,0]|mvar2[2,0]
            mvar2[0,1]|mvar2[1,1]|mvar2[2,1]
            mvar2[0,2]|mvar2[1,2]|mvar2[2,2]
            mvar2[0,3]|mvar2[1,3]|mvar2[2,3]
        """.trimIndent())
    }

//    @Test
//    fun mult() {
//        val m1 by matrixOf(10, 1) { it.column + 0.0 }
//        val m2 by matrixOf(1, 10) { 10.0 - it.row }
//        val m3 by m1 cross m2
//        m3.assertString("m3=[165]")
//    }

    @Test
    fun `multiplication bug`() {
        val input by matrixVariable<Double>(1, 1)
        val w0 by matrixVariable<Double>(input.rows + 1, 1)
        val h1 by logit(w0 cross (input stack 1.0))
        h1.toDataMatrix().assertString("h1=[logit(w0[0,0]*input[0,0]+w0[1,0])]")
    }

    @Test
    fun `repro tiny diff problem`() {
        val x by variable<Double>()
        val m by variable<Double>()
        val b by variable<Double>()
        val t by variable<Double>()
        val error by 0.5 * pow(t - ((m * x) + b), 2.0)
        val dm by differentiate(d(error)/d(m))
        dm.assertString("dm=-(t-(m*x+b))*x")
    }

    @Test
    fun `render formulas`() {
        val r by variable<Double>()
        val x by variable<Double>()
        val m by variable<Double>()
        val b by variable<Double>()
        val y by m * x + b
        val target by variable<Double>()
        val error by pow(target - y, 2.0)
        val dm by -1.0 * r * differentiate(d(error)/d(m))
        val db by -1.0 * r * differentiate(d(error)/d(b))
        error.assertString("error=(target-(m*x+b))²")
        dm.assertString("dm=2*r*(target-(m*x+b))*x")
        db.assertString("db=2*r*(target-(m*x+b))")
    }

    @Test
    fun `tiny relu regression`() {
        val x by variable<Double>()
        val m by variable(0.1)
        val b by variable(0.2)
        val y by relu(m * x + b)
        val target by variable<Double>()
        val error by pow(target - y, 2.0)
        val dm by m - 0.1 * differentiate(d(error)/d(m))
        val db by b - 0.1 * differentiate(d(error)/d(b))
        val am by assign(dm to m)
        val ab by assign(db to b)
        val tab = tableauOf(am,ab,dm,db)
        val map = mutableMapOf(
            "b" to 0.0,
            "m" to 0.0,
        )

        (0 until 100).forEach {
            (-5 until 5).forEach { point ->
                map["x"] = point.toDouble()
                val target = relu(10.0 * point.toDouble() + 0.2)
                map["target"] = target
                tab.eval(map)
                map["b"] = map.getValue("db")
                map["m"] = map.getValue("dm")
            }
        }
        map["m"]!!.assertNear(10.0)
        map["b"]!!.assertNear(0.2)

        val model = tab.linearize()
        val space = model.allocateSpace()
        println(model)
        val xSlot = model.shape(x).ref(space)
        val mSlot = model.shape(m).ref(space)
        val bSlot = model.shape(b).ref(space)
        val targetSlot = model.shape(target).ref(space)

        for(i in 0 until 100) {
            (-5 until 5).forEach { point ->
                xSlot.set(point.toDouble())
                val target = relu(10.0 * point.toDouble() + 0.2)
                targetSlot.set(target)
                model.eval(space)
            }
        }

        mSlot.value.assertNear(10.0)
        bSlot.value.assertNear(0.2)
    }

    @Test
    fun `tiny relu regression with matrix`() {
        val s by matrixVariable<Double>(1,2)
        val x by variable<Double>()
        val m by s[0,0]
        val b by s[0,1]
        val y by relu(m * x + b)
        val target by variable<Double>()
        val error by pow(target - y, 2.0)
        val ds by s - 0.1 * differentiate(d(error)/d(s))
        val ass by assign(ds to s)
        val tab = tableauOf(m, b, ass)
        val model = tab.linearize()
        val space = model.allocateSpace()
        println(model)
        val xSlot = model.shape(x).ref(space)
        val mSlot = model.shape(m).ref(space)
        val bSlot = model.shape(b).ref(space)
        val targetSlot = model.shape(target).ref(space)

        for(i in 0 until 100) {
            (-5 until 5).forEach { point ->
                xSlot.set(point.toDouble())
                val target = relu(10.0 * point.toDouble() + 0.2)
                targetSlot.set(target)
                model.eval(space)
            }
        }

        mSlot.value.assertNear(10.0)
        bSlot.value.assertNear(0.2)
    }

    @Test
    fun `tiny linear regression`() {
        val r by variable<Double>()
        val x by variable<Double>()
        val m by variable<Double>()
        val b by variable<Double>()
        val y by m * x + b
        val target by variable<Double>()
        val error by pow(target - y, 2.0)
        val dm by -1.0 * r * differentiate(d(error)/d(m))
        val db by -1.0 * r * differentiate(d(error)/d(b))
        println(error)
        println(dm)
        println(db)
        val tab = tableauOf(dm,db)
        val map = mutableMapOf(
            "r" to 0.1, // learning rate
            "b" to 0.0,
            "m" to 0.0,
        )

        (0 until 50).forEach {
            (-5 until 5).forEach { point ->
                map["x"] = point.toDouble()
                map["target"] = -500 * point.toDouble() + 0.2
                tab.eval(map)
                map["b"] = map.getValue("b") + map.getValue("db")
                map["m"] = map.getValue("m") + map.getValue("dm")
            }
        }
        map["m"]!!.assertNear(-500.0)
        map["b"]!!.assertNear(0.2)
        println(map)
    }

    @Test
    fun `assign-back basics`() {
        val r by variable<Double>()
        val a1 by assign(r + 1.0 - 1.0 to r)
        a1.assertString("r <- r+1-1")
        a1.memoizeAndReduceArithmetic().assertString("r <- r")
    }

    @Test
    fun `tiny linear regression with assign-back backed by matrix with bound holes`() {
        val a by matrixVariable<Double>(1, 2)
        val r by variable<Double>()
        val x by variable<Double>()
        val m by a[0,0]
        val b by a[0,1]
        val y by m * x + b
        val target by variable<Double>()
        val error by 0.5 * pow(target - y, 2.0)
        val da by a - r * differentiate(d(error)/d(a))
        val aa by assign(da to a)
        val tab = tableauOf(m,b,aa)
        println(tab)
        val layout = tab.linearize()
        println("---")
        println(layout)
        val space = layout.allocateSpace()
        val xslot = layout.shape(x).ref(space)
        val rslot = layout.shape(r).ref(space)
        val targetSlot = layout.shape(target).ref(space)
        val mslot = layout.shape(m).ref(space)
        val bslot = layout.shape(b).ref(space)
        rslot.set(0.1)
        repeat(50) {
            (-2 until 2).forEach { point ->
                xslot.set(point.toDouble())
                targetSlot.set(-500 * point.toDouble() + 0.2)
                layout.eval(space)
            }
        }
        mslot.value.assertNear(-500.0)
        bslot.value.assertNear(0.2)
    }

    @Test
    fun `check relu and steps`() {
        assert(lrelu(-5.0)==-0.5)
        assert(lrelu(5.0)==5.0)
        assert(lrelu(0.0)==0.0)
        assert(relu(-5.0)==0.0)
        assert(relu(5.0)==5.0)
        assert(relu(0.0)==0.0)
        assert(lstep(-5.0)==0.1)
        assert(lstep(5.0)==1.0)
        assert(lstep(0.0)==1.0)
        assert(step(-5.0)==0.0)
        assert(step(5.0)==1.0)
        assert(step(0.0)==1.0)
    }

    @Test
    fun `follow logistic example`() {
        // https://mattmazur.com/2015/03/17/a-step-by-step-backpropagation-example/
        val r by variable(0.5) // Learning rate
        val inputs by matrixVariable(1, 2, 0.05, 0.1)
        val w0 by matrixVariable(2,2,
            0.15, 0.2,
            0.25, 0.3)
        val w1 by matrixVariable(2,2,
            0.4, 0.45,
            0.5, 0.55)
        val b0 by variable(0.35)
        val b1 by variable(0.6)
        val h by logit((w0 cross inputs) + b0)
        val output by logit((w1 cross h) + b1)
        val target by matrixVariable(1, 2, 0.01, 0.99)
        val errors by 0.5 * pow(target-output, 2.0)
        val error by summation(errors)
        val db0 by b0 - r * differentiate(d(error)/d(b0))
        val db1 by b1 - r * differentiate(d(error)/d(b1))
        val dw0 by w0 - r * differentiate(d(error)/d(w0))
        val dw1 by w1 - r * differentiate(d(error)/d(w1))
        val h0 by h[0,0]
        val h1 by h[0,1]
        val o0 by output[0,0]
        val o1 by output[0,1]
        val e0 by errors[0,0]
        val e1 by errors[0,1]

//        h0.substituteInitial().assertString("h0=logit(0.15*0.05+0.2*0.1+0.35)")
        h0.substituteInitial().reduceArithmetic().assertString("h0=0.59327")
//        h1.substituteInitial().assertString("h1=logit(0.25*0.05+0.3*0.1+0.35)")
        h1.substituteInitial().reduceArithmetic().assertString("h1=0.59688")

        o0.substituteInitial().reduceArithmetic().assertString("o0=0.75137")
        o1.substituteInitial().reduceArithmetic().assertString("o1=0.77293")

        e0.substituteInitial().reduceArithmetic().assertString("e0=0.27481")
        e1.substituteInitial().reduceArithmetic().assertString("e1=0.02356")

        error.substituteInitial().reduceArithmetic().assertString("error=0.29837")
        val x = dw1.substituteInitial()
        x.reduceArithmetic().assertString("""
            dw1
            ------
            0.35892|0.40867
            0.5113|0.56137
        """.trimIndent())
        dw0.substituteInitial().reduceArithmetic().assertString("""
            dw0
            ------
            0.14978|0.19956
            0.24975|0.2995
        """.trimIndent())

        val layout = tableauOf(output, db0, db1, dw0, dw1).linearize()
        println(layout)
    }

    @Test
    fun `follow logistic example short`() {
        // https://mattmazur.com/2015/03/17/a-step-by-step-backpropagation-example/
        val inputs by matrixVariable(1, 2, 0.05f, 0.1f)
        val w0 by matrixVariable(2,2,
            0.15f, 0.2f,
            0.25f, 0.3f)
        val w1 by matrixVariable(2,2,
            0.4f, 0.45f,
            0.5f, 0.55f)
        val b1 by variable(0.35f)
        val b2 by variable(0.6f)
        val h by logit((w0 cross inputs) + b1)
        val output by logit((w1 cross h) + b2)
        val target by matrixVariable(1, 2, 0.01f, 0.99f)
        val errors by 0.5f * pow(target-output, 2.0f)
        val error by summation(errors)
        val learningRate = 0.5f
        val dw1 by w1 - learningRate * differentiate(d(error)/d(w1))
        val dw0 by w0 - learningRate * differentiate(d(error)/d(w0))
        val h0 by h[0,0]
        val h1 by h[0,1]
        val o0 by output[0,0]
        val o1 by output[0,1]
        val e0 by errors[0,0]
        val e1 by errors[0,1]

        h0.substituteInitial().reduceArithmetic().assertString("h0=0.59327")
        h1.substituteInitial().reduceArithmetic().assertString("h1=0.59688")

        o0.substituteInitial().reduceArithmetic().assertString("o0=0.75137")
        o1.substituteInitial().reduceArithmetic().assertString("o1=0.77293")

        e0.substituteInitial().reduceArithmetic().assertString("e0=0.27481")
        e1.substituteInitial().reduceArithmetic().assertString("e1=0.02356")

        error.substituteInitial().reduceArithmetic().assertString("error=0.29837")
        dw1.substituteInitial().reduceArithmetic().assertString("""
            dw1
            ------
            0.35892|0.40867
            0.5113|0.56137
        """.trimIndent())
        dw0.substituteInitial().reduceArithmetic().assertString("""
            dw0
            ------
            0.14978|0.19956
            0.24975|0.2995
        """.trimIndent())
    }

    @Test
    fun `learn xor with relu`() {
        val random = Random(1)
        val learningRate = 0.3
        val inputs = 2
        val count0 = 3
        val outputs = 1
        val input by matrixVariable<Double>(1, inputs)
        val left by input[0,0]
        val right by input[0,1]
        val w0 by matrixVariable(input.rows, count0) { abs(random.nextGaussian()) / 3.0 }
        val h1 by lrelu(w0 cross input)
        val w1 by matrixVariable(w0.rows, outputs) { abs(random.nextGaussian()) / 3.0 }
        val output by lrelu(w1 cross h1)
        val target by matrixVariable<Double>(output.columns, output.rows)
        val error by summation(0.5 * pow(target - output, 2.0))
        val dw0 by w0 - learningRate * differentiate(d(error) / d(w0))
        val dw1 by w1 - learningRate * differentiate(d(error) / d(w1))
        val aw0 by assign(dw0 to w0)
        val aw1 by assign(dw1 to w1)
        val targetElement by target[0,0]
        val answer by output[0,0]
        val tab = tableauOf(left,right,targetElement,error,answer,aw0,aw1)
        val layout = tab.linearize()
        println(layout)
        val space = layout.allocateSpace()
        val leftSlot = layout.shape(left).ref(space)
        val rightSlot = layout.shape(right).ref(space)
        val targetSlot = layout.shape(targetElement).ref(space)
        val errorSlot = layout.shape(error).ref(space)
        val outSlot = layout.shape(answer).ref(space)
        fun rev(value : Double) = value > 0.5
        fun repr(value : Boolean) = if (value) 1.0 else 0.0
        fun train(left : Boolean, right : Boolean, target : Boolean) : Double {
            leftSlot.set(repr(left))
            rightSlot.set(repr(right))
            targetSlot.set(repr(target))
            layout.eval(space)
            return outSlot.value
        }
        repeat(100000) {
            repeat(4) { r ->
                val left = rev(abs(random.nextGaussian() / 1.5))
                val right = rev(abs(random.nextGaussian() / 1.5))
                val target = left != right
                val output = train(left, right, target)
            }

            var done = true
            listOf(false, true).forEach { left ->
                listOf(false, true).forEach { right ->
                    val target = left != right
                    val output = train(left, right, target)
                    if (!output.near(repr(target))) done = false
                    if (!done && it % 1000 == 900) {
                        println("$left $right [$errorSlot] -> $output [$target=$targetSlot]")
                    }
                }
            }
            if (done) {
                println("finished in $it iterations")
                return
            }
        }
        error("didn't finish")
    }

    @Test
    fun `rnn`() {
        // https://github.com/nicklashansen/rnn_lstm_from_scratch/blob/master/RNN_LSTM_from_scratch.ipynb
        val r = Random()
        val x by matrixVariable(1, 6) { 0.0 }
        val u by matrixVariable(x.rows, x.rows) { r.nextGaussian() }
        val h1 by matrixVariable(x.columns, x.rows) { r.nextGaussian() }
        val v by matrixVariable(h1.rows, u.rows) { r.nextGaussian() }
        val h2 by tanh((u cross x) + (v cross h1))
        val w by matrixVariable(h2.rows, 5) { r.nextGaussian() }

        val o by w cross h2
        val yhat by softmax(o)
        val step by assign(h2 to h1)
        val model = tableauOf(yhat, o, step).linearize()
        val space = model.allocateSpace()
        model.eval(space)
        val yhatref = model.shape(yhat).ref(space)
        println(model)
        println(yhatref)
    }

    @Test
    fun `softmax`() {
        val output by softmax(matrixOf(1, 3, 1.0, 5.0, 10.0))
        val layout = output.linearize()
        val space = layout.allocateSpace()
        layout.eval(space)
        val outref = layout.shape(output).ref(space)
        println(outref)
    }

    @Test
    fun `exp`() {
        val x by variable<Double>()
        val e2x by exp(x * x)
        val e2xprime by differentiate(d(e2x)/d(x))
        println(e2x)
        println(e2xprime)
    }

    @Test
    fun `divide`() {
        val x by variable<Double>()
        val fx by 4.0 / pow(x, 6.0)
        val dfx by differentiate(d(fx)/d(x))
        fx.assertString("fx=4*x⁻⁶")
        dfx.assertString("dfx=-24*x⁻⁷")
    }

    @Test
    fun `bits in matrixes`() {
        bitsToArray<Double>(3, 8).toList().assertString("[1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]")
        bitsToArray<Double>(3, 8).toRowMatrix().assertString("1|1|0|0|0|0|0|0")
        bitsToArray<Double>(3, 8).toColumnMatrix().assertString("[1|1|0|0|0|0|0|0]ᵀ")
        bitsToArray(3, 8, -1.0, 1.0).toList().assertString("[1.0, 1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0]")
    }

    @Test
    fun `check toFunc`() {
        // https://www.desmos.com/calculator/fdrpen56zk
        val range = 1.0
        val steepnessNearZero = 1.0
        val limitSlope = 0.01
        fun squarsh(x : MatrixExpr<Double>) = (2.0 * range / (1.0 + exp(-steepnessNearZero*x)) - range) + x*limitSlope
        fun squarsh(x : ScalarExpr<Double>) = (2.0 * range / (1.0 + exp(-steepnessNearZero*x)) - range) + x*limitSlope
        val x by variable(100.0)
        val y by squarsh(x)
        val dy by (differentiate(d(y)/d(x)))
        y.assertString("y=((2/e⁻ˣ+1)-1)+0.01*x")
        dy.assertString("dy=2*e⁻ˣ*(e⁻ˣ+1)⁻²+0.01")
        val fx = y.toFunc(x)
        val fxp = dy.toFunc(x)
        fx(0.0).assertString("0.0")
        fx(1.0).assertString("0.4721171572600098")
        fx(-1.0).assertString("-0.4721171572600098")

        fxp(0.0).assertString("0.51")
        fxp(1.0).assertString("0.40322386648296377")
        fxp(1000.0).assertString("0.01") // Should approach limitSlope
        fxp(-1.0).assertString("0.40322386648296377")
    }

    @Test
    fun `autoencode gaussian random into 8 bits`() {
        val type = doubleAlgebraicType
        val random = Random(3)
        val learningRate = 0.1
        val batchSize = 100.0
        val inputs = 1
        val count0 = 1
        val count1 = 8
        val count2 = 1
        val outputs = 1
        val input by matrixVariable(1, inputs, type) { 0.0001 }
        val w0 by matrixVariable(input.rows, count0, type) { random.nextGaussian() }

        val h1 by w0 cross input
        val w1 by matrixVariable(h1.rows, count1, type) { random.nextGaussian() }

        val h2 by logit(w1 cross h1)
        val w2 by matrixVariable(h2.rows, count2, type) { random.nextGaussian() }

        val h3 by w2 cross h2
        val w3 by matrixVariable(h3.rows, outputs, type) { random.nextGaussian() }

        val output by w3 cross h3

        val target by matrixVariable(output.columns, output.rows, type) { 0.0 }
        val error by summation(type.half * pow(target - output, type.two))
        val sumdw0 by matrixVariable(w0.columns,w0.rows,type) { 0.0 }
        val sumdw1 by matrixVariable(w1.columns,w1.rows,type) { 0.0 }
        val sumdw2 by matrixVariable(w2.columns,w2.rows,type) { 0.0 }
        val sumdw3 by matrixVariable(w3.columns,w3.rows,type) { 0.0 }
        val dw0 by sumdw0 + type.coerceFrom(learningRate/batchSize) * differentiate(d(error) / d(w0))
        val dw1 by sumdw1 + type.coerceFrom(learningRate/batchSize) * differentiate(d(error) / d(w1))
        val dw2 by sumdw2 + type.coerceFrom(learningRate/batchSize) * differentiate(d(error) / d(w2))
        val dw3 by sumdw3 + type.coerceFrom(learningRate/batchSize) * differentiate(d(error) / d(w3))
        val adw0 by assign(dw0 to sumdw0)
        val adw1 by assign(dw1 to sumdw1)
        val adw2 by assign(dw2 to sumdw2)
        val adw3 by assign(dw3 to sumdw3)
        val tab = tableauOf(output,error,dw0,dw1,dw2,dw3,adw0,adw1,adw2,adw3)
        val layout = tab.linearize()
        println(layout)
        val space = layout.allocateSpace()
        val targetRef = layout.shape(target).ref(space)
        val errorRef = layout.shape(error).ref(space)
        val outputRef = layout.shape(output).ref(space)
        val inputRef = layout.shape(input).ref(space)
        val w0ref = layout.shape(w0).ref(space)
        val w1ref = layout.shape(w1).ref(space)
        val w2ref = layout.shape(w2).ref(space)
        val w3ref = layout.shape(w3).ref(space)
        val sumdw0ref = layout.shape(sumdw0).ref(space)
        val sumdw1ref = layout.shape(sumdw1).ref(space)
        val sumdw2ref = layout.shape(sumdw2).ref(space)
        val sumdw3ref = layout.shape(sumdw3).ref(space)

        fun train() : Any {
            val roll = abs(random.nextInt(2000))
            val target = if(roll < 1)
                space[abs(random.nextInt()) % space.size]
            else
                type.coerceFrom(random.nextGaussian())
            inputRef.set(target)
            targetRef.set(target)
            layout.eval(space)
            return target
        }

        var lastError = 1000.0
        repeat(100) {
            sumdw0ref.zero()
            sumdw1ref.zero()
            sumdw2ref.zero()
            sumdw3ref.zero()
            var totalError = 0.0
            repeat(batchSize.toInt()) {
                train()
                totalError += errorRef.value
            }
            totalError /= batchSize
            println("error=$totalError")

            w0ref -= sumdw0ref
            w1ref -= sumdw1ref
            w2ref -= sumdw2ref
            w3ref -= sumdw3ref

            if (totalError < lastError) {

                val outlier = space.indices.maxBy { index -> space[index] }
                val file = File("/Users/jomof/IdeaProjects/KotlinRegression/src/test/kotlin/com/github/jomof/kane/rigueur/distribution.txt")

                val func = layout.toFunc(space, h2, output)
                val result = mutableListOf<Pair<Double,String>>()
                (0 until 256).map { value ->
                    val bits = bitsToArray(value, 8, type).toColumnMatrix()
                    val predicted = func(bits)[0,0]

                    result.add(predicted to "$bits")
                }
                if (file.isFile) file.delete()
                file.appendText("$totalError\n")
                result.sortedBy { (predicted,_) -> predicted}.forEach { (predicted,bits) ->
                    file.appendText("$predicted = $bits\n")
                }
                lastError = totalError
            }

            if (it % 100 == 90) {
                repeat(4) {
                    val train = train()
                    println("$train -> $outputRef")
                }

                if (totalError.near(0.0)) return
            }

        }
    }

    @Test
    fun `quarter precision`() {
        ieeeBasis(0).assertString("0.125")
        ieeeBasis(1).assertString("0.125")
        ieeeBasis(7).assertString("0.125")
        ieeeBasis(8).assertString("0.125")
        ieeeBasis(16).assertString("0.25")
        ieeeBasis(24).assertString("0.5")
        ieeeBasis(128+24).assertString("0.5")
        ieeeSignificand(0).assertString("0")
        ieeeSignificand(7).assertString("7")
        ieeeSignificand(8).assertString("0")
        ieeeExponent(9).assertString("1")
        ieeeExponent(16).assertString("2")
        ieeeExponent(0).assertString("0")
        ieeeExponent(7).assertString("0")
        ieeeExponent(8).assertString("1")
        ieeeExponent(9).assertString("1")
        ieeeExponent(16).assertString("2")
        ieeeSign(0).assertString("0")
        ieeeSign(1).assertString("0")
        ieeeSign(127).assertString("0")
        ieeeSign(128).assertString("1")
        ieeeSign(255).assertString("1")
        ieeeBasis(23).assertString("0.25")
        ieeeSignificand(23).assertString("7")
        ieeeExponent(23).assertString("2")
        ieeeStart(23).assertString("2.0")
        ieeeAdd(23).assertString("1.75")


        ieee(23).assertString("3.75")
        ieee(24).assertString("4.0")
        ieee(25).assertString("4.5")
        ieee(8).assertString("1.0")
        ieee(16).assertString("2.0")
        ieee(17).assertString("2.25")
        ieee(0).assertString("0.0")
        ieee(1).assertString("0.125")

        ieee(255-8).assertString("-15360.0")

        ieeeExponent(15 * 8).assertString("15")
        ieeeSignificand(15 * 8).assertString("0")
        ieee(15 * 8).assertString("Infinity")
        ieee(128 + 15 * 8).assertString("-Infinity")
        ieee(15 * 8 + 1).assertString("NaN")
        ieee(128 + 15 * 8 + 1).assertString("NaN")

        doubleToByteIndex(0.0).assertString("-128")
    }

    @Test
    fun `learn xor with logistic`() {
        val random = Random(1)
        val learningRate = 7.0
        val inputs = 2
        val count0 = 4
        val outputs = 1
        val input by matrixVariable(1, inputs) { 0.0 }
        val left by input[0,0]
        val right by input[0,1]
        val w0 by matrixVariable(input.rows, count0) { random.nextGaussian() / 3.0 }
        val h1 by logit(w0 cross input)
        val w1 by matrixVariable(w0.rows, outputs) { random.nextGaussian() / 3.0 }
        val output by logit(w1 cross h1)
        val target by matrixVariable(output.columns, output.rows) { 0.0 }
        val error by summation(0.5 * pow(target - output, 2.0))
        val dw0 by w0 - learningRate * differentiate(d(error) / d(w0))
        val dw1 by w1 - learningRate * differentiate(d(error) / d(w1))
        val aw0 by assign(dw0 to w0)
        val aw1 by assign(dw1 to w1)
        val targetElement by target[0,0]
        val answer by output[0,0]
        val tab = tableauOf(left,right,targetElement,error,answer,aw0,aw1,dw0,dw1)
        val layout = tab.linearize()
        println(layout)
        layout.check()

        val space = layout.allocateSpace()
        val leftRef = layout.shape(left).ref(space)
        val rightRef = layout.shape(right).ref(space)
        val targetRef = layout.shape(targetElement).ref(space)
        val errorRef = layout.shape(error).ref(space)
        val outRef = layout.shape(answer).ref(space)
        fun repr(value : Boolean) = if (value) 0.9 else 0.1
        fun train(left : Boolean, right : Boolean, target : Boolean) : Double {
            leftRef.set(repr(left))
            rightRef.set(repr(right))
            targetRef.set(repr(target))
            layout.eval(space)
            return outRef.value
        }
        repeat(100000) {
            listOf(false, true).forEach { left ->
                listOf(false, true).forEach { right ->
                    val target = left != right
                    val output = train(left, right, target)
                    if (it % 1000 == 900) {
                        println("$left $right [$errorRef] -> $output [$target=${targetRef}]")
                    }
                }
            }
            var done = true
            listOf(false, true).forEach { left ->
                listOf(false, true).forEach { right ->
                    val target = left != right
                    val output = train(left, right, target)
                    if (!output.near(repr(target))) done = false
                }
            }
            if (done) {
                println("finished in $it iterations")
                return
            }
        }
        error("didn't finish")
    }

    @Test
    fun `repro multiplication bug`() {
        val input by matrixVariable<Double>(1, 1)
        val w0 by matrixVariable<Double>(input.rows + 1, 3)
        val h1 by logit(w0 cross (input stack 1.0))
        val w1 by matrixVariable<Double>(w0.rows + 1, 1)
        val output by logit(w1 cross (h1 stack 1.0))
        val target by matrixVariable<Double>(output.columns, output.rows)
        val error by summation(0.5 * pow(target - output, 2.0))
        val gradientW0 by d(error) / d(w0)
        differentiate(gradientW0)
    }

    @Test
    fun `summation expands on element access`() {
        val a by matrixVariable<Double>(1,3)
        val b by matrixVariable<Double>(3,2)
        val s by summation(a) / b
        s[0,0].assertString("(a[0,0]+a[0,1]+a[0,2])/b[0,0]")
    }

    @Test
    fun `matrix element rendering`() {
        val m by matrixVariable<Double>(2,3)
        val e by m[1,1]
        e.assertString("e=m[1,1]")
        (matrixVariable<Double>(2,3)).assertString("matrixVariable(2x3)")
        val p by pow(m, 2.0)
        p.assertString("p=pow(m,2)")
        val pe by p[1,2]
        pe.assertString("pe=m[1,2]²")
        p[1,2].assertString("m[1,2]²")
        val column by matrixVariable<Double>(1,3)
        val s by 1.0 stack column
        s.assertString("s=[1] stack column")
        s[0,0].assertString("1")
        s[0,1].assertString("column[0,0]")
        val a1 by s[0,0]
        a1.assertString("a1=1")
    }

    @Test
    fun `requires parens`() {
        fun check(parent : BinaryOp, child : BinaryOp, childIsRight: Boolean, expect : Boolean?) {
            val result = binaryRequiresParents(parent, child, childIsRight)
            assert(result == expect) {
                binaryRequiresParents(parent, child, childIsRight)
                "$result != $expect"
            }
        }
        check(DIV, POW, childIsRight = false, expect = false)
        check(DIV, POW, childIsRight = true, expect = false)
        check(POW, PLUS, childIsRight = false, expect = true)
        check(POW, PLUS, childIsRight = true, expect = false)
        check(MINUS, PLUS, childIsRight = false, expect = false)
        check(MINUS, PLUS, childIsRight = true, expect = true)
        check(PLUS, PLUS, childIsRight = false, expect = false)
        check(PLUS, PLUS, childIsRight = true, expect = false)
        check(TIMES, TIMES, childIsRight = false, expect = false)
        check(TIMES, TIMES, childIsRight = true, expect = false)
        check(MINUS, MINUS, childIsRight = false, expect = false)
        check(MINUS, MINUS, childIsRight = true, expect = true)
        check(PLUS, MINUS, childIsRight = false, expect = true)
        check(PLUS, MINUS, childIsRight = true, expect = true)
        check(PLUS, TIMES, childIsRight = false, expect = false)
        check(PLUS, TIMES, childIsRight = true, expect = false)
        check(MINUS, TIMES, childIsRight = false, expect = false)
        check(MINUS, TIMES, childIsRight = true, expect = false)
    }

    @Test
    fun `variable rendering`() {
        variable<Double>().assertString("?")
        variable<Int>().assertString("?")
        val a by variable<Double>()
        a.assertString("a")
        val b by variable<Double>()
        (a + b).assertString("a+b")
        val c by a + b
        c.assertString("c=a+b")
        (a * -1.0).assertString("a*-1")
        (a * b).assertString("a*b")
        (a - b).assertString("a-b")
        (a / b).assertString("a/b")
        (a + a * b).assertString("a+a*b")
        ((a + a) * b).assertString("(a+a)*b")
        ((a - a) * b).assertString("(a-a)*b")
        (a - a * b).assertString("a-a*b")
        (a / a * b).assertString("(a/a)*b")
        (a / (a * b)).assertString("a/(a*b)")
        ((a / a) * b).assertString("(a/a)*b")
        (a + a / b).assertString("a+a/b")
        ((a + a) / b).assertString("(a+a)/b")
        ((a - a) / b).assertString("(a-a)/b")
        (a + a - b).assertString("a+a-b")
        ((a + a) - b).assertString("a+a-b")
        ((a - a) - b).assertString("a-a-b")
        (a - (a - b)).assertString("a-(a-b)")
        (a - -b).reduceArithmetic().assertString("a+b")
        val m : MatrixExpr<Double> by matrixVariable<Double>(5, 2)
        val n : MatrixExpr<Double> by matrixVariable<Double>(3, 5)
        m.assertString("m")
        n.assertString("n")
        val z by m
        z.assertString("z=m")
        val element by z[0,1]
        element.assertString("element=m[0,1]")
        z[1,0].assertString("m[1,0]")
        val mult by m cross n
        mult.assertString("mult=m cross n")
        (m cross n).assertString("m cross n")
        (m stack 1.0).assertString("m stack 1|1|1|1|1")
        (m stack m stack m).assertString("m stack m stack m")
        ((m stack m) stack m).assertString("m stack m stack m")
        (m stack (m stack m)).assertString("m stack m stack m")
    }

    @Test
    fun `variable negation rendering`() {
        val a by variable<Double>()
        val b by variable<Double>()
        (-b * -b * -a).reduceArithmetic().assertString("-a*b²")
        (-b * -b).reduceArithmetic().assertString("b²")
        (-b * -a * -b).reduceArithmetic().assertString("-a*b²")
        (-a).assertString("-a")
        (-(a + b)).assertString("-(a+b)")
        (-(a * b)).assertString("-a*b")
        (-1.0 * b).reduceArithmetic().assertString("-b")
        (b * -1.0).reduceArithmetic().assertString("-b")
    }

    @Test
    fun `superscript rendering`() {
        val a by variable<Double>()
        val b by variable<Double>()
        val x by variable<Double>()
        val M by variable<Double>()
        (pow(a, -1.0)).reduceArithmetic().assertString("a⁻¹")
        (pow(a, b)).reduceArithmetic().assertString("aᵇ")
        (pow(a, M)).reduceArithmetic().assertString("aᴹ")
        (pow(a, -b)).reduceArithmetic().assertString("a⁻ᵇ")
        (pow(a, b-b)).reduceArithmetic().assertString("a⁽ᵇ⁻ᵇ⁾")
        (exp(x)).reduceArithmetic().assertString("eˣ")
        (exp(-x)).reduceArithmetic().assertString("e⁻ˣ")
    }

    @Test
    fun `matrix chain rule case 8668`() {
        val m by matrixVariable<Double>(1, 2)
        val x by m[0, 0]
        val y by m[0, 1]
        val f1 by pow(x, 2.0) + pow(y, 3.0)
        val d1 by d(f1) / d(x)
        differentiate(d1).assertString("d1'=2*m[0,0]")
    }

    @Test
    fun `matrix chain rule`() {
        val m by matrixVariable<Double>(1, 2)
        val x by m[0,0]
        val y by m[0,1]
        val f1 by pow(x, 2.0) + pow(y, 3.0)
        f1.assertString("f1=m[0,0]²+m[0,1]³")
        val d1 by d(f1) / d(x)
        d1.assertString("d1=d(m[0,0]²+m[0,1]³)/d(m[0,0])")
        differentiate(d1).assertString("d1'=2*m[0,0]")
        val d2 by d(f1) / d(y)
        d2.assertString("d2=d(m[0,0]²+m[0,1]³)/d(m[0,1])")
        differentiate(d2).assertString("d2'=3*m[0,1]²")
        val f2 by pow(x, 2.0) * pow(y, 3.0)
        f2.assertString("f2=m[0,0]²*m[0,1]³")
        val df2dx by d(f2) / d(x)
        df2dx.assertString("df2dx=d(m[0,0]²*m[0,1]³)/d(m[0,0])")
        differentiate(df2dx).assertString("df2dx'=2*m[0,0]*m[0,1]³")
        val df2dy by d(f2) / d(y)
        df2dy.assertString("df2dy=d(m[0,0]²*m[0,1]³)/d(m[0,1])")
        differentiate(df2dy).assertString("df2dy'=3*m[0,0]²*m[0,1]²")
        val assigned by differentiate(df2dy)
        assigned.assertString("assigned=3*m[0,0]²*m[0,1]²")
        val f3 by logit(x * y)
        f3.assertString("f3=logit(m[0,0]*m[0,1])")
        val df3dx by d(f3) / d(x)
        df3dx.assertString("df3dx=d(logit(m[0,0]*m[0,1]))/d(m[0,0])")
        differentiate(df3dx).assertString("df3dx'=logit(m[0,0]*m[0,1])*(1-logit(m[0,0]*m[0,1]))*m[0,1]")
        val df3dy by d(f3) / d(y)
        df3dy.assertString("df3dy=d(logit(m[0,0]*m[0,1]))/d(m[0,1])")
        differentiate(df3dy).assertString("df3dy'=logit(m[0,0]*m[0,1])*(1-logit(m[0,0]*m[0,1]))*m[0,0]")
    }

    @Test
    fun `chain rule`() {
        val x by variable<Double>()
        val y by variable<Double>()
        val f1 by pow(x, 2.0) + pow(y, 3.0)
        f1.assertString("f1=x²+y³")
        val d1 by d(f1) / d(x)
        d1.assertString("d1=d(x²+y³)/dx")
        differentiate(d1).assertString("d1'=2*x")
        val d2 by d(f1) / d(y)
        d2.assertString("d2=d(x²+y³)/dy")
        differentiate(d2).assertString("d2'=3*y²")
        val f2 by pow(x, 2.0) * pow(y, 3.0)
        f2.assertString("f2=x²*y³")
        val df2dx by d(f2) / d(x)
        df2dx.assertString("df2dx=d(x²*y³)/dx")
        differentiate(df2dx).assertString("df2dx'=2*x*y³")
        val df2dy by d(f2) / d(y)
        df2dy.assertString("df2dy=d(x²*y³)/dy")
        differentiate(df2dy).assertString("df2dy'=3*x²*y²")
        val assigned by differentiate(df2dy)
        assigned.assertString("assigned=3*x²*y²")
        val f3 by logit(x * y)
        f3.assertString("f3=logit(x*y)")
        val df3dx by d(f3) / d(x)
        df3dx.assertString("df3dx=d(logit(x*y))/dx")
        differentiate(df3dx).assertString("df3dx'=logit(x*y)*(1-logit(x*y))*y")
        val df3dy by d(f3) / d(y)
        df3dy.assertString("df3dy=d(logit(x*y))/dy")
        differentiate(df3dy).assertString("df3dy'=logit(x*y)*(1-logit(x*y))*x")
    }

    @Test
    fun `test differentiate case 774036348`() {
        val learningRate = 0.1
        val input by matrixVariable<Double>(1, 2)
        val w0 by matrixVariable<Double>(input.rows + 1, 2)
        val h1 by logit(w0 cross (input stack 1.0))
        val w1 by matrixVariable<Double>(w0.rows + 1, 2)
        val output by logit(w1 cross (h1 stack 1.0))
        val target by matrixVariable<Double>(output.columns, output.rows)
        val error by summation(learningRate * pow(target - output, 2.0))
        val gradientW0 by d(error) / d(w0)
        differentiate(gradientW0)
    }


    // Try various things to see if we can make it crash
    private fun spamExpression(expr : UntypedExpr) {
        if (expr !is Expr<*>) return
        fun identifierOf(structure : String) = structure.substringAfter("val ").substringBefore(" ")
        expr.toString()

        val structure = expr.renderStructure()
        val id = identifierOf(structure)
        assert(expr == expr)
        expr.memoizeAndReduceArithmetic()
        if (expr.type == doubleAlgebraicType) {
            if (expr is NamedExpr<*>) {
                tableauOf(expr).linearize()
            }
        } else {
            assert(false) { "${expr.type}"}
        }

        try {
            differentiate(expr)
        } catch (e : Exception) {
            val sb = StringBuilder()
            expr.renderStructure()
            sb.append("@Test\n")
            sb.append("fun `test differentiate case ${structure.hashCode().absoluteValue}`() {\n")
            sb.append("    $structure\n")
            sb.append("    differentiate($id).assertString(\"\")\n")
            sb.append("}\n")
            println(sb)
            throw e
        }
        try {
            differentiate(expr).render()
        } catch (e : Exception) {
            val sb = StringBuilder()
            expr.renderStructure()
            sb.append("@Test\n")
            sb.append("fun `test differentiate case ${structure.hashCode().absoluteValue}`() {\n")
            sb.append("    $structure\n")
            sb.append("    differentiate($id).assertString(\"\")\n")
            sb.append("}\n")
            println(sb)
            throw e
        }

        if(expr is MatrixExpr<*>) {
            for(column in 0 until expr.columns) {
                for (row in 0 until expr.rows) {
                    try {
                        expr[column, row]
                    } catch (e : Exception) {
                        val sb = StringBuilder()
                        sb.append("@Test\n")
                        sb.append("fun `test matrix element case ${structure.hashCode().absoluteValue}`() {\n")
                        sb.append("    $structure\n")
                        sb.append("    $id[$column,$row].assertString(\"\")\n")
                        sb.append("}\n")
                        println(sb)
                        throw e
                    }
                }
            }
        }
    }

    @Test
    fun `generate new tests`() = trackExprs().use {
        val v1 by variable<Double>()
        val x1 by matrixVariable<Double>(1,1)
        val x2: ScalarExpr<Double> by pow(v1, 1.0)
        val x3: ScalarExpr<Double> by pow(v1, v1)
        val x4: MatrixExpr<Double> by pow(x1, v1)
        val x7: ScalarExpr<Double> by x4[0, 0]
        val x9: ScalarExpr<Double> by logit(v1)
        val x10: ScalarExpr<Double> by v1 * 1.0
        val x11: ScalarExpr<Double> by 1.0 * v1
        val x12: ScalarExpr<Double> by v1 * v1
        val x13: MatrixExpr<Double> by 1.0 * x1
        val x14: MatrixExpr<Double> by x1 * 1.0
        val x15: MatrixExpr<Double> by x1 cross x1
        val x16: MatrixExpr<Double> by x1 stack 1.0
        val x17: MatrixExpr<Double> by 1.0 stack x1
        val x18: MatrixExpr<Double> by x1 stack x1
        val x19: MatrixExpr<Double> by x1 stack v1
        val x20: MatrixExpr<Double> by v1 stack x1
        val x22: MatrixExpr<Double> by v1 * x1
        val x23: MatrixExpr<Double> by x1 * v1
        val x24: MatrixExpr<Double> by logit(x1)
        val x25: ScalarExpr<Double> by variable<Double>()
        val x26: ScalarExpr<Double> by variable<Double>()
        val x28: ScalarExpr<Double> by v1 + v1
        val x29: ScalarExpr<Double> by v1 * v1
        val learningRate = 0.1
        val input by matrixVariable<Double>(1, 2)
        val w0 by matrixVariable<Double>(input.rows + 1, 2)
        val h1 by logit(w0 cross (input stack 1.0))
        val w1 by matrixVariable<Double>(w0.rows + 1, 2)
        val output by logit(w1 cross (h1 stack 1.0))
        val target by matrixVariable<Double>(output.columns, output.rows)
        val error by summation(learningRate * pow(target - output, 2.0))
        val gradientW0 by d(error) / d(w0)
        val gradientW1 by d(error) / d(w1)
        val x by variable<Double>()
        val q by variable<Double>()
        val m by variable<Double>()
        val b by variable<Double>()
        val y by q * pow(x,2.0) + m * x + b
        val polyTarget by variable<Double>()
        val polyError = pow(y - polyTarget, 2.0)
        val dq by differentiate(d(polyError)/d(q))
        val dm by differentiate(d(polyError)/d(m))
        val db by differentiate(d(polyError)/d(b))
        val all = listOf(x2, x3, x4, x7, x9, x10, x11, x12, x13, x14, x15, x16, x17, x18,
                         x19, x20, x22, x23, x24, x25, x26, x28, x29,
                         input, w0, h1, w1, output, target, error, gradientW0, gradientW1,
                         x, q, m, b, y, polyTarget, polyError, dq, dm, db)

        val next = mutableSetOf<UntypedExpr>()
        val seen = mutableSetOf<UntypedExpr>()
        next += all
        next += getRecordedExprs()

        while (next.isNotEmpty()) {
            for (expr in next.toList()) {
                spamExpression(expr)
                seen += expr
            }
            next += getRecordedExprs()
            next -= seen
        }
    }
}

