package dev.yekta.searchit.api.repo

import kotlin.math.max

private typealias ContentTokens = Pair<Int, List<String>>

class InvertedIndexGenerator(private val tokenizer: (String) -> List<String>) {
    private fun estimateTokenCount(tokensSize: Int) =
        max(1024, if (tokensSize >= 10_000) tokensSize * 2 else tokensSize * 4)

    fun generate(contents: List<String>): Map<String, ArrayList<Int>> {
        val contentTokens: List<ContentTokens> = buildList {
            contents.forEachIndexed { index, content ->
                val tokens = tokenizer(content)
                val contentTokens = ContentTokens(index, tokens)
                add(contentTokens)
            }
        }

        return buildMap(estimateTokenCount(contentTokens.size)) {
            contentTokens.forEach { (contentIndex, tokens) ->
                for (token in tokens) {
                    val contentListOfToken = getOrPut(token, ::arrayListOf)
                    contentListOfToken.add(contentIndex)
                }
            }

            forEach { (_, contentIndexes) -> contentIndexes.sort() }
        }
    }
}
