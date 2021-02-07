package com.github.jomof.kane

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
            lateinit var equation: String
            val data = sheetOf {
                val x by (-100..100).map { (PI * it) / 100.0 }
                val y by func(x)
                equation = "$y"
                listOf(x, y)
            }.toMap()

            val op = func.meta.simpleName

            run {
                val p = lets_plot(data) +
                        geom_point { x = "x"; y = "y"; color = "y" } +
                        labs(
                            title = "$equation profile",
                            x = "$op argument",
                            y = "$op value",
                            color = "$op value"
                        )
                ggsave(p, "$op-profile.svg", path = images)
            }
        }
    }
}