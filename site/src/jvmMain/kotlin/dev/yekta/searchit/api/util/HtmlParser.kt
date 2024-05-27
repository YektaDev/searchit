package dev.yekta.searchit.api.util

import com.varabyte.kobweb.api.log.Logger
import dev.yekta.searchit.api.model.ParsedHtml
import org.jsoup.Jsoup

internal class HtmlParser(private val logger: Logger) {
    private fun Result<String>.getOrFallback() = getOrElse { e ->
        logger.error("[Jsoup Error]: ${e.stackTraceToString()}")
        ParsedHtml(title = "", body = "")
    }


    fun parse(html: String): ParsedHtml? = try {
        val doc = Jsoup.parse(html)
        ParsedHtml(
            title = doc.title().trim(),
            body = doc.body().text().trim(),
        )
    } catch (e: Throwable) {
        logger.warn("[Jsoup Error]:\n${e.stackTraceToString()}\n[HTML]:\n$html")
        null
    }
}