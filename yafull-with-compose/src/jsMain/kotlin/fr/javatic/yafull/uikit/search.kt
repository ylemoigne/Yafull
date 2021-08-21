package fr.javatic.yafull.uikit

import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.w3c.dom.HTMLElement

val <T : HTMLElement> AttrsBuilder<T>.ukSearch get() = fluentClasses("uk-search")
val <T : HTMLElement> AttrsBuilder<T>.ukSearchInput get() = fluentClasses("uk-search-input")

val <T : HTMLElement> AttrsBuilder<T>.ukSearchIcon get() = attr("uk-search-icon", "")

val <T : HTMLElement> AttrsBuilder<T>.ukSearchDefault get() = fluentClasses("uk-search-default")

val <T : HTMLElement> AttrsBuilder<T>.ukSearchLarge get() = fluentClasses("uk-search-large")
val <T : HTMLElement> AttrsBuilder<T>.ukSearchNavbar get() = fluentClasses("uk-search-navbar")
val <T : HTMLElement> AttrsBuilder<T>.ukSearchToggle get() = fluentClasses("uk-search-toggle")

