package fr.javatic.yafull.uikit

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

//fun <T : HTMLDivElement> AttrsBuilder<T>.ukModal(
//    escClose: Boolean? = null,
//    bgClose: Boolean? = null,
//    stack: Boolean? = null,
//    container: String? = null,
//    clsPage: String? = null,
//    clsPanel: String? = null,
//    selClose: String? = null
//) = attr(
//    "uk-modal",
//    buildList<String> {
//        escClose?.let { add("esc-close: $escClose") }
//        bgClose?.let { add("bg-close: $bgClose") }
//        stack?.let { add("stack: $stack") }
//        container?.let { add("container: $container") }
//        clsPage?.let { add("cls-page: $clsPage") }
//        clsPanel?.let { add("cls-panel: $clsPanel") }
//        selClose?.let { add("sel-close: $selClose") }
//    }.joinToString(";")
//)

val <T : HTMLDivElement> AttrsBuilder<T>.ukModalDialog get() = fluentClasses("uk-modal-dialog")
val <T : HTMLElement> AttrsBuilder<T>.ukModalBody get() = fluentClasses("uk-modal-body")
val <T : HTMLElement> AttrsBuilder<T>.ukModalTitle get() = fluentClasses("uk-modal-title")

//val <T : HTMLAnchorElement> AttrsBuilder<T>.ukModalClose get() = fluentClasses("uk-modal-close")
//val <T : HTMLButtonElement> AttrsBuilder<T>.ukModalClose get() = fluentClasses("uk-modal-close")
//val <T : HTMLAnchorElement> AttrsBuilder<T>.ukModalCloseDefault get() = fluentClasses("uk-modal-close-default")
//val <T : HTMLButtonElement> AttrsBuilder<T>.ukModalCloseDefault get() = fluentClasses("uk-modal-close-default")
//val <T : HTMLAnchorElement> AttrsBuilder<T>.ukModalCloseOutside get() = fluentClasses("uk-modal-close-outside")
//val <T : HTMLButtonElement> AttrsBuilder<T>.ukModalCloseOutside get() = fluentClasses("uk-modal-close-outside")
val <T : HTMLDivElement> AttrsBuilder<T>.ukModalHeader get() = fluentClasses("uk-modal-header")
val <T : HTMLDivElement> AttrsBuilder<T>.ukModalFooter get() = fluentClasses("uk-modal-footer")
val <T : HTMLElement> AttrsBuilder<T>.ukModalContainer get() = fluentClasses("uk-modal-container")

//val <T : HTMLElement> AttrsBuilder<T>.ukModalCloseFull get() = fluentClasses("uk-modal-close-full")
val <T : HTMLElement> AttrsBuilder<T>.ukModalFull get() = fluentClasses("uk-modal-full")

@Composable
fun UkModalOf(content: @Composable () -> Unit) {
    Div({
        classes("uk-modal", "uk-open", UIKitStylesheet.displayBlock)
    }) {
        content()
    }
}
