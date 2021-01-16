package com.github.jomof.kane.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.UnaryOp
import com.github.jomof.kane.constant
import com.github.jomof.kane.tryFindConstant

// %gain at end of year
private val baaCorporateBondMap = mapOf(
    1928 to 0.0322,
    1929 to 0.0302,
    1930 to 0.0054,
    1931 to -0.1568,
    1932 to 0.2359,
    1933 to 0.1297,
    1934 to 0.1882,
    1935 to 0.1331,
    1936 to 0.1138,
    1937 to -0.0442,
    1938 to 0.0924,
    1939 to 0.0798,
    1940 to 0.0865,
    1941 to 0.0501,
    1942 to 0.0518,
    1943 to 0.0804,
    1944 to 0.0657,
    1945 to 0.068,
    1946 to 0.0251,
    1947 to 0.0026,
    1948 to 0.0344,
    1949 to 0.0538,
    1950 to 0.0424,
    1951 to -0.0019,
    1952 to 0.0444,
    1953 to 0.0162,
    1954 to 0.0616,
    1955 to 0.0204,
    1956 to -0.0235,
    1957 to -0.0072,
    1958 to 0.0643,
    1959 to 0.0157,
    1960 to 0.0666,
    1961 to 0.051,
    1962 to 0.065,
    1963 to 0.0546,
    1964 to 0.0516,
    1965 to 0.0319,
    1966 to -0.0345,
    1967 to 0.009,
    1968 to 0.0485,
    1969 to -0.0203,
    1970 to 0.0565,
    1971 to 0.14,
    1972 to 0.1141,
    1973 to 0.0432,
    1974 to -0.0438,
    1975 to 0.1105,
    1976 to 0.1975,
    1977 to 0.0995,
    1978 to 0.0314,
    1979 to -0.0201,
    1980 to -0.0332,
    1981 to 0.0846,
    1982 to 0.2905,
    1983 to 0.1619,
    1984 to 0.1562,
    1985 to 0.2386,
    1986 to 0.2149,
    1987 to 0.0229,
    1988 to 0.1512,
    1989 to 0.1579,
    1990 to 0.0614,
    1991 to 0.1785,
    1992 to 0.1217,
    1993 to 0.1643,
    1994 to -0.0132,
    1995 to 0.2016,
    1996 to 0.0479,
    1997 to 0.1183,
    1998 to 0.0795,
    1999 to 0.0084,
    2000 to 0.0933,
    2001 to 0.0782,
    2002 to 0.1218,
    2003 to 0.1353,
    2004 to 0.0989,
    2005 to 0.0492,
    2006 to 0.0705,
    2007 to 0.0315,
    2008 to -0.0507,
    2009 to 0.2333,
    2010 to 0.0835,
    2011 to 0.1258,
    2012 to 0.1012,
    2013 to -0.0106,
    2014 to 0.1038,
    2015 to -0.007,
    2016 to 0.1037,
    2017 to 0.0972,
    2018 to -0.0276,
    2019 to 0.1533,
)

private fun baaCorporateBond(year : Int) : Double {
    return baaCorporateBondMap[year]!!
}
private val BAACORPORATEBOND by UnaryOp()

private class BAACorporateBondFunctionFunction : AlgebraicUnaryScalarFunction {
    override val meta = BAACORPORATEBOND
    override fun doubleOp(value: Double) = baaCorporateBond(value.toInt())
    override fun floatOp(value: Float) = baaCorporateBond(value.toInt()).toFloat()

    override fun reduceArithmetic(value: ScalarExpr): ScalarExpr? {
        val constValue = value.tryFindConstant()
        return when {
            constValue != null -> constant(baaCorporateBond(constValue.toInt()))
            else -> value
        }
    }
    override fun differentiate(
        expr : ScalarExpr,
        exprd : ScalarExpr,
        variable : ScalarExpr
    ) = error("not differentiable")
}

val baaCorporateBond : AlgebraicUnaryScalarFunction = BAACorporateBondFunctionFunction()