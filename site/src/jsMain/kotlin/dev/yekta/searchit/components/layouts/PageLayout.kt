package dev.yekta.searchit.components.layouts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.BoxScope
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.ColumnScope
import com.varabyte.kobweb.compose.ui.Alignment.CenterHorizontally
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.gridTemplateRows
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import kotlinx.browser.document
import org.jetbrains.compose.web.css.fr
import org.jetbrains.compose.web.css.percent

private const val TITLE = "Searchit!"

private fun setTitle(title: String?) {
    document.title = if (title == null || title == TITLE) TITLE else "$TITLE - $title"
}

@Composable
fun Page(title: String? = null, modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    LaunchedEffect(title) { setTitle(title) }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .minHeight(100.percent),
        content = content,
    )
}

@Composable
fun PageLayout(title: String? = null, content: @Composable ColumnScope.() -> Unit) {
    Page(title) {
        Column(
            horizontalAlignment = CenterHorizontally,
            modifier = Modifier
                // Create a box with two rows: the main content (fills as much space as it can) and the footer (which reserves
                // space at the bottom). "min-content" means the use the height of the row, which we use for the footer.
                // Since this box is set to *at least* 100%, the footer will always appear at least on the bottom but can be
                // pushed further down if the first row grows beyond the page.
                // Grids are powerful but have a bit of a learning curve. For more info, see:
                // https://css-tricks.com/snippets/css/complete-guide-grid/
                .gridTemplateRows { size(1.fr); size(minContent) },
        ) {
            content()
        }
    }
}
