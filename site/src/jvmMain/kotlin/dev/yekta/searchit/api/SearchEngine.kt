package dev.yekta.searchit.api

import com.moriatsushi.cacheable.Cacheable
import com.varabyte.kobweb.api.log.Logger
import dev.yekta.searchit.api.model.Page
import dev.yekta.searchit.api.repo.DBManager
import dev.yekta.searchit.api.repo.StringNormalizer
import dev.yekta.searchit.api.repo.WebpageIndexes
import dev.yekta.searchit.api.repo.Webpages
import dev.yekta.searchit.api.util.HtmlParser
import dev.yekta.searchit.common.Item
import dev.yekta.searchit.common.ResultStats
import dev.yekta.searchit.common.SearchResult
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.selectAll
import kotlin.math.min
import kotlin.system.measureTimeMillis

class SearchEngine(private val logger: Logger) {
    private val db = DBManager(logger)
    private val parser = HtmlParser(logger)
    private val pages = runBlocking {
        logger.info("Reading Page Data...")
        db.transaction {
            Webpages.selectAll().mapNotNull {
                val parsedHtml = parser.parse(it[Webpages.html]) ?: return@mapNotNull null
                Page(url = it[Webpages.url], parsedHtml = parsedHtml)
            }
        }
    }

    private val pageItems = runBlocking {
        logger.info("Extracting Items...")
        pages.map {
            Item(
                url = it.url,
                title = it.title,
                description = it.body.substring(0, min(500, it.body.length)),
            )
        }
    }

    private val indexes = runBlocking {
        logger.info("Building Indexes...")
        WebpageIndexes(pages)
    }

    private val finder = Finder(indexes, logger)

    private inline fun Map<String, ArrayList<Int>>.getUniqueMatchingPageIndexes(
        crossinline uniquePredicate: (String) -> Boolean
    ) = this
        .asSequence()
        .filter { (token: String, _) -> uniquePredicate(token) }
        .flatMap { (_, pageIndexes: ArrayList<Int>) -> pageIndexes }
        .groupBy { pageIndex -> pageIndex }

    suspend fun search(query: String): SearchResult = coroutineScope {
        var result: List<Item>
        val duration = measureTimeMillis {
            result = when (query.length) {
                in 4..100 -> performCachableSearch(query)
                else -> performNonCachableSearch(query)
            }
        }

        SearchResult(
            items = result,
            stats = ResultStats(
                results = result.size.toLong(),
                durationMs = duration,
            ),
        )
    }

    @Cacheable(maxCount = 100_000, lock = true)
    private fun performCachableSearch(query: String): List<Item> = searchForQuery(query)
    private fun performNonCachableSearch(query: String): List<Item> = searchForQuery(query)
    private fun searchForQuery(query: String): List<Item> {
        val result = mutableListOf<Item>()
        // Normalizers must be the same for both query and indexes, this can be encapsulated.
        val tokens = StringNormalizer.normalizeAndTokenize(query)

        // Defaults to AND search
        val foundPages = finder.havingAllTokens(tokens, indexes.titleInvertedIndex, indexes.bodyInvertedIndex)
        foundPages.forEach { result.add(pageItems[it]) }

        return result
//            val titleMatches = indexes.titleInvertedIndex
//                .getUniqueMatchingPageIndexes { token -> tokens.contains(token) }
//                .map { (token, pageIndex) -> token to pageIndex.size }
//                .sortedByDescending { it.second }
//                .map { it.first }
//                .toList()
//                .forEach { result.add(0, pageItems[it]) }
//
//            val bodyMatches = indexes.bodyInvertedIndex
//                .getUniqueMatchingPageIndexes { token -> tokens.contains(token) }
//                .filter { (token: String, _) -> tokens.contains(token) }
//                .flatMap { (_, pageIndexes: ArrayList<Int>) -> pageIndexes }
//
//            (titleMatches + bodyMatches)
//                .groupBy { pageIndex -> pageIndex }
//                .map { (token, pageIndex) -> token to pageIndex.size }
//                .sortedByDescending { it.second }
//                .map { it.first }
//                .toList()
//                .forEach { result.add(0, pageItems[it]) }
//

//            for ((index, page) in pages.withIndex()) {
//                when {
//                    page.title.contains(query, ignoreCase = true) -> result.add(0, pageItems[index])
//                    page.body.contains(query, ignoreCase = true) -> result.add(pageItems[index])
//                }
//            }
    }
}
