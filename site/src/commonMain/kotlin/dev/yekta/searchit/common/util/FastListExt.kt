@file:Suppress("unused")

package dev.yekta.searchit.common.util

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Utility to help perform slightly faster operations on lists that support random access
 * (particularly ArrayLists).
 * ---
 * @author Ali Khaleqi Yekta [Me@Yekta.Dev]
 * Inspired by: https://discuss.kotlinlang.org/t/for-statement-performance-improvement-kotlin-jvm/22805/18
 */

/**
 * A variation of [forEach] which performs slightly better for lists that have random access. This
 * function **does not assert concurrent modifications**.
 * ---
 * ### CAUTION: Only use if you are sure the list supports random access!
 */
@OptIn(ExperimentalContracts::class)
inline fun <T> List<T>.fastForEach(action: (T) -> Unit) {
  contract { callsInPlace(action) }
  for (index in indices) action(get(index))
}

/**
 * A variation of [forEachIndexed] which performs slightly better for lists that have random access.
 * This function **does not assert concurrent modifications**.
 * ---
 * ### CAUTION: Only use if you are sure the list supports random access!
 */
@OptIn(ExperimentalContracts::class)
inline fun <T> List<T>.fastForEachIndexed(action: (Int, T) -> Unit) {
  contract { callsInPlace(action) }
  for (index in indices) action(index, get(index))
}

/**
 * A variation of reversed [forEach] which performs slightly better for lists that have random
 * access. This function **does not assert concurrent modifications**.
 * ---
 * ### CAUTION: Only use if you are sure the list supports random access!
 */
@OptIn(ExperimentalContracts::class)
inline fun <T> List<T>.fastForEachReversed(action: (T) -> Unit) {
  contract { callsInPlace(action) }
  for (index in indices.reversed()) action(get(index))
}

/**
 * A variation of [any] which performs slightly better for lists that have random access. This
 * function **does not assert concurrent modifications**.
 * ---
 * ### CAUTION: Only use if you are sure the list supports random access!
 */
@OptIn(ExperimentalContracts::class)
inline fun <T> List<T>.fastAny(predicate: (T) -> Boolean): Boolean {
  contract { callsInPlace(predicate) }
  fastForEach { if (predicate(it)) return true }
  return false
}

/**
 * A variation of [all] which performs slightly better for lists that have random access. This
 * function **does not assert concurrent modifications**.
 * ---
 * ### CAUTION: Only use if you are sure the list supports random access!
 */
@OptIn(ExperimentalContracts::class)
inline fun <T> List<T>.fastAll(predicate: (T) -> Boolean): Boolean {
  contract { callsInPlace(predicate) }
  fastForEach { if (!predicate(it)) return false }
  return true
}

/**
 * A variation of [none] which performs slightly better for lists that have random access. This
 * function **does not assert concurrent modifications**.
 * ---
 * ### CAUTION: Only use if you are sure the list supports random access!
 */
@OptIn(ExperimentalContracts::class)
inline fun <T> List<T>.fastNone(predicate: (T) -> Boolean): Boolean {
  contract { callsInPlace(predicate) }
  fastForEach { if (predicate(it)) return false }
  return true
}

/**
 * A variation of [firstOrNull] which performs slightly better for lists that have random access.
 * This function **does not assert concurrent modifications**.
 * ---
 * ### CAUTION: Only use if you are sure the list supports random access!
 */
@OptIn(ExperimentalContracts::class)
inline fun <T> List<T>.fastFirstOrNull(predicate: (T) -> Boolean): T? {
  contract { callsInPlace(predicate) }
  fastForEach { if (predicate(it)) return it }
  return null
}

/**
 * A variation of [lastOrNull] which performs slightly better for lists that have random access.
 * This function **does not assert concurrent modifications**.
 * ---
 * ### CAUTION: Only use if you are sure the list supports random access!
 */
@OptIn(ExperimentalContracts::class)
inline fun <T> List<T>.fastLastOrNull(predicate: (T) -> Boolean): T? {
  contract { callsInPlace(predicate) }
  fastForEachReversed { if (predicate(it)) return it }
  return null
}

/**
 * A variation of [map] which performs slightly better for lists that have random access. This
 * function **does not assert concurrent modifications**.
 * ---
 * ### CAUTION: Only use if you are sure the list supports random access!
 */
@OptIn(ExperimentalContracts::class)
inline fun <T, R> List<T>.fastMap(transform: (T) -> R): List<R> {
  contract { callsInPlace(transform) }
  val target = ArrayList<R>(size)
  fastForEach { target += transform(it) }
  return target
}

/**
 * A variation of [mapTo] which performs slightly better for lists that have random access. This
 * function **does not assert concurrent modifications**.
 * ---
 * ### CAUTION: Only use if you are sure the list supports random access!
 */
@OptIn(ExperimentalContracts::class)
inline fun <T, R, C : MutableCollection<in R>> List<T>.fastMapTo(destination: C, transform: (T) -> R): C {
  contract { callsInPlace(transform) }
  fastForEach { destination.add(transform(it)) }
  return destination
}

/**
 * A variation of [maxOf] which performs slightly better for lists that have random access. This
 * function **does not assert concurrent modifications**.
 * ---
 * ### CAUTION: Only use if you are sure the list supports random access!
 */
@OptIn(ExperimentalContracts::class)
inline fun <T, R : Comparable<R>> List<T>.fastMaxOfOrNull(selector: (T) -> R): T? {
  contract { callsInPlace(selector) }
  if (isEmpty()) return null
  var maxElement = get(0)
  var maxMappedValue = selector(maxElement)
  for (i in 1..lastIndex) {
    val element = get(i)
    val mappedValue = selector(element)
    if (mappedValue <= maxMappedValue) continue
    maxElement = element
    maxMappedValue = mappedValue
  }
  return maxElement
}

/**
 * A variation of [sumOf] which performs slightly better for lists that have random access. This
 * function **does not assert concurrent modifications**.
 * ---
 * ### CAUTION: Only use if you are sure the list supports random access!
 */
@OptIn(ExperimentalContracts::class)
inline fun <T> List<T>.fastSumOf(selector: (T) -> Int): Int {
  contract { callsInPlace(selector) }
  var sum = 0
  fastForEach { sum += selector(it) }
  return sum
}
