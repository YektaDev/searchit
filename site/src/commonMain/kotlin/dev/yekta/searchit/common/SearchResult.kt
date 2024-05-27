package dev.yekta.searchit.common

import kotlinx.serialization.Serializable

@Serializable
data class SearchResult(
    val items: List<Item>,
    val stats: ResultStats,
)
