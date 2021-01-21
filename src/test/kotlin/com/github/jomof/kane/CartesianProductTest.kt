package com.github.jomof.kane

import org.junit.Test

class CartesianProductTest {
    @Test
    fun `indices check`() {
        val sb = StringBuilder()
        cartesianOfIndices(listOf(1,2,3)) { (a,b,c) ->
            sb.append("$a,$b,$c\n")
        }
        sb.assertString("""
            0,0,0
            0,0,1
            0,0,2
            0,1,0
            0,1,1
            0,1,2
        """.trimIndent())
    }

    @Test
    fun `cartesian check`() {
        val sb = StringBuilder()
        cartesianOf(
            listOf(
                listOf(1,2,3),
                listOf('m','n'),
                listOf("z"),
            )) { (a,b,c) ->
            sb.append("$a,$b,$c\n")
        }
        sb.assertString("""
            1,m,z
            1,n,z
            2,m,z
            2,n,z
            3,m,z
            3,n,z
        """.trimIndent())
    }
}