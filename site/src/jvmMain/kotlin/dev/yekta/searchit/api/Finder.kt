package dev.yekta.searchit.api

import com.varabyte.kobweb.api.log.Logger
import dev.yekta.searchit.api.repo.WebpageIndexes
import dev.yekta.searchit.api.util.distanceFrom

typealias PageIndex = Int
typealias SortedInvertedIndex = Map<String, List<PageIndex>>

class Finder(private val indexes: WebpageIndexes, private val logger: Logger) {
    fun havingAllTokens(
        queryTokens: List<String>,
        titleInvertedIndex: SortedInvertedIndex,
        bodyInvertedIndex: SortedInvertedIndex,
    ): List<PageIndex> {
        val titleMatches = havingAllTokens(queryTokens, titleInvertedIndex)
        val bodyMatches = havingAllTokens(queryTokens, bodyInvertedIndex)
        return titleMatches + (bodyMatches - titleMatches.toSet())
    }

//     Can be used with more than 2 inverted indexes as well: (BUGGY)
//    private fun havingAllTokens(
//        queryTokens: List<String>,
//        invertedIndexesFromHighestPriority: List<SortedInvertedIndex>,
//    ): List<PageIndex> {
//        val indexMatchLists = invertedIndexesFromHighestPriority.fastMap { havingAllTokens(queryTokens, it) }
//        return intersection(indexMatchLists).toList()
//    }

    private fun havingAllTokens(
        queryTokens: List<String>,
        invertedIndex: SortedInvertedIndex,
    ): List<PageIndex> {
        val tokensPageIndexes = resolveQueryTokensPageIndexesWithErrorCorrection(queryTokens, invertedIndex)

        // Error Correction Only:
//        val indexOfTokenWithLeastMatches = tokensPageIndexes
//            .mapIndexed { index, x -> x to index }
//            .minByOrNull { (tokenPageIndexes, _) -> tokenPageIndexes.size }
//            ?.second
//        indexOfTokenWithLeastMatches?.let {
//            val newQueryTokens = ArrayList<String>(queryTokens.size).apply { addAll(queryTokens) }
//            val tokenWithLeastMatches = queryTokens[it]
//            val (nearestTokenByOneDiff, _) = indexes.allTokensSortedByTypeThenSize
//                .firstOrNull { (token, _) -> token.distanceFrom(tokenWithLeastMatches) == 1 } ?: return@let
//            newQueryTokens[it] = nearestTokenByOneDiff
//
//            logger.info("Corrected $tokenWithLeastMatches to $nearestTokenByOneDiff")
//
//            val alteredTokensPageIndexes = resolveQueryTokensPageIndexes(newQueryTokens, invertedIndex)
//            return intersection(tokensPageIndexes).toList() + intersection(alteredTokensPageIndexes)
//        }

        val originalResult = intersection(tokensPageIndexes.originalTokensPageIndexes).toList()
        if (tokensPageIndexes.errorCorrectedTokensPageIndexes == null) {
            logger.debug("Resulting ${originalResult.size} query results")
            return originalResult
        }

        val correctedResult =
            intersection(tokensPageIndexes.errorCorrectedTokensPageIndexes).toList() - originalResult.toSet()
        logger.debug("Resulting ${originalResult.size} query results & ${correctedResult.size} fixed query responses")

        return originalResult + correctedResult
    }

    private data class ExpandedPageIndexes(
        val originalTokensPageIndexes: List<List<PageIndex>>,
        val errorCorrectedTokensPageIndexes: List<List<PageIndex>>? = null,
    )


    private fun firstProbablyWrongToken(
        queryTokens: List<String>,
        queryMatches: List<Pair<String, List<PageIndex>>>,
    ): String? {
        val (leastResultsToken, _) = queryMatches
            .filter { it.first.length > 1 }
            .minByOrNull { it.second.size } ?: return queryTokens.firstOrNull { it.length > 1 }
        return leastResultsToken
    }

    // Dirty! Clean when it's important!
    private fun resolveQueryTokensPageIndexesWithErrorCorrection(
        queryTokens: List<String>,
        invertedIndex: SortedInvertedIndex,
    ): ExpandedPageIndexes {
        val queryTokensSorted = queryTokens.sorted()

        // Walking through a map!
        val matches = invertedIndex.filter { (token, _) -> queryTokensSorted.binarySearch(token) >= 0 }.toList()
        val noErrAnswer = matches.map { (_, pageIndexes) -> pageIndexes }
        val leastResultsToken = firstProbablyWrongToken(queryTokens, matches) ?: return ExpandedPageIndexes(noErrAnswer)

        // Error Correction Only:
        val indexOfTokenWithLeastMatches = queryTokens.indexOf(leastResultsToken)
        if (indexOfTokenWithLeastMatches >= 0 && queryTokens.isNotEmpty()) {
            val newQueryTokens = ArrayList<String>(queryTokens.size).apply { addAll(queryTokens) }
            val tokenWithLeastMatches = queryTokens[indexOfTokenWithLeastMatches]
            val (nearestTokenByOneDiff, _) = indexes.allTokensSortedByTypeThenSize
                .firstOrNull { (token, _) ->
                    token.distanceFrom(tokenWithLeastMatches) == 1
                }
                ?: return ExpandedPageIndexes(noErrAnswer)
            newQueryTokens[indexOfTokenWithLeastMatches] = nearestTokenByOneDiff

            logger.info("Corrected $tokenWithLeastMatches to $nearestTokenByOneDiff")

            val alteredTokensPageIndexes = resolveQueryTokensPageIndexes(newQueryTokens, invertedIndex)
//            return noErrAnswer + (alteredTokensPageIndexes - noErrAnswer.toSet())
            return ExpandedPageIndexes(noErrAnswer, alteredTokensPageIndexes)
        }
        return ExpandedPageIndexes(noErrAnswer)
    }

    // Simplified version of above function (no err correction)
    private fun resolveQueryTokensPageIndexes(
        queryTokens: List<String>,
        invertedIndex: SortedInvertedIndex,
    ): List<List<PageIndex>> {
        val queryTokensSorted = queryTokens.sorted()

        // Walking through a map!
        return invertedIndex
            .filter { (token, _) -> queryTokensSorted.binarySearch(token) >= 0 }
            .map { (_, pageIndexes) -> pageIndexes }
    }


    private fun <T : Comparable<T>> intersection(inputs: List<List<T>>): Sequence<T> = sequence {
        try {
            val n = inputs.size
            val iters = inputs.map { it.iterator() }
            var it = iters.first()
            var currIter = 1 % n
            var candidate = it.next()
            while (true) {
                for (i in 0 until n - 1) {
                    it = iters[currIter]
                    currIter = (currIter + 1) % n
                    var value = it.next()
                    while (value < candidate) {
                        value = it.next()
                    }
                    if (value != candidate) {
                        candidate = value
                        break
                    }
                }
                yield(candidate)
                it = iters[currIter]
                currIter = (currIter + 1) % n
                candidate = it.next()
            }
        } catch (e: NoSuchElementException) {
            return@sequence
        }
    }
}