@file:Suppress("unused")

package dev.yekta.searchit.api

import com.moriatsushi.cacheable.Cacheable
import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.http.setBodyText
import com.varabyte.kobweb.api.init.InitApi
import com.varabyte.kobweb.api.init.InitApiContext
import dev.yekta.searchit.common.Item
import dev.yekta.searchit.common.ResultStats
import dev.yekta.searchit.common.SearchResponse
import dev.yekta.searchit.common.SearchResponse.Success
import dev.yekta.searchit.common.SearchResult
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private lateinit var engine: SearchEngine

@InitApi
fun initEngine(ctx: InitApiContext) {
    engine = SearchEngine(ctx.logger)
    ctx.logger.info("READY!")
}

@Api
suspend fun search(context: ApiContext) = try {
    val query = context.req.body?.decodeToString()
    context.logger.info("Searching for: $query")

    when {
        query.isNullOrBlank() -> respond(context, ok(emptyList(), ResultStats(0, 0)))
        else -> respond(context, Success(engine.search(query)))
    }
} catch (e: Exception) {
    respond(context, e(e.message.toString()))
}

private fun respond(context: ApiContext, response: SearchResponse) = context.res.setBodyText(
    Json.encodeToString<SearchResponse>(response)
        .also {
            when (response) {
                is SearchResponse.Error -> context.logger.error(it)
                is Success -> context.logger.info("Success(size: ${response.data.items.size})")
            }
        }
)

private fun e(errorMessage: String) = SearchResponse.Error(errorMessage)
private fun ok(items: List<Item>, stats: ResultStats) = Success(SearchResult(items, stats))
