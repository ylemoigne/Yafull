package fr.javatic.yafull.uikit

import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.w3c.dom.Element

fun <T : Element> AttrsBuilder<T>.ukCard(style: UkCardStyle): AttrsBuilder<T> = fluentClasses("uk-card").fluentClasses(style.applicableStyles)
val <T : Element> AttrsBuilder<T>.ukCardHeader get() = fluentClasses("uk-card-header")
val <T : Element> AttrsBuilder<T>.ukCardBody get() = fluentClasses("uk-card-body")
val <T : Element> AttrsBuilder<T>.ukCardFooter get() = fluentClasses("uk-card-footer")
val <T : Element> AttrsBuilder<T>.ukCardTitle get() = fluentClasses("uk-card-title")

class UkCardStyle(val applicableStyles: MutableSet<String>) {
    val hover: UkCardStyle
        get() {
            applicableStyles.add("uk-card-hover")
            return this
        }

    val small: UkCardStyle
        get() {
            applicableStyles.add("uk-card-small")
            return this
        }

    val large: UkCardStyle
        get() {
            applicableStyles.add("uk-card-large")
            return this
        }

    companion object {
        fun default() = UkCardStyle(mutableSetOf("uk-card-default"))
        fun primary() = UkCardStyle(mutableSetOf("uk-card-primary"))
        fun secondary() = UkCardStyle(mutableSetOf("uk-card-secondary"))
    }
}


