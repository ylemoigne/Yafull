package fr.javatic.yafull.uikit

import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.w3c.dom.HTMLElement

val <T : HTMLElement> AttrsBuilder<T>.ukLight get() = fluentClasses("uk-light")
val <T : HTMLElement> AttrsBuilder<T>.ukDark get() = fluentClasses("uk-dark")
