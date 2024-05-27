package dev.yekta.searchit.api.repo

import dev.yekta.searchit.api.repo.LossySpaceNormalizer.normalizeSpacesAndPunctuation
import java.util.Locale.ENGLISH

object StringNormalizer {
    fun normalizeAndTokenize(text: String): List<String> = normalize(text).split(' ')

    fun normalize(text: String): String = normalizeSpacesAndPunctuation(text) { char: Char ->
        char
            .let(DigitNormalizer::normalize)
            .normalizeCommonArabicLetterToPersian()
    }.lowercase(ENGLISH)

    private fun Char.normalizeCommonArabicLetterToPersian(): Char {
        val arabicPersianSimilarities = arrayOf(
            'آ' to 'ا',
            'ة' to 'ه',
            'ي' to 'ی',
            'ك' to 'ک',
            'ئ' to 'ی',
            'أ' to 'ا',
            'إ' to 'ا',
            'ؤ' to 'و',
            'ء' to 'ا',
            'ۀ' to 'ه',
            'ۂ' to 'ه',
            'ە' to 'ه',
            'ہ' to 'ه',
            'ۃ' to 'ه',
            'ۓ' to 'ی',
            'ے' to 'ی',
            'ې' to 'ی',
            'ۍ' to 'ی',
            'ێ' to 'ی',
        )
        val persianMatchOrNull = arabicPersianSimilarities.find { (arabic, _) -> this == arabic }?.second
        return persianMatchOrNull ?: this
    }
}

private object LossySpaceNormalizer {
    /**
     * A sorted array of known invisible characters that have no width when being represented by usual fonts. It's
     * binary-searchable and might not contain ALL possibly/definitely characters that have no width.
     */
    private val knownInvisibleCharsWithoutWidthSorted = charArrayOf(
        '؜',
        '​',
        '‌',
        '‍',
        '‎',
        '‏',
        '‪',
        '‫',
        '‬',
        '‭',
        '‮',
        '⁠',
        '⁡',
        '⁢',
        '⁣',
        '⁤',
        '⁥',
        '⁦',
        '⁧',
        '⁨',
        '⁩',
        '⁪',
        '⁫',
        '⁬',
        '⁭',
        '⁮',
        '⁯',
        '﻿',
    )

    /**
     * Returns `true` if this character is a known invisible character that has no width.
     * @see knownInvisibleCharsWithoutWidthSorted
     */
    private fun Char.hasNoWidthWhenShown() = knownInvisibleCharsWithoutWidthSorted.binarySearch(this) >= 0

    inline fun normalizeSpacesAndPunctuation(string: String, mapClean: (Char) -> Char = { it }): String {
        val trimmed = string.trim()
        return buildString(trimmed.length) {
            var lastCharWasSpace = false
            for (char in trimmed) {
                lastCharWasSpace = when {
                    char.isWhitespace() || (!char.isLetter() && !char.isDigit()) -> {
                        if (!lastCharWasSpace) append(' ')
                        true
                    }

                    else -> {
                        append(mapClean(char))
                        false
                    }
                }
            }
        }
    }
}

private object DigitNormalizer {
    private val enNumRange = 48..57
    private val arNumRange = 1632..1641
    private val faNumRange = 1776..1785

    private val enArDifference = enNumRange.first - arNumRange.first
    private val enFaDifference = enNumRange.first - faNumRange.first

    /** Converts Arabic and Persian digits to English digits (if any), in a _performant_ way. */
    fun normalize(char: Char): Char = when (char.code) {
        in arNumRange -> (char.code + enArDifference).toChar()
        in faNumRange -> (char.code + enFaDifference).toChar()
        else -> char
    }
}
