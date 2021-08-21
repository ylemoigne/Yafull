package fr.javatic.yafull.uikit

import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.w3c.dom.HTMLElement

val <T : HTMLElement> AttrsBuilder<T>.ukHidden get() = fluentClasses("uk-hidden")
val <T : HTMLElement> AttrsBuilder<T>.ukInvisible get() = fluentClasses("uk-invisible")

val <T : HTMLElement> AttrsBuilder<T>.ukHiddenHover get() = fluentClasses("uk-hidden-hover")
val <T : HTMLElement> AttrsBuilder<T>.ukInvisibleHover get() = fluentClasses("uk-invisible-hover")

val <T : HTMLElement> AttrsBuilder<T>.ukHiddenTouch get() = fluentClasses("uk-hidden-touch")
val <T : HTMLElement> AttrsBuilder<T>.ukHiddenNotouch get() = fluentClasses("uk-hidden-notouch")
