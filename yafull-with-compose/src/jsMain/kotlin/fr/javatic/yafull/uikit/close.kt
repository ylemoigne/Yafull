package fr.javatic.yafull.uikit

import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLButtonElement

val <T : HTMLButtonElement> AttrsBuilder<T>.ukClose get() = attr("uk-close", "")
val <T : HTMLAnchorElement> AttrsBuilder<T>.ukClose get() = attr("uk-close", "")
val <T : HTMLButtonElement> AttrsBuilder<T>.ukCloseLarge get() = fluentClasses("uk-close-large")
val <T : HTMLAnchorElement> AttrsBuilder<T>.ukCloseLarge get() = fluentClasses("uk-close-large")
