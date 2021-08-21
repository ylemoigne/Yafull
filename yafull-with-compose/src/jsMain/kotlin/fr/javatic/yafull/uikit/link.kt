package fr.javatic.yafull.uikit

import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.w3c.dom.HTMLElement

val <T : HTMLElement> AttrsBuilder<T>.ukLinkMuted get() = fluentClasses("uk-link-muted")
val <T : HTMLElement> AttrsBuilder<T>.ukLinkText get() = fluentClasses("uk-link-text")
val <T : HTMLElement> AttrsBuilder<T>.ukLinkHeading get() = fluentClasses("uk-link-heading")
val <T : HTMLElement> AttrsBuilder<T>.ukLinkReset get() = fluentClasses("uk-link-reset")
val <T : HTMLElement> AttrsBuilder<T>.ukLinkToggle get() = fluentClasses("uk-link-toggle")
