package fr.javatic.noteapp

import org.jetbrains.compose.web.css.*

object AppStyleSheet : StyleSheet() {
    val fullPage by style {
//        minHeight(100.percent)
//        display(DisplayStyle.Flex)
//        flexDirection(FlexDirection.Column)
//        alignContent(AlignContent.Stretch)
//        alignItems(AlignItems.Stretch)
        flex(1)
    }

    val inlineRightCard by style {
        display(DisplayStyle.InlineBlock)
        position(Position.Absolute)
        marginLeft(20.px)
        property("z-index", 2)
    }

    val width100 by style {
        width(100.percent)
    }
}
