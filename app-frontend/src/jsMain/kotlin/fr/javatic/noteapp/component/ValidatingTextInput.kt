package fr.javatic.noteapp.component

import androidx.compose.runtime.Composable
import fr.javatic.noteapp.AppStyleSheet
import fr.javatic.yafull.uikit.*
import org.jetbrains.compose.web.attributes.builders.InputAttrsBuilder
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.PasswordInput
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.TextInput
import org.jetbrains.compose.web.events.SyntheticInputEvent
import org.w3c.dom.HTMLInputElement

@Composable
fun ValidatingInput(
    constructionFn: @Composable (String, InputAttrsBuilder<String>.() -> Unit) -> Unit,
    value: String,
    inputId: String,
    validity: () -> Boolean?,
    messageRenderer: @Composable (() -> Unit)? = null,
    onInput: (SyntheticInputEvent<String, HTMLInputElement>) -> Unit
) {
    val valid = validity()
    Div({ ukInline }) {
        Span(attrs = {
            when (valid) {
                null -> {
                    ukInvisible
                    ukFormIcon(UkIcon.flickr, true)
                }
                true -> {
                    ukTextSuccess
                    ukFormIcon(UkIcon.check, true)
                }
                false -> {
                    ukTextDanger
                    ukFormIcon(UkIcon.ban, true)
                }
            }
        })
        constructionFn(value) {
            ukInput
            when (valid) {
                true -> ukFormSuccess
                false -> ukFormDanger
            }

            id(inputId)
            onInput(listener = onInput)
        }
        if (messageRenderer != null) {
            Div({
                if (valid == null) ukInvisible
                classes(AppStyleSheet.inlineRightCard)
                ukCard(UkCardStyle.default())
            }) {
                messageRenderer()
            }
        }
    }
}

@Composable
fun ValidatingTextInput(
    value: String,
    inputId: String,
    validity: () -> Boolean?,
    messageRenderer: @Composable (() -> Unit)? = null,
    onInput: (SyntheticInputEvent<String, HTMLInputElement>) -> Unit
) = ValidatingInput(@Composable { v, builder ->
    TextInput(v, builder)
}, value, inputId, validity, messageRenderer, onInput)

@Composable
fun ValidatingPasswordInput(
    value: String,
    inputId: String,
    validity: () -> Boolean?,
    messageRenderer: @Composable() (() -> Unit)? = null,
    onInput: (SyntheticInputEvent<String, HTMLInputElement>) -> Unit
) = ValidatingInput(@Composable { v, builder ->
    PasswordInput(v, builder)
}, value, inputId, validity, messageRenderer, onInput)
