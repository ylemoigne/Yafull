package fr.javatic.yafull.uikit

import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.w3c.dom.HTMLElement

fun <T : HTMLElement> AttrsBuilder<T>.ukTooltip(
    title: String,
    pos: TooltipPosition? = null,
    offset: Int? = null,
    animation: String? = null,
    duration: Int? = null,
    delay: Int? = null,
    cls: Int? = null,
    container: String? = null,
) = attr(
    "uk-tooltip",
    buildList<String> {
        add("title: $title")
        pos?.let { add("pos: ${pos.value}") }
        offset?.let { add("offset: $offset") }
        animation?.let { add("animation: $animation") }
        duration?.let { add("duration: $duration") }
        delay?.let { add("delay: $delay") }
        cls?.let { add("cls: $cls") }
        container?.let { add("container: $container") }
    }.joinToString(";")
)

enum class TooltipPosition(val value: String) {
    TOP("top"),
    TOP_LEFT("top-left"),
    TOP_RIGHT("top-right"),
    BOTTOM("bottom"),
    BOTTOM_LEFT("bottom-left"),
    BOTTOM_RIGHT("bottom-right"),
    LEFT("left"),
    RIGHT("right"),
}
