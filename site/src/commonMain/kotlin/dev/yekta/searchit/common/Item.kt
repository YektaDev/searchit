package dev.yekta.searchit.common

import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val url: String,
    val title: String,
    val description: String,
)
