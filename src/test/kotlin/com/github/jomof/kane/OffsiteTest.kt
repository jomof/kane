package com.github.jomof.kane

import com.github.jomof.kane.functions.*
import com.github.jomof.kane.sheet.sheetOf
import com.github.jomof.kane.types.dollars
import com.github.jomof.kane.types.percent
import org.junit.Test
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.io.File
import java.util.zip.ZipFile


class OffsiteTest {

            val o1 by a1 - start + modelStartYear
            //val o1 by columnOf(allYears) { randomOf(1928.0 to 2019.0) }
            val p1 by sp500(o1)
            val q1 by baaCorporateBond(o1)
            val r1 by usInflation(o1)

            val bend = 0.994 // Lifestyle deflation due to slowing down
            val s3 by columnOf(11) { percent(bend) }
            val s14 by columnOf(10) { percent(bend) }
            val s24 by columnOf(10) { percent(bend) }
            val s34 by columnOf(10) { percent(bend) }
            val s44 by columnOf(6) { percent(bend) }
            val t1 by mean(step(preTax + postTax))
            val u1 by columnOf(allYears) { intercept + slope * it }

            listOf(
                year, j, k, a1, b1, c1, total, d1, e1, f1, g1, h1, i1, j1, k3, l1, l6,
                m19, n21, o1, modelYear, `sp500%`, `bond%`, `inflation%`, p1, q1, r1, s3, t1,
                s14, s24, s34, s44, survival, u1
            )
        }
        repeat(1) { println(retire.eval()) }
//        var best = 5.0
//        var bestSurvival = 0.0
//        for (m in listOf(-0.002, -0.001, 0.0, 0.001).shuffled()) {
//            for (b in listOf(0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5, 0.55, 0.6, 0.65, 0.70, 0.75, 0.8, 0.85, 0.9, 1.0).sortedDescending()) {
//                val adjusted = retire.cells.toMutableMap()
//                adjusted["slope"] = percent(m)
//                adjusted["intercept"] = percent(b)
//                val check = Sheet.of(retire.columnDescriptors, adjusted).eval()
//                //println(check.eval())
//                val survival = (check["T49"] as ConstantScalar).value
//                val endTotal = (check["F49"] as ScalarStatistic).statistic.median
//                if (survival > 0.9) {
//                    if (endTotal > best) {
//                        println("m=$m, b=$b, survival=${check["T49"]}, total=${check["F49"]}")
//                        best = endTotal
//                        bestSurvival = survival
//                        //println(check)
//                    }
//                } else if (survival > bestSurvival) {
//                    println("m=$m, b=$b, survival=${check["T49"]}, total=${check["F49"]}")
//                    best = endTotal
//                    bestSurvival = survival
//                }
//            }
//        }
    }
}