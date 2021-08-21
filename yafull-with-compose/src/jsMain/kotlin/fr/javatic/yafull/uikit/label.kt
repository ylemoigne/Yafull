package fr.javatic.yafull.uikit

import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.w3c.dom.HTMLSpanElement

fun <T : HTMLSpanElement> AttrsBuilder<T>.ukLabel(type: UkLabelType = UkLabelType.DEFAULT) {
    fluentClasses("uk-label")
    when (type) {
        UkLabelType.SUCCESS -> fluentClasses("uk-label-success")
        UkLabelType.WARNING -> fluentClasses("uk-label-warning")
        UkLabelType.DANGER -> fluentClasses("uk-label-danger")
        UkLabelType.DEFAULT -> Unit
    }
}

enum class UkLabelType {
    DEFAULT,
    SUCCESS,
    WARNING,
    DANGER
}
