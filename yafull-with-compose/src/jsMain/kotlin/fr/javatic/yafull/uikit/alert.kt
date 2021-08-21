package fr.javatic.yafull.uikit

import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.w3c.dom.HTMLElement

val <T : HTMLElement> AttrsBuilder<T>.ukAlert get() = attr("uk-alert", "")
val <T : HTMLElement> AttrsBuilder<T>.ukAlertClose get() = fluentClasses("uk-alert-close")

val <T : HTMLElement> AttrsBuilder<T>.ukAlertPrimary get() = fluentClasses("uk-alert-primary")
val <T : HTMLElement> AttrsBuilder<T>.ukAlertSuccess get() = fluentClasses("uk-alert-success")
val <T : HTMLElement> AttrsBuilder<T>.ukAlertWarning get() = fluentClasses("uk-alert-warning")
val <T : HTMLElement> AttrsBuilder<T>.ukAlertDanger get() = fluentClasses("uk-alert-danger")

