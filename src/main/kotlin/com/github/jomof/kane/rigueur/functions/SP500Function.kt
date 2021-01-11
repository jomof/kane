package com.github.jomof.kane.rigueur.functions

import com.github.jomof.kane.rigueur.*

// %gain, including dividends at end of year
private val sp500Map = mapOf(
    1928 to 0.4381,
    1929 to -0.083,
    1930 to -0.2512,
    1931 to -0.4384,
    1932 to -0.0864,
    1933 to 0.4998,
    1934 to -0.0119,
    1935 to 0.4674,
    1936 to 0.3194,
    1937 to -0.3534,
    1938 to 0.2928,
    1939 to -0.011,
    1940 to -0.1067,
    1941 to -0.1277,
    1942 to 0.1917,
    1943 to 0.2506,
    1944 to 0.1903,
    1945 to 0.3582,
    1946 to -0.0843,
    1947 to 0.052,
    1948 to 0.057,
    1949 to 0.183,
    1950 to 0.3081,
    1951 to 0.2368,
    1952 to 0.1815,
    1953 to -0.0121,
    1954 to 0.5256,
    1955 to 0.326,
    1956 to 0.0744,
    1957 to -0.1046,
    1958 to 0.4372,
    1959 to 0.1206,
    1960 to 0.0034,
    1961 to 0.2664,
    1962 to -0.0881,
    1963 to 0.2261,
    1964 to 0.1642,
    1965 to 0.124,
    1966 to -0.0997,
    1967 to 0.238,
    1968 to 0.1081,
    1969 to -0.0824,
    1970 to 0.0356,
    1971 to 0.1422,
    1972 to 0.1876,
    1973 to -0.1431,
    1974 to -0.259,
    1975 to 0.37,
    1976 to 0.2383,
    1977 to -0.0698,
    1978 to 0.0651,
    1979 to 0.1852,
    1980 to 0.3174,
    1981 to -0.047,
    1982 to 0.2042,
    1983 to 0.2234,
    1984 to 0.0615,
    1985 to 0.3124,
    1986 to 0.1849,
    1987 to 0.0581,
    1988 to 0.1654,
    1989 to 0.3148,
    1990 to -0.0306,
    1991 to 0.3023,
    1992 to 0.0749,
    1993 to 0.0997,
    1994 to 0.0133,
    1995 to 0.372,
    1996 to 0.2268,
    1997 to 0.331,
    1998 to 0.2834,
    1999 to 0.2089,
    2000 to -0.0903,
    2001 to -0.1185,
    2002 to -0.2197,
    2003 to 0.2836,
    2004 to 0.1074,
    2005 to 0.0483,
    2006 to 0.1561,
    2007 to 0.0548,
    2008 to -0.3655,
    2009 to 0.2594,
    2010 to 0.1482,
    2011 to 0.021,
    2012 to 0.1589,
    2013 to 0.3215,
    2014 to 0.1352,
    2015 to 0.0138,
    2016 to 0.1177,
    2017 to 0.2161,
    2018 to -0.0423,
    2019 to 0.3122 
)

private fun sp500(year : Int) : Double {
    return sp500Map[year]!!
}
private val SP500 by UnaryOp()

private class SP500Function : AlgebraicUnaryScalarFunction {
    override val meta = SP500
    override fun doubleOp(value: Double) = sp500(value.toInt())
    override fun floatOp(value: Float) = sp500(value.toInt()).toFloat()

    override fun <E : Number> reduceArithmetic(value: ScalarExpr<E>) : ScalarExpr<E>? {
        val constValue = value.tryFindConstant()
        return when {
            constValue != null -> constant(sp500(constValue.toInt()) as E)
            else -> value
        }
    }

    override fun <E : Number> differentiate(
        expr : ScalarExpr<E>,
        exprd : ScalarExpr<E>,
        variable : ScalarExpr<E>
    ) = error("not differentiable")
}

val sp500 : AlgebraicUnaryScalarFunction = SP500Function()