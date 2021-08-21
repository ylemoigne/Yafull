package fr.javatic.yafull.uikit

import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.w3c.dom.Element

// animation: uk-animation-slide-top
fun <T : Element> AttrsBuilder<T>.ukSticky(
    offset: Int? = null,
    top: String? = null,
    showOnUp: Boolean? = null,
    bottom: String? = null
) = attr(
    "uk-sticky",
    buildList {
        if (offset != null) add("offset: $offset")
        if (top != null) add("top: $top")
        if (showOnUp != null) add("show-on-up: $showOnUp")
        if (bottom != null) add("bottom: $bottom")
    }.joinToString(";")
)
