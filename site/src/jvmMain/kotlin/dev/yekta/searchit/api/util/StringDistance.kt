package dev.yekta.searchit.api.util

import kotlin.math.min

fun String.distanceFrom(other: String) = distance(toCharArray(), other.toCharArray())
fun CharArray.distanceFrom(other: CharArray) = distance(this, other)

private fun distance(s1: CharArray, s2: CharArray): Int {
    if (s1.isEmpty()) return s2.size
    if (s2.isEmpty()) return s1.size

    var prev = IntArray(s2.size + 1) { it }

    for (i in 1..s1.size) {
        val curr = IntArray(s2.size + 1)
        curr[0] = i

        for (j in 1..s2.size) {
            val d1 = prev[j] + 1
            val d2 = curr[j - 1] + 1
            var d3 = prev[j - 1]
            if (s1[i - 1] != s2[j - 1]) d3++

            curr[j] = min(min(d1, d2), d3)
        }

        prev = curr
    }

    return prev[s2.size]
}
