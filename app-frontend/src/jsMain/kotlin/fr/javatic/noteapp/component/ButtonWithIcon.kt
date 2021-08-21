package fr.javatic.noteapp.component

import androidx.compose.runtime.Composable
import androidx.compose.web.events.SyntheticMouseEvent
import fr.javatic.yafull.uikit.*
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLButtonElement

@Composable
fun ButtonWithIcon(
    style: UkButtonStyle,
    icon: String,
    text: String?,
    attrs: AttrBuilderContext<HTMLButtonElement>? = null,
    onClick: (SyntheticMouseEvent) -> Unit
) {
    Button({
        ukTextNowrap
        ukButton(style)
        this.onClick { onClick(it) }
        attrs?.invoke(this)
    }) {
        Span({
            ukIcon(icon)
        })
        if (text != null) {
            Span({ ukMarginSmallLeft }) {
                Text(text)
            }
        }
    }
}
