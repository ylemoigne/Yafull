package fr.javatic.yafull.uikit

import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLParagraphElement

fun <T : HTMLDivElement> AttrsBuilder<T>.ukPanel(scrollable: Boolean) {
    fluentClasses("uk-panel")
    if (scrollable) {
        fluentClasses("uk-panel-scrollable")
    }
}

val <T : Element> AttrsBuilder<T>.ukFloatLeft get() = fluentClasses("uk-float-left")
val <T : Element> AttrsBuilder<T>.ukFloatRight get() = fluentClasses("uk-float-right")
val <T : Element> AttrsBuilder<T>.ukClearfix get() = fluentClasses("uk-clearfix")

val <T : Element> AttrsBuilder<T>.ukOverflowAuto get() = fluentClasses("uk-overflow-auto")
val <T : Element> AttrsBuilder<T>.ukOverflowHidden get() = fluentClasses("uk-overflow-hidden")

fun <T : Element> AttrsBuilder<T>.ukOverflowAutoFillRemainingHeight(
    selContainer: String? = null,
    selContent: String? = null
) = attr(
    "uk-overflow-auto",
    buildList {
        selContainer?.let { add("selContainer: $it") }
        selContent?.let { add("selContent: $it") }
    }.joinToString(";")
)

val <T : Element> AttrsBuilder<T>.ukResize get() = fluentClasses("uk-resize")
val <T : Element> AttrsBuilder<T>.ukResizeVertical get() = fluentClasses("uk-resize-vertical")

val <T : Element> AttrsBuilder<T>.ukDisplayBlock get() = fluentClasses("uk-display-block")
val <T : Element> AttrsBuilder<T>.ukDisplayInline get() = fluentClasses("uk-display-inline")
val <T : Element> AttrsBuilder<T>.ukDisplayInlineBlock get() = fluentClasses("uk-display-inline-block")

val <T : Element> AttrsBuilder<T>.ukInline get() = fluentClasses("uk-inline")
val <T : Element> AttrsBuilder<T>.ukInlineClip get() = fluentClasses("uk-inline-clip")

val <T : Element> AttrsBuilder<T>.ukBorderRounded get() = fluentClasses("uk-border-rounded")
val <T : Element> AttrsBuilder<T>.ukBorderCircle get() = fluentClasses("uk-border-circle")
val <T : Element> AttrsBuilder<T>.ukBorderPill get() = fluentClasses("uk-border-pill")

val <T : Element> AttrsBuilder<T>.ukBoxShadowSmall get() = fluentClasses("uk-box-shadow-small")
val <T : Element> AttrsBuilder<T>.ukBoxShadowMedium get() = fluentClasses("uk-box-shadow-medium")
val <T : Element> AttrsBuilder<T>.ukBoxShadowLarge get() = fluentClasses("uk-box-shadow-large")
val <T : Element> AttrsBuilder<T>.ukBoxShadowXLarge get() = fluentClasses("uk-box-shadow-xlarge")

val <T : Element> AttrsBuilder<T>.ukBoxShadowBottom get() = fluentClasses("uk-box-shadow-bottom")

val <T : Element> AttrsBuilder<T>.ukBoxShadowHoverSmall get() = fluentClasses("uk-box-shadow-hover-small")
val <T : Element> AttrsBuilder<T>.ukBoxShadowHoverMedium get() = fluentClasses("uk-box-shadow-hover-medium")
val <T : Element> AttrsBuilder<T>.ukBoxShadowHoverLarge get() = fluentClasses("uk-box-shadow-hover-large")
val <T : Element> AttrsBuilder<T>.ukBoxShadowHoverXLarge get() = fluentClasses("uk-box-shadow-hover-xlarge")

val <T : HTMLParagraphElement> AttrsBuilder<T>.ukDropcap get() = fluentClasses("uk-dropcap")

val <T : HTMLElement> AttrsBuilder<T>.ukLogo get() = fluentClasses("uk-logo")
val <T : HTMLElement> AttrsBuilder<T>.ukLogoInverse get() = fluentClasses("uk-logo-inverse")

//    .uk-blend-multiply 	This class sets the blend mode to multiply.
//        .uk-blend-screen 	This class sets the blend mode to screen.
//        .uk-blend-overlay 	This class sets the blend mode to overlay.
//        .uk-blend-darken 	This class sets the blend mode to darken.
//        .uk-blend-lighten 	This class sets the blend mode to lighten.
//        .uk-blend-color-dodge 	This class sets the blend mode to color dodge.
//    .uk-blend-color-burn 	This class sets the blend mode to color burn.
//    .uk-blend-hard-light 	This class sets the blend mode to hard light.
//    .uk-blend-soft-light 	This class sets the blend mode to soft light.
//    .uk-blend-difference 	This class sets the blend mode to difference.
//        .uk-blend-exclusion 	This class sets the blend mode to exclusion.
//        .uk-blend-hue 	This class sets the blend mode to hue.
//        .uk-blend-saturation 	This class sets the blend mode to saturation.
//        .uk-blend-color 	This class sets the blend mode to color.
//        .uk-blend-luminosity 	This class sets the blend mode to luminosity.

val <T : HTMLElement> AttrsBuilder<T>.ukTransformCenter get() = fluentClasses("uk-transform-center")

//    .uk-transform-origin-top-left 	The transition originates from the top left.
//        .uk-transform-origin-top-center 	The transition originates from the top.
//    .uk-transform-origin-top-right 	The transition originates from the top right.
//        .uk-transform-origin-center-left 	The transition originates from the left.
//    .uk-transform-origin-center-right 	The transition originates from the right.
//    .uk-transform-origin-bottom-left 	The transition originates from the bottom left.
//        .uk-transform-origin-bottom-center 	The transition originates from the bottom.
//    .uk-transform-origin-bottom-right 	The transition originates from the bottom right.

val <T : HTMLElement> AttrsBuilder<T>.ukDisabled get() = fluentClasses("uk-disabled")

val <T : HTMLElement> AttrsBuilder<T>.ukDrag get() = fluentClasses("uk-drag")
val <T : HTMLElement> AttrsBuilder<T>.ukDragover get() = fluentClasses("uk-dragover")
