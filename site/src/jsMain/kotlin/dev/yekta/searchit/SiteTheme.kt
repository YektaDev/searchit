package dev.yekta.searchit

import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Color.Companion.rgb
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.palette.background
import com.varabyte.kobweb.silk.theme.colors.palette.color
import dev.yekta.searchit.SitePalette.Brand

/**
 * @property nearBackground A useful color to apply to a container that should differentiate itself from the background
 *   but just a little.
 */
class SitePalette(
    val nearBackground: Color,
    val brand: Brand,
) {
    class Brand(
        val primary: Color = rgb(0xF3DB5B),
        val accent: Color = rgb(0x3C83EF),
    )
}

object SitePalettes {
    val light = SitePalette(
        nearBackground = rgb(0xF4F6FA),
        brand = Brand(
            primary = rgb(0xFCBA03),
            accent = rgb(0x3C83EF),
        )
    )
    val dark = SitePalette(
        nearBackground = rgb(0x13171F),
        brand = Brand(
            primary = rgb(0xF3DB5B),
            accent = rgb(0x3C83EF),
        )
    )
}

fun ColorMode.toSitePalette(): SitePalette = when (this) {
    ColorMode.LIGHT -> SitePalettes.light
    ColorMode.DARK -> SitePalettes.dark
}

@InitSilk
fun initTheme(ctx: InitSilkContext) {
    ctx.theme.palettes.light.background = rgb(0xFAFAFA)
    ctx.theme.palettes.light.color = Colors.Black
    ctx.theme.palettes.dark.background = rgb(0x06080B)
    ctx.theme.palettes.dark.color = Colors.White
}
