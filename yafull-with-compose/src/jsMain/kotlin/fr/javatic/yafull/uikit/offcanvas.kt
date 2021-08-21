package fr.javatic.yafull.uikit

import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement

fun <T : Element> AttrsBuilder<T>.ukOffcanvas(
    overlay: Boolean? = null,
    flip: Boolean? = null,
    mode: UkOffcanvasMode? = null,
    escClose: Boolean? = null,
    container: String? = null
) = attr(
    "uk-offcanvas",
    buildList {
        overlay?.let { add("overlay: $it") }
        flip?.let { add("flip: $it") }
        mode?.let { add("mode: ${it.value}") }
        escClose?.let { add("bg-close: $it") }
        container?.let { add("container: $it") }
    }.joinToString(";")
)

enum class UkOffcanvasMode(val value: String) {
    SLIDE("slide"),
    PUSH("push"),
    REVEAL("reveal"),
    NONE("none"),
}

val <T : HTMLElement> AttrsBuilder<T>.ukOffcanvasBar get() = fluentClasses("uk-offcanvas-bar")
val <T : HTMLElement> AttrsBuilder<T>.ukOffcanvasClose get() = fluentClasses("uk-offcanvas-close")
