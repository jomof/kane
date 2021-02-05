package com.github.jomof.kane.impl


private fun cartesianOfIndices(
    counts : List<Int>,
    index : Int,
    elements : Array<Int>,
    action: (Array<Int>) -> Unit) {
    if (index == counts.size) action(elements)
    else {
        for(element in 0 until counts[index]) {
            elements[index] = element
            cartesianOfIndices(counts, index + 1, elements, action)
        }
    }
}

fun cartesianOfIndices(counts : List<Int>, action: (Array<Int>) -> Unit) {
    val elements = Array(counts.size) { 0 }
    return cartesianOfIndices(counts, 0, elements, action)
}

fun cartesianOf(elements : List<List<Any>>, action: (List<Any>) -> Unit) {
    val counts = elements.map { it.size }
    cartesianOfIndices(counts) { indices ->
        action((elements zip indices).map { (list, index) -> list[index] })
    }
}