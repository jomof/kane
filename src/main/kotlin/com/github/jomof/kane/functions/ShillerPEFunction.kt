package com.github.jomof.kane.functions

import com.github.jomof.kane.impl.*

// Source: https://www.multpl.com/shiller-pe/table/by-year
// Year is on January 1st of that year.
private val shillerPEMap = mapOf(
    1928 to 18.81,
    1929 to 27.08,
    1930 to 22.31,
    1931 to 16.71,
    1932 to 9.31,
    1933 to 8.73,
    1934 to 13.03,
    1935 to 11.5,
    1936 to 17.09,
    1937 to 21.62,
    1938 to 13.51,
    1939 to 15.6,
    1940 to 16.38,
    1941 to 13.9,
    1942 to 10.1,
    1943 to 10.15,
    1944 to 11.05,
    1945 to 11.96,
    1946 to 15.62,
    1947 to 11.47,
    1948 to 10.42,
    1949 to 10.25,
    1950 to 10.75,
    1951 to 11.9,
    1952 to 12.53,
    1953 to 13.01,
    1954 to 12.0,
    1955 to 15.99,
    1956 to 18.29,
    1957 to 16.72,
    1958 to 13.79,
    1959 to 17.98,
    1960 to 18.34,
    1961 to 18.47,
    1962 to 21.2,
    1963 to 19.26,
    1964 to 21.63,
    1965 to 23.27,
    1966 to 24.06,
    1967 to 20.43,
    1968 to 21.51,
    1969 to 21.19,
    1970 to 17.09,
    1971 to 16.46,
    1972 to 17.26,
    1973 to 18.71,
    1974 to 13.53,
    1975 to 8.92,
    1976 to 11.19,
    1977 to 11.44,
    1978 to 9.24,
    1979 to 9.26,
    1980 to 8.85,
    1981 to 9.26,
    1982 to 7.39,
    1983 to 8.76,
    1984 to 9.89,
    1985 to 10.0,
    1986 to 11.72,
    1987 to 14.92,
    1988 to 13.9,
    1989 to 15.09,
    1990 to 17.05,
    1991 to 15.61,
    1992 to 19.77,
    1993 to 20.32,
    1994 to 21.41,
    1995 to 20.22,
    1996 to 24.76,
    1997 to 28.33,
    1998 to 32.86,
    1999 to 40.57,
    2000 to 43.77,
    2001 to 36.98,
    2002 to 30.28,
    2003 to 22.9,
    2004 to 27.66,
    2005 to 26.59,
    2006 to 26.47,
    2007 to 27.21,
    2008 to 24.02,
    2009 to 15.17,
    2010 to 20.53,
    2011 to 22.98,
    2012 to 21.21,
    2013 to 21.9,
    2014 to 24.86,
    2015 to 26.49,
    2016 to 24.21,
    2017 to 28.06,
    2018 to 33.31,
    2019 to 28.38
)

private fun shillerPEImpl(year : Int) : Double {
    return shillerPEMap[year]!!
}
private val SHILLERPE by UnaryOp()

private class ShillerPEFunction : AlgebraicUnaryScalarFunction {
    override val meta = SHILLERPE
    override fun doubleOp(value: Double) = shillerPEImpl(value.toInt())

    override fun reduceArithmetic(value: ScalarExpr) : ScalarExpr? {
        if (!value.canGetConstant()) return null
        return constant(shillerPEImpl(value.getConstant().toInt()))
    }

    override fun differentiate(
        expr : ScalarExpr,
        exprd : ScalarExpr,
        variable : ScalarExpr
    ) = error("not differentiable")
}

val shillerPE : AlgebraicUnaryScalarFunction = ShillerPEFunction()