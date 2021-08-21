package fr.javatic.yafull.uikit

import org.jetbrains.compose.web.css.*

/**
 * We override some uikit class which are intercepted by uikit js which manipulate dom structure in way that break compose
 */
object UIKitStylesheet : StyleSheet() {
    val displayBlock by style {
        display(DisplayStyle.Block)
    }
    val ukModalCloseDefault by style {
        position(Position.Absolute)
        property("z-index", 1010)
        top(10.px)
        right(10.px)
        padding(5.px)
    }
}
