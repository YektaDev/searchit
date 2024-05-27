package dev.yekta.searchit.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.api
import com.varabyte.kobweb.compose.css.AnimationIterationCount
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment.CenterHorizontally
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.navigation.OpenLinkStrategy.IN_NEW_TAB
import com.varabyte.kobweb.navigation.open
import com.varabyte.kobweb.silk.components.animation.Keyframes
import com.varabyte.kobweb.silk.components.animation.toAnimation
import com.varabyte.kobweb.silk.components.forms.TextInput
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import dev.yekta.searchit.common.Item
import dev.yekta.searchit.common.ResultStats
import dev.yekta.searchit.common.SearchResponse
import dev.yekta.searchit.common.SearchResult
import dev.yekta.searchit.components.layouts.SmallHeaderLayout
import kotlinx.browser.window
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.css.*

@Page
@Composable
fun Index() {
    var result by remember { mutableStateOf<SearchResult?>(null) }
    var query by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isSearching by remember { mutableStateOf(false) }

    LaunchedEffect(query) {
        if (result == null && error == null && query.isBlank()) return@LaunchedEffect
        error = null
        result = null
        isSearching = true
        when (val searchResult = search(query)) {
            is SearchResponse.Success -> result = searchResult.data
            is SearchResponse.Error -> error = searchResult.errorMessage
        }
        isSearching = false
    }

    SmallHeaderLayout(
        header = {
            TextInput(
                text = query,
                modifier = Modifier.weight(1f),
                onTextChanged = { query = it },
            )
        }
    ) {
        val itemModifier = Modifier
            .align(CenterHorizontally)
            .margin(leftRight = 1f.em, topBottom = .25f.em)
            .fadeInFromLeft(.25f)
        val errorModifier = Modifier.padding(leftRight = 1f.em, topBottom = .5f.em).fadeInFromLeft()
        if (isSearching) {
            SpanText("Searching...", itemModifier.padding(leftRight = 1f.em, topBottom = .5f.em))
        }
        result?.let { searchResult ->
            error?.let { Error(it, errorModifier) }
            Column(horizontalAlignment = CenterHorizontally) {
                searchResult.stats.takeIf { it.results > 0 }?.let { StatsReport(it, itemModifier) }

                for ((i, item) in searchResult.items.withIndex()) {
                    showAfter((i * 150).toLong()) {
                        Item(item, itemModifier)
                    }
                }
            }
        }
    }
}


val itemBackground
    @Composable get() = if (ColorMode.current.isDark) rgba(248, 255, 210, .15f) else rgba(255, 225, 123, .15f)
val itemBorder
    @Composable get() = if (ColorMode.current.isDark) rgba(248, 255, 210, .5f) else rgba(255, 225, 123, .5f)
val itemForeground
    @Composable get() = if (ColorMode.current.isDark) rgb(248, 255, 210) else rgb(60, 30, 10)
val itemForegroundWeak
    @Composable get() = if (ColorMode.current.isDark) rgba(248, 255, 210, .85f) else rgba(60, 30, 10, .85f)

@Composable
private fun Item(item: Item, modifier: Modifier = Modifier) = Column(
    modifier = modifier
        .onClick { window.open(item.url, IN_NEW_TAB) }
        .cursor(Cursor.Pointer)
        .padding(1f.em)
        .borderRadius(.5f.em)
        .border(width = .05f.cssRem, color = itemBorder, style = LineStyle.Solid)
        .backgroundColor(itemBackground)
) {
    SpanText(item.title, modifier = Modifier.fillMaxWidth().color(itemForeground).fontWeight(FontWeight.Bold))
    SpanText(item.url, modifier = Modifier.color(rgb(223, 130, 108)).margin(bottom = .5f.em))
    SpanText(item.description, modifier = Modifier.color(itemForegroundWeak))
}

@Composable
private fun StatsReport(stats: ResultStats, modifier: Modifier = Modifier.color(Color.gray)) = with(stats) {
    val duration = remember(stats.durationMs) { if (durationMs < 1000) "$durationMs ms" else "${durationMs / 1000} s" }
    SpanText("$results results ($duration)", modifier)
}

private suspend fun search(query: String): SearchResponse {
    return try {
        val byteResult = window.api.tryPost(apiPath = "search", body = query.encodeToByteArray())
        val strResult = byteResult?.decodeToString() ?: return SearchResponse.Error("No Response from Server")
        Json.decodeFromString(SearchResponse.serializer(), strResult)
    } catch (e: Exception) {
        SearchResponse.Error(e.message.toString())
    }
}

val FadeInFromLeft by Keyframes {
    from { Modifier.opacity(.0f).margin(left = (-6).em) }
    to { Modifier.opacity(1f).margin(left = 0.em) }
}

@Composable
fun Error(message: String, modifier: Modifier = Modifier) =
    SpanText(message, modifier.color(Color.darkred))

@Composable
private fun Modifier.fadeInFromLeft(durationSeconds: Float = 1f, delaySeconds: Float = 0f) = animation(
    FadeInFromLeft.toAnimation(
        delay = delaySeconds.s,
        duration = durationSeconds.s,
        iterationCount = AnimationIterationCount.of(1),
    )
)

@Composable
private inline fun showAfter(delayMs: Long, content: @Composable () -> Unit) {
    var isShown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(delayMs)
        isShown = true
    }
    if (isShown) content()
}
