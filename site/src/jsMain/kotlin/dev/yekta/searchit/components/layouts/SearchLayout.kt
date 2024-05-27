package dev.yekta.searchit.components.layouts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.css.AlignContent.Companion.Center
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.functions.blur
import com.varabyte.kobweb.compose.css.functions.saturate
import com.varabyte.kobweb.compose.dom.ElementTarget
import com.varabyte.kobweb.compose.foundation.layout.*
import com.varabyte.kobweb.compose.ui.Alignment.CenterHorizontally
import com.varabyte.kobweb.compose.ui.Alignment.CenterVertically
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.icons.MoonIcon
import com.varabyte.kobweb.silk.components.icons.SunIcon
import com.varabyte.kobweb.silk.components.overlay.PopupPlacement.BottomRight
import com.varabyte.kobweb.silk.components.overlay.Tooltip
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.base
import com.varabyte.kobweb.silk.components.style.common.SmoothColorStyle
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import dev.yekta.searchit.components.widgets.IconButton
import dev.yekta.searchit.toSitePalette
import org.jetbrains.compose.web.attributes.DirType
import org.jetbrains.compose.web.css.*

private val headerHeight = 3.5f.em

val NavHeaderStyle by ComponentStyle.base(extraModifiers = { SmoothColorStyle.toModifier() }) {
    Modifier
        .fillMaxWidth()
        .backgroundColor(getNavBackgroundColor(colorMode))
        .position(Position.Sticky)
        .top(0.percent)
        .backdropFilter(saturate(180.percent), blur(5.px))
        .boxShadow(colorMode)
        .padding(leftRight = 1f.em)
        .height(headerHeight)
}

private fun getNavBackgroundColor(colorMode: ColorMode): CSSColorValue = when (colorMode) {
    ColorMode.DARK -> rgba(0.0, 0.0, 0.0, 0.65)
    ColorMode.LIGHT -> rgba(255, 255, 255, 0.65)
}

fun Modifier.boxShadow(colorMode: ColorMode) = run {
    boxShadow(
        spreadRadius = 1.px, color = when (colorMode) {
            ColorMode.DARK -> Color.rgba(238, 238, 238, 0.2f)
            ColorMode.LIGHT -> Color.rgba(17, 17, 17, 0.2f)
        }
    )
}

@Composable
fun SmallHeaderLayout(header: @Composable RowScope.() -> Unit, content: @Composable ColumnScope.() -> Unit) =
    Page("Searchit!", modifier = Modifier) {
        Header(header)
        Column(Modifier.padding(top = headerHeight), horizontalAlignment = CenterHorizontally) {
            content()
        }
    }

@Composable
private fun Header(content: @Composable RowScope.() -> Unit) {
    val sitePalette = ColorMode.current.toSitePalette()
    val modifier = Modifier.fontSize(2.cssRem).fontWeight(FontWeight.Bolder).color(sitePalette.brand.primary)

    Row(NavHeaderStyle.toModifier(), verticalAlignment = CenterVertically) {
        SpanText(
            "Searchit!",
            modifier.padding(right = .5f.em).textShadow(0.px, 0.px, blurRadius = 0.5.cssRem, color = Colors.LightGray)
        )
        content()
        ColorModeButton(Modifier.padding(left = .5f.em))
    }
}


@Composable
private fun ColorModeButton(modifier: Modifier = Modifier) = Box(modifier) {
    var colorMode by ColorMode.currentState
    IconButton(onClick = { colorMode = colorMode.opposite }) {
        if (colorMode.isLight) MoonIcon() else SunIcon()
    }
    Tooltip(ElementTarget.PreviousSibling, "Toggle Color Mode", placement = BottomRight)
}
