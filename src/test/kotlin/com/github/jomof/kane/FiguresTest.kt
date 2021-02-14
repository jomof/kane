package com.github.jomof.kane

import com.github.jomof.kane.impl.differentiate
import com.github.jomof.kane.impl.functions.d
import com.github.jomof.kane.impl.variable
import jetbrains.letsPlot.export.ggsave
import jetbrains.letsPlot.geom.geom_density
import jetbrains.letsPlot.geom.geom_point
import jetbrains.letsPlot.ggsize
import jetbrains.letsPlot.intern.Plot
import jetbrains.letsPlot.label.labs
import jetbrains.letsPlot.lets_plot
import org.junit.Test
import java.io.File
import java.lang.Math.PI
import java.util.*

class FiguresTest {
    private val images = File("./figures").absolutePath

    @Test
    fun `README kernel density plot`() {
        val rand = Random(7)
        val sheet = sheetOf {
            val rating by List(200) { rand.nextGaussian() } + List(200) { rand.nextGaussian() * 1.5 + 1.5 }
            val cond by List(200) { "A" } + List(200) { "B" }
            listOf(rating, cond)
        }

        var p: Plot = lets_plot(sheet.toMap())

        p += geom_density(color = "dark_green", alpha = .3) { x = "rating"; fill = "cond" }
        p += ggsize(500, 250)

        ggsave(p, "readme-density.svg", path = images)

    }

    @Test
    fun `unary function plots`() {
        for (func in Kane.unaryFunctions) {

            val x by (-200..200).map { (PI * it) / 100.0 }
            val y by func(x)
            val op = func.meta.simpleName
            val map = y.toMap()
            val p = lets_plot(map) +
                    geom_point { this.x = "x"; this.y = "y"; color = "y" } +
                    labs(
                        title = "$y profile",
                        x = "$op argument",
                        y = "$op value",
                        color = "$op value"
                    )
            ggsave(p, "$op-profile.svg", path = images)
        }
    }

    @Test
    fun `derivative unary function plots`() {
        for (func in Kane.unaryFunctions) {
            val x by variable()
            val y by func(x)
            val d by differentiate(d(y) / d(x))
            println("$y -> $d")
        }
    }

    @Test
    fun `README standalone equation`() {
        val x by (-200..200).map { (3.14 * it) / 100.0 }
        val y by sin(exp(x) / 20.0)
        val p = lets_plot(y.toMap()) +
                geom_point { this.x = "x"; this.y = "y"; color = "y" } +
                labs(
                    title = "$y",
                )
        ggsave(p, "readme-standalone-equation.svg", path = images)
    }
}