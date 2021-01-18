package com.github.jomof.kane.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.UnaryOp
import com.github.jomof.kane.constant
import com.github.jomof.kane.tryFindConstant
private val usInflationMap = mapOf(
    1916 to 0.079,
    1917 to 0.174,
    1918 to 0.18,
    1919 to 0.146,
    1920 to 0.156,
    1921 to -0.105,
    1922 to -0.061,
    1923 to 0.018,
    1924 to 0.0,
    1925 to 0.023,
    1926 to 0.011,
    1927 to -0.017,
    1928 to -0.017,
    1929 to 0.0,
    1930 to -0.023,
    1931 to -0.09,
    1932 to -0.099,
    1933 to -0.051,
    1934 to 0.031,
    1935 to 0.022,
    1936 to 0.015,
    1937 to 0.036,
    1938 to -0.021,
    1939 to -0.014,
    1940 to 0.007,
    1941 to 0.05,
    1942 to 0.109,
    1943 to 0.061,
    1944 to 0.017,
    1945 to 0.023,
    1946 to 0.083,
    1947 to 0.144,
    1948 to 0.081,
    1949 to -0.012,
    1950 to 0.013,
    1951 to 0.079,
    1952 to 0.019,
    1953 to 0.008,
    1954 to 0.007,
    1955 to -0.004,
    1956 to 0.015,
    1957 to 0.033,
    1958 to 0.028,
    1959 to 0.007,
    1960 to 0.017,
    1961 to 0.01,
    1962 to 0.01,
    1963 to 0.013,
    1964 to 0.013,
    1965 to 0.016,
    1966 to 0.029,
    1967 to 0.031,
    1968 to 0.042,
    1969 to 0.055,
    1970 to 0.057,
    1971 to 0.044,
    1972 to 0.032,
    1973 to 0.062,
    1974 to 0.11,
    1975 to 0.091,
    1976 to 0.058,
    1977 to 0.065,
    1978 to 0.076,
    1979 to 0.113,
    1980 to 0.135,
    1981 to 0.103,
    1982 to 0.062,
    1983 to 0.032,
    1984 to 0.043,
    1985 to 0.036,
    1986 to 0.019,
    1987 to 0.036,
    1988 to 0.041,
    1989 to 0.048,
    1990 to 0.054,
    1991 to 0.042,
    1992 to 0.03,
    1993 to 0.03,
    1994 to 0.026,
    1995 to 0.028,
    1996 to 0.03,
    1997 to 0.023,
    1998 to 0.016,
    1999 to 0.022,
    2000 to 0.034,
    2001 to 0.028,
    2002 to 0.016,
    2003 to 0.023,
    2004 to 0.027,
    2005 to 0.034,
    2006 to 0.032,
    2007 to 0.028,
    2008 to 0.038,
    2009 to -0.004,
    2010 to 0.016,
    2011 to 0.032,
    2012 to 0.021,
    2013 to 0.015,
    2014 to 0.016,
    2015 to 0.001,
    2016 to 0.013,
    2017 to 0.021,
    2018 to 0.024,
    2019 to 0.018,
    2020 to 0.012 
)

fun usInflation(year : Int) : Double {
    return usInflationMap[year]!!
}
private val USINFLATION by UnaryOp()

private class USInflationFunction : AlgebraicUnaryScalarFunction {
    override val meta = USINFLATION
    override fun doubleOp(value: Double) = usInflation(value.toInt())
    override fun floatOp(value: Float) = usInflation(value.toInt()).toFloat()

    override fun reduceArithmetic(value: ScalarExpr): ScalarExpr? {
        val constValue = value.tryFindConstant()
        return when {
            constValue != null -> constant(usInflation(constValue.toInt()))
            else -> null
        }
    }
    override fun differentiate(
        expr : ScalarExpr,
        exprd : ScalarExpr,
        variable : ScalarExpr
    ) = error("not differentiable")
}

val usInflation : AlgebraicUnaryScalarFunction = USInflationFunction()