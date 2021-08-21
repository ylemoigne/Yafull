package fr.javatic.yafull.uikit

import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.w3c.dom.HTMLElement

val <T : HTMLElement> AttrsBuilder<T>.ukAlignLeft get() = fluentClasses("uk-align-left")
val <T : HTMLElement> AttrsBuilder<T>.ukAlignRight get() = fluentClasses("uk-align-right")
val <T : HTMLElement> AttrsBuilder<T>.ukAlignCenter get() = fluentClasses("uk-align-center")
