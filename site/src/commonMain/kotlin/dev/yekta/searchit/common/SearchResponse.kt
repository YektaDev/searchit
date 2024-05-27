package dev.yekta.searchit.common


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class SearchResponse {
    @Serializable
    @SerialName("success")
    data class Success(val data: SearchResult) : SearchResponse()

    @Serializable
    @SerialName("error")
    data class Error(val errorMessage: String) : SearchResponse()
}
