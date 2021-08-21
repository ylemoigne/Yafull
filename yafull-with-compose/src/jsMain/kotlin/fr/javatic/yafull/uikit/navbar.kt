package fr.javatic.yafull.uikit

import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.w3c.dom.*

enum class BoundaryAlign(val alignValue: String) {
    LEFT("left"),
    CENTER("center"),
    RIGHT("right");

    companion object {
        val DEFAULT = LEFT
    }
}

enum class DropbarMode {
    PUSH,
    DEFAULT
}

fun <T : HTMLElement> AttrsBuilder<T>.ukNavbar(
    modeClick: Boolean = false,
    dropbar: DropbarMode? = null,
    boundaryAlign: BoundaryAlign? = null
) = fluentClasses("uk-navbar").attr(
    "uk-navbar",
    buildList {
        if (modeClick) add("mode: click")
        when (dropbar) {
            DropbarMode.PUSH -> {
                add("dropbar: true")
                add("dropbar-mode: push")
            }
            DropbarMode.DEFAULT -> {
                add("dropbar: true")
            }
        }
        if (boundaryAlign != null) {
            add("boundary-align: true")
            if (boundaryAlign != BoundaryAlign.DEFAULT) {
                add("align: ${boundaryAlign.alignValue}")
            }
        }
    }.joinToString(";")
)

val <T : Element> AttrsBuilder<T>.ukNavbarContainer get() = fluentClasses("uk-navbar-container")
val <T : Element> AttrsBuilder<T>.ukNavbarLeft get() = fluentClasses("uk-navbar-left")
val <T : Element> AttrsBuilder<T>.ukNavbarCenter get() = fluentClasses("uk-navbar-center")
val <T : Element> AttrsBuilder<T>.ukNavbarRight get() = fluentClasses("uk-navbar-right")

val <T : HTMLUListElement> AttrsBuilder<T>.ukNavbarNav get() = fluentClasses("uk-navbar-nav")

val <T : HTMLLIElement> AttrsBuilder<T>.ukParent get() = fluentClasses("uk-parent")
val <T : HTMLLIElement> AttrsBuilder<T>.ukActive get() = fluentClasses("uk-active")

val <T : Element> AttrsBuilder<T>.ukNavbarTransparent get() = fluentClasses("uk-navbar-transparent")

val <T : Element> AttrsBuilder<T>.ukNavbarSubtitle get() = fluentClasses("uk-navbar-subtitle")

val <T : Element> AttrsBuilder<T>.ukNavbarItem get() = fluentClasses("uk-navbar-item")

val <T : Element> AttrsBuilder<T>.ukNavbarCenterLeft get() = fluentClasses("uk-navbar-center-left")
val <T : Element> AttrsBuilder<T>.ukNavbarCenterRight get() = fluentClasses("uk-navbar-center-right")

val <T : HTMLAnchorElement> AttrsBuilder<T>.ukNavbarToggle
    get() = fluentClasses("uk-navbar-toggle").attr(
        "uk-navbar-toggle-icon",
        ""
    )
val <T : HTMLDivElement> AttrsBuilder<T>.ukNavbarToggle
    get() = fluentClasses("uk-navbar-toggle").attr(
        "uk-navbar-toggle-icon",
        ""
    )

val <T : HTMLDivElement> AttrsBuilder<T>.ukNavbarDropDown get() = fluentClasses("uk-navbar-dropdown")
val <T : HTMLUListElement> AttrsBuilder<T>.ukNavbarDropDownNav get() = fluentClasses("uk-navbar-dropdown-nav")

val <T : HTMLDivElement> AttrsBuilder<T>.ukNavbarDropDownWidth2 get() = fluentClasses("uk-navbar-dropdown-width-2")
val <T : HTMLDivElement> AttrsBuilder<T>.ukNavbarDropDownWidth3 get() = fluentClasses("uk-navbar-dropdown-width-3")
val <T : HTMLDivElement> AttrsBuilder<T>.ukNavbarDropDownWidth4 get() = fluentClasses("uk-navbar-dropdown-width-4")
val <T : HTMLDivElement> AttrsBuilder<T>.ukNavbarDropDownWidth5 get() = fluentClasses("uk-navbar-dropdown-width-5")

val <T : HTMLDivElement> AttrsBuilder<T>.ukNavbarDropbar get() = fluentClasses("uk-navbar-dropdown-width-5")

