package fr.javatic.yafull.uikit

import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.w3c.dom.Element

fun <T : Element> AttrsBuilder<T>.ukMarginProp(margin: String? = null, firstColumn: String? = null): AttrsBuilder<T> =
    attr(
        "uk-margin",
        buildList {
            if (margin != null) add("margin: $margin")
            if (firstColumn != null) add("first-column: $firstColumn")
        }.joinToString(";")
    )

object UkMarginPropValue {
    val ukMargin = "uk-margin"
    val ukMarginTop = "uk-margin-top"
    val ukMarginBottom = "uk-margin-bottom"
    val ukMarginLeft = "uk-margin-left"
    val ukMarginRight = "uk-margin-right"

    val ukMarginSmall = "uk-margin-small"
    val ukMarginSmallTop = "uk-margin-small-top"
    val ukMarginSmallBottom = "uk-margin-small-bottom"
    val ukMarginSmallLeft = "uk-margin-small-left"
    val ukMarginSmallRight = "uk-margin-small-right"

    val ukMarginMedium = "uk-margin-medium"
    val ukMarginMediumTop = "uk-margin-medium-top"
    val ukMarginMediumBottom = "uk-margin-medium-bottom"
    val ukMarginMediumLeft = "uk-margin-medium-left"
    val ukMarginMediumRight = "uk-margin-medium-right"

    val ukMarginLarge = "uk-margin-large"
    val ukMarginLargeTop = "uk-margin-large-top"
    val ukMarginLargeBottom = "uk-margin-large-bottom"
    val ukMarginLargeLeft = "uk-margin-large-left"
    val ukMarginLargeRight = "uk-margin-large-right"

    val ukMarginXLarge = "uk-margin-xlarge"
    val ukMarginXLargeTop = "uk-margin-xlarge-top"
    val ukMarginXLargeBottom = "uk-margin-xlarge-bottom"
    val ukMarginXLargeLeft = "uk-margin-xlarge-left"
    val ukMarginXLargeRight = "uk-margin-xlarge-right"

}

val <T : Element> AttrsBuilder<T>.ukMargin get() = fluentClasses("uk-margin")
val <T : Element> AttrsBuilder<T>.ukMarginTop get() = fluentClasses("uk-margin-top")
val <T : Element> AttrsBuilder<T>.ukMarginBottom get() = fluentClasses("uk-margin-bottom")
val <T : Element> AttrsBuilder<T>.ukMarginLeft get() = fluentClasses("uk-margin-left")
val <T : Element> AttrsBuilder<T>.ukMarginRight get() = fluentClasses("uk-margin-right")

val <T : Element> AttrsBuilder<T>.ukMarginSmall get() = fluentClasses("uk-margin-small")
val <T : Element> AttrsBuilder<T>.ukMarginSmallTop get() = fluentClasses("uk-margin-small-top")
val <T : Element> AttrsBuilder<T>.ukMarginSmallBottom get() = fluentClasses("uk-margin-small-bottom")
val <T : Element> AttrsBuilder<T>.ukMarginSmallLeft get() = fluentClasses("uk-margin-small-left")
val <T : Element> AttrsBuilder<T>.ukMarginSmallRight get() = fluentClasses("uk-margin-small-right")

val <T : Element> AttrsBuilder<T>.ukMarginMedium get() = fluentClasses("uk-margin-medium")
val <T : Element> AttrsBuilder<T>.ukMarginMediumTop get() = fluentClasses("uk-margin-medium-top")
val <T : Element> AttrsBuilder<T>.ukMarginMediumBottom get() = fluentClasses("uk-margin-medium-bottom")
val <T : Element> AttrsBuilder<T>.ukMarginMediumLeft get() = fluentClasses("uk-margin-medium-left")
val <T : Element> AttrsBuilder<T>.ukMarginMediumRight get() = fluentClasses("uk-margin-medium-right")

val <T : Element> AttrsBuilder<T>.ukMarginLarge get() = fluentClasses("uk-margin-large")
val <T : Element> AttrsBuilder<T>.ukMarginLargeTop get() = fluentClasses("uk-margin-large-top")
val <T : Element> AttrsBuilder<T>.ukMarginLargeBottom get() = fluentClasses("uk-margin-large-bottom")
val <T : Element> AttrsBuilder<T>.ukMarginLargeLeft get() = fluentClasses("uk-margin-large-left")
val <T : Element> AttrsBuilder<T>.ukMarginLargeRight get() = fluentClasses("uk-margin-large-right")

val <T : Element> AttrsBuilder<T>.ukMarginXLarge get() = fluentClasses("uk-margin-xlarge")
val <T : Element> AttrsBuilder<T>.ukMarginXLargeTop get() = fluentClasses("uk-margin-xlarge-top")
val <T : Element> AttrsBuilder<T>.ukMarginXLargeBottom get() = fluentClasses("uk-margin-xlarge-bottom")
val <T : Element> AttrsBuilder<T>.ukMarginXLargeLeft get() = fluentClasses("uk-margin-xlarge-left")
val <T : Element> AttrsBuilder<T>.ukMarginXLargeRight get() = fluentClasses("uk-margin-xlarge-right")

val <T : Element> AttrsBuilder<T>.ukMarginRemove get() = fluentClasses("uk-margin-remove")
val <T : Element> AttrsBuilder<T>.ukMarginRemoveTop get() = fluentClasses("uk-margin-remove-top")
val <T : Element> AttrsBuilder<T>.ukMarginRemoveBottom get() = fluentClasses("uk-margin-remove-bottom")
val <T : Element> AttrsBuilder<T>.ukMarginRemoveLeft get() = fluentClasses("uk-margin-remove-left")
val <T : Element> AttrsBuilder<T>.ukMarginRemoveRight get() = fluentClasses("uk-margin-remove-right")
val <T : Element> AttrsBuilder<T>.ukMarginRemoveVertical get() = fluentClasses("uk-margin-remove-vertical")
val <T : Element> AttrsBuilder<T>.ukMarginRemoveAdjacent get() = fluentClasses("uk-margin-remove-adjacent")
val <T : Element> AttrsBuilder<T>.ukMarginRemoveFistChild get() = fluentClasses("uk-margin-remove-first-child")
val <T : Element> AttrsBuilder<T>.ukMarginRemoveLastChild get() = fluentClasses("uk-margin-remove-last-child")

val <T : Element> AttrsBuilder<T>.ukMarginAuto get() = fluentClasses("uk-margin-auto")
val <T : Element> AttrsBuilder<T>.ukMarginAutoTop get() = fluentClasses("uk-margin-auto-top")
val <T : Element> AttrsBuilder<T>.ukMarginAutoBottom get() = fluentClasses("uk-margin-auto-bottom")
val <T : Element> AttrsBuilder<T>.ukMarginAutoLeft get() = fluentClasses("uk-margin-auto-left")
val <T : Element> AttrsBuilder<T>.ukMarginAutoRight get() = fluentClasses("uk-margin-auto-right")
val <T : Element> AttrsBuilder<T>.ukMarginAutoVertical get() = fluentClasses("uk-margin-auto-vertical")
