package fr.javatic.yafull.uikit

import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.w3c.dom.HTMLDivElement

fun <T : HTMLDivElement> AttrsBuilder<T>.ukGrid(masonry: Boolean? = null, parallax: Int? = null, margin: String? = null, firstColumn: String? = null) = attr(
    "uk-grid",
    buildList<String> {
        masonry?.let { add("masonry: $it") }
        parallax?.let { add("parallax: $it") }
        margin?.let { add("margin: $it") }
        firstColumn?.let { add("first-column: $it") }
    }.joinToString(";")
)

val <T : HTMLDivElement> AttrsBuilder<T>.ukGridSmall get() = fluentClasses("uk-grid-small")
val <T : HTMLDivElement> AttrsBuilder<T>.ukGridMedium get() = fluentClasses("uk-grid-medium")
val <T : HTMLDivElement> AttrsBuilder<T>.ukGridLarge get() = fluentClasses("uk-grid-large")
val <T : HTMLDivElement> AttrsBuilder<T>.ukGridCollapse get() = fluentClasses("uk-grid-collapse")

val <T : HTMLDivElement> AttrsBuilder<T>.ukGridColumnSmall get() = fluentClasses("uk-grid-column-small")
val <T : HTMLDivElement> AttrsBuilder<T>.ukGridRowSmall get() = fluentClasses("uk-grid-row-small")
val <T : HTMLDivElement> AttrsBuilder<T>.ukGridColumnMedium get() = fluentClasses("uk-grid-column-medium")
val <T : HTMLDivElement> AttrsBuilder<T>.ukGridRowMedium get() = fluentClasses("uk-grid-row-medium")
val <T : HTMLDivElement> AttrsBuilder<T>.ukGridColumnLarge get() = fluentClasses("uk-grid-column-large")
val <T : HTMLDivElement> AttrsBuilder<T>.ukGridRowLarge get() = fluentClasses("uk-grid-row-large")
val <T : HTMLDivElement> AttrsBuilder<T>.ukGridColumnCollapse get() = fluentClasses("uk-grid-column-collapse")
val <T : HTMLDivElement> AttrsBuilder<T>.ukGridRowCollapse get() = fluentClasses("uk-grid-row-collapse")

val <T : HTMLDivElement> AttrsBuilder<T>.ukGridDivider get() = fluentClasses("uk-grid-divider")

val <T : HTMLDivElement> AttrsBuilder<T>.ukGridMatch get() = fluentClasses("uk-grid-match")
val <T : HTMLDivElement> AttrsBuilder<T>.ukGridItemMatch get() = fluentClasses("uk-grid-item-match")

