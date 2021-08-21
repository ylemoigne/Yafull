package fr.javatic.yafull.uikit

import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.w3c.dom.HTMLButtonElement

fun <T : HTMLButtonElement> AttrsBuilder<T>.ukButton(style: UkButtonStyle): AttrsBuilder<T> = fluentClasses("uk-button").fluentClasses(style.applicableStyles)

class UkButtonStyle private constructor(val applicableStyles: MutableSet<String>) {
    val small: UkButtonStyle
        get() {
            applicableStyles.add("uk-button-small")
            return this
        }

    val large: UkButtonStyle
        get() {
            applicableStyles.add("uk-button-large")
            return this
        }

    companion object {
        fun default() = UkButtonStyle(mutableSetOf("uk-button-default"))
        fun primary() = UkButtonStyle(mutableSetOf("uk-button-primary"))
        fun secondary() = UkButtonStyle(mutableSetOf("uk-button-secondary"))
        fun danger() = UkButtonStyle(mutableSetOf("uk-button-danger"))
        fun text() = UkButtonStyle(mutableSetOf("uk-button-text"))
        fun link() = UkButtonStyle(mutableSetOf("uk-button-link"))
    }
}
