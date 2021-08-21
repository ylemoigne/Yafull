package fr.javatic.yafull.uikit

import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLHRElement

val <T : HTMLHRElement> AttrsBuilder<T>.ukDividerIcon get() = fluentClasses("uk-divider-icon")
val <T : HTMLDivElement> AttrsBuilder<T>.ukDividerIcon get() = fluentClasses("uk-divider-icon")

val <T : HTMLHRElement> AttrsBuilder<T>.ukDividerSmall get() = fluentClasses("uk-divider-small")
val <T : HTMLDivElement> AttrsBuilder<T>.ukDividerSmall get() = fluentClasses("uk-divider-small")

val <T : HTMLHRElement> AttrsBuilder<T>.ukDividerVertical get() = fluentClasses("uk-divider-vertical")
val <T : HTMLDivElement> AttrsBuilder<T>.ukDividerVertical get() = fluentClasses("uk-divider-vertical")
