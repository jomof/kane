package com.github.jomof.kane

import jetbrains.letsPlot.export.ggsave
import jetbrains.letsPlot.geom.geom_density
import jetbrains.letsPlot.ggsize
import jetbrains.letsPlot.intern.Plot
import jetbrains.letsPlot.lets_plot
import org.junit.Test
import java.io.File
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
        sheet.head().assertString(
            """
                  rating [A] cond [B] 
                  ---------- -------- 
                1    0.84521        A 
                2    0.91288        A 
                3   -0.28708        A 
                4    0.75186        A 
                5    1.33547        A 
            """.trimIndent()
        )

        val map = sheet.toMap()
        var p: Plot = lets_plot(map)

        p += geom_density(color = "dark_green", alpha = .3) { x = "rating"; fill = "cond" }
        p += ggsize(500, 250)

        ggsave(p, "readme-density.svg", path = images)

    }
}