package fr.javatic.yafull.uikit

import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.w3c.dom.HTMLElement

val <T : HTMLElement> AttrsBuilder<T>.ukLink get() = fluentClasses("uk-link")

val <T : HTMLElement> AttrsBuilder<T>.ukH1 get() = fluentClasses("uk-h1")
val <T : HTMLElement> AttrsBuilder<T>.ukH2 get() = fluentClasses("uk-h2")
val <T : HTMLElement> AttrsBuilder<T>.ukH3 get() = fluentClasses("uk-h3")
val <T : HTMLElement> AttrsBuilder<T>.ukH4 get() = fluentClasses("uk-h4")
val <T : HTMLElement> AttrsBuilder<T>.ukH5 get() = fluentClasses("uk-h5")
val <T : HTMLElement> AttrsBuilder<T>.ukH6 get() = fluentClasses("uk-h6")
