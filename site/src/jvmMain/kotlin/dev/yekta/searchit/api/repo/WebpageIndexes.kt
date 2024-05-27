package dev.yekta.searchit.api.repo

import dev.yekta.searchit.api.model.Page

class WebpageIndexes(pages: List<Page>) {
    private val indexer = InvertedIndexGenerator(StringNormalizer::normalizeAndTokenize)

    val titleInvertedIndex: Map<String, ArrayList<Int>>
    val bodyInvertedIndex: Map<String, ArrayList<Int>>

    init {
        val titles = mutableListOf<String>()
        val bodies = mutableListOf<String>()

        for (page in pages) {
            titles.add(page.title)
            bodies.add(page.body)
        }

        titleInvertedIndex = indexer.generate(titles)
        bodyInvertedIndex = indexer.generate(bodies)
    }

    val allTokensSortedByTypeThenSize =
        buildList {
            try {
                val titleMatches = titleInvertedIndex.toList()
                val bodyMatches =
                    bodyInvertedIndex.toList().filterNot { (token, _) -> titleInvertedIndex.containsKey(token) }

                val sortedTitleMatches = titleMatches.sortedByDescending { it.second.size }
                addAll(sortedTitleMatches)

                val sortedBodyMatches = bodyMatches.sortedByDescending { it.second.size }
                addAll(sortedBodyMatches)

            } catch (e: Throwable) {
                throw IllegalStateException("WebpageIndexes: ${e.stackTraceToString()}")
            }
        }
}