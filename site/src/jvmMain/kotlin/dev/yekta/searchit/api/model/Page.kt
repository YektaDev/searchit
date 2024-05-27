package dev.yekta.searchit.api.model

data class Page(
    val url: String,
    val title: String,
    val body: String,
) {
    constructor(url: String, parsedHtml: ParsedHtml) : this(
        url = url,
        title = parsedHtml.title,
        body = parsedHtml.body
    )
}
