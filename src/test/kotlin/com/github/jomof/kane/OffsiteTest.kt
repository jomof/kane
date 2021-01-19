package com.github.jomof.kane

import com.github.jomof.kane.functions.*
import com.github.jomof.kane.sheet.*
import com.github.jomof.kane.types.dollars
import com.github.jomof.kane.types.percent
import org.junit.Test

class OffsiteTest {

    @Test
    fun `budget calculator`() {
        val start = 2021
        val end = start + 30
        val retireYear = 2023
        val four01k by dollars(26_000)
        val retirementSpend by dollars(100_000)
        val fcollege by dollars(70_000)
        val ncollege by dollars(35_000)
        val wealthFrontIRA by dollars(837_000)
        val google401k by dollars(458_000)
        val s8profitSharing by dollars(244_000)
        val affirma401K by dollars(180_408)
        val f529plan by dollars(89_443)
        val n529plan by dollars(60_167)
        val wealthFrontCash by dollars(10_500)
        val capitalOne by dollars(49_000)
        val chase by dollars(20_000)
        val wealthFrontBrokerage by dollars(730_000)
        val wealthFrontRoth by dollars(50_000)
        val homeEquity by dollars(934_000)
        val coinbase by dollars(270_000)
        val initialPostTax by wealthFrontCash + capitalOne + chase + wealthFrontBrokerage + wealthFrontRoth + homeEquity + coinbase
        val initialPreTax by wealthFrontIRA + google401k + s8profitSharing + affirma401K + f529plan + n529plan
        val allYears = end - start + 1
        val yearsUntilRetirement = retireYear - start
        val yearsOfRetirement = end - retireYear + 1
        val modelStartYear by constant(1929)
        val stock by percent(0.7)
        val retire = sheetOf {
            val year by range("A")
            val j by range("B")
            val k by range("C")
            val postTax by range("D")
            val preTax by range("E")
            val total by range("F")
            val postTaxGrowth by range("G")
            val preTaxGrowth by range("H")
            val j401k by range("I")
            val k401k by range("J")
            val expenses by range("K")
            val college by range("L")
            val modelYear by range("M")
            val `sp500%` by range("N")
            val `bond%` by range("O")
            val `inflation%` by range("P")

            val a1 by columnOf(allYears) { constant(start + it) }
            val b1 by a1 - 1969
            val c1 by a1 - 1972

            val d1 by columnOf(allYears) {
                if (it == 0) initialPostTax
                else postTax.up + postTaxGrowth.up - expenses.up / 2.0 - college.up
            }
            val e1 by columnOf(allYears) {
                if (it == 0) initialPreTax
                else preTax.up + preTaxGrowth.up + j401k.up + k401k.up - expenses.up / 2.0
            }
            val f1 by columnOf(allYears) { preTax + postTax }
            val g1 by (d1 * `sp500%` * stock + d1 * `bond%` * (1.0 - stock)) * (1.0 - `inflation%`)
            val h1 by (e1 * `sp500%` * stock + e1 * `bond%` * (1.0 - stock)) * (1.0 - `inflation%`)
            val i1 by columnOf(yearsUntilRetirement) { four01k }
            val j1 by columnOf(yearsUntilRetirement) { four01k }
            val l1 by columnOf(4) { fcollege }
            val l6 by columnOf(4) { ncollege }
            val k3 by columnOf(yearsOfRetirement) { retirementSpend }

            val m1 by a1 - start + modelStartYear
            val n1 by sp500(m1)
            val o1 by baaCorporateBond(m1)
            val p1 by usInflation(m1)

            add(year, j, k, a1, b1, c1, total, d1, e1, f1, g1, h1, i1, j1, k3, l1, l6,
                m1, n1, o1, modelYear, `sp500%`, `bond%`, `inflation%`, p1)
        }
        println(retire.eval())
        /*
            retire.monteCarlo(
            )
         */
    }
}