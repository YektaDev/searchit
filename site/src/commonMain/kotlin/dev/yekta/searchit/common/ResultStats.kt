package dev.yekta.searchit.common

import kotlinx.serialization.Serializable

@Serializable
data class ResultStats(
    val durationMs: Long,
    val results: Long,
)
