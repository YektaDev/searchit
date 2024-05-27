package dev.yekta.searchit.components.widgets

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.setVariable
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.forms.ButtonVars
import dev.yekta.searchit.CircleButtonVariant
import dev.yekta.searchit.UncoloredButtonVariant
import org.jetbrains.compose.web.css.em

@Composable
fun IconButton(onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable () -> Unit) = Button(
    modifier = modifier.setVariable(
        ButtonVars.FontSize,
        1.em
    ), // Make button icon size relative to parent container font size
    onClick = { onClick() },
    variant = CircleButtonVariant.then(UncoloredButtonVariant)
) {
    content()
}