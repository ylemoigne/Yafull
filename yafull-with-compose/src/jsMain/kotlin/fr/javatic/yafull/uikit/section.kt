package fr.javatic.yafull.uikit

import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.w3c.dom.Element

fun <T : Element> AttrsBuilder<T>.ukSection(style: UkSectionStyle): AttrsBuilder<T> = fluentClasses("uk-section").fluentClasses(style.applicableStyles)

class UkSectionStyle(val applicableStyles: MutableSet<String>) {
    val preserveColor: UkSectionStyle
        get() {
            applicableStyles.add("uk-preserve-color")
            return this
        }

    val xsmall: UkSectionStyle
        get() {
            applicableStyles.add("uk-section-xsmall")
            return this
        }

    val small: UkSectionStyle
        get() {
            applicableStyles.add("uk-section-small")
            return this
        }

    val large: UkSectionStyle
        get() {
            applicableStyles.add("uk-section-large")
            return this
        }

    val xlarge: UkSectionStyle
        get() {
            applicableStyles.add("uk-section-xlarge")
            return this
        }

    val overlap: UkSectionStyle
        get() {
            applicableStyles.add("uk-section-overlap")
            return this
        }

    companion object {
        fun default() = UkSectionStyle(mutableSetOf("uk-section-default"))
        fun muted() = UkSectionStyle(mutableSetOf("uk-section-muted"))
        fun primary() = UkSectionStyle(mutableSetOf("uk-section-primary"))
        fun secondary() = UkSectionStyle(mutableSetOf("uk-section-secondary"))
    }
}
