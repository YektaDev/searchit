package dev.yekta.searchit.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.api
import com.varabyte.kobweb.compose.css.StyleVariable
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.layout.breakpoint.displayIfAtLeast
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.base
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.style.toAttrs
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.ColorSchemes
import dev.yekta.searchit.HeadlineTextStyle
import dev.yekta.searchit.SubheadlineTextStyle
import dev.yekta.searchit.common.SearchResponse
import dev.yekta.searchit.components.layouts.PageLayout
import dev.yekta.searchit.toSitePalette
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLInputElement

// Container that has a tagline and grid on desktop, and just the tagline on mobile
val HeroContainerStyle by ComponentStyle {
    base { Modifier.fillMaxWidth().gap(2.cssRem) }
    Breakpoint.MD { Modifier.margin { top(20.vh) } }
}

// A demo grid that appears on the homepage because it looks good
val HomeGridStyle by ComponentStyle.base {
    Modifier
        .gap(0.5.cssRem)
        .width(70.cssRem)
        .height(18.cssRem)
}

private val GridCellColorVar by StyleVariable<Color>()
val HomeGridCellStyle by ComponentStyle.base {
    Modifier
        .backgroundColor(GridCellColorVar.value())
        .boxShadow(blurRadius = 0.6.cssRem, color = GridCellColorVar.value())
        .borderRadius(1.cssRem)
}

@Composable
private fun GridCell(color: Color, row: Int, column: Int, width: Int? = null, height: Int? = null) {
    Div(
        HomeGridCellStyle.toModifier()
            .setVariable(GridCellColorVar, color)
            .gridItem(row, column, width, height)
            .toAttrs()
    )
}

@Page
@Composable
fun HomePage() {
    val scope = rememberCoroutineScope()
    var apiResponseText by remember { mutableStateOf("") }

    PageLayout("Home") {
        Row(HeroContainerStyle.toModifier()) {
            Box {
                val sitePalette = ColorMode.current.toSitePalette()

                Column(Modifier.gap(2.cssRem)) {
                    Div(HeadlineTextStyle.toAttrs()) {
                        SpanText(
                            "Use this template as your starting point for ", Modifier.color(
                                when (ColorMode.current) {
                                    ColorMode.LIGHT -> Colors.Black
                                    ColorMode.DARK -> Colors.White
                                }
                            )
                        )
                        SpanText(
                            "Kobweb",
                            Modifier
                                .color(sitePalette.brand.accent)
                                // Use a shadow so this light-colored word is more visible in light mode
                                .textShadow(0.px, 0.px, blurRadius = 0.5.cssRem, color = Colors.Gray)
                        )
                    }

                    Div(SubheadlineTextStyle.toAttrs()) {
                        SpanText("You can read the ")
                        Link("/about", "About")
                        SpanText(" page for more information.")
                    }

                    val ctx = rememberPageContext()
                    Button(onClick = {
                        // Change this click handler with your call-to-action behavior
                        // here. Link to an order page? Open a calendar UI? Play a movie?
                        // Up to you!
                        ctx.router.tryRoutingTo("/about")
                    }, colorScheme = ColorSchemes.Blue) {
                        Text("This could be your CTA")
                    }
                }
            }

            Div(HomeGridStyle
                .toModifier()
                .displayIfAtLeast(Breakpoint.MD)
                .grid {
                    rows { repeat(3) { size(1.fr) } }
                    columns { repeat(5) {size(1.fr) } }
                }
                .toAttrs()
            ) {
                val sitePalette = ColorMode.current.toSitePalette()
                GridCell(sitePalette.brand.primary, 1, 1, 2, 2)
                GridCell(ColorSchemes.Monochrome._600, 1, 3)
                GridCell(ColorSchemes.Monochrome._100, 1, 4, width = 2)
                GridCell(sitePalette.brand.accent, 2, 3, width = 2)
                GridCell(ColorSchemes.Monochrome._300, 2, 5)
                GridCell(ColorSchemes.Monochrome._800, 3, 1, width = 5)
            }
        }
        Button(
            modifier = Modifier.margin(bottom = 1.em).fontFamily("Roboto").fontSize(1.em),
            onClick = {
                scope.launch {
                    val apiResponse = search()
                    apiResponseText = apiResponse.toString()
                }
            },
        ) { SpanText(text = "Fetch") }
    }
}

private suspend fun search(): SearchResponse {
    return try {
        val inputText = (document.getElementById("countInput") as HTMLInputElement).value
        val number = if (inputText.isEmpty()) 0 else inputText.toInt()
        val result = window.api.tryGet(apiPath = "getpeople?count=$number")?.decodeToString()
        Json.decodeFromString(result.toString())
    } catch (e: Exception) {
        return SearchResponse.Error(errorMessage = e.message.toString())
    }
}
