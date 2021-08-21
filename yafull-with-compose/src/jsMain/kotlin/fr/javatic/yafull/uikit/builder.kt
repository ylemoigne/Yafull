package fr.javatic.yafull.uikit

import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.w3c.dom.Element

fun <T : Element> AttrsBuilder<T>.fluentClasses(vararg classes: String): AttrsBuilder<T> {
    classes(*classes)
    return this
}

fun <T : Element> AttrsBuilder<T>.fluentClasses(classes: Set<String>): AttrsBuilder<T> {
    classes(*classes.toTypedArray())
    return this
}

fun <T : Element> AttrsBuilder<T>.attrs(attrs: Map<String, String>): AttrsBuilder<T> {
    attrs.forEach { attr(it.key, it.value) }
    return this
}




