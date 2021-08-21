package fr.javatic.yafull.uikit

import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLLIElement
import org.w3c.dom.HTMLUListElement

fun <T : HTMLUListElement> AttrsBuilder<T>.ukNav(style: UkNavStyle) {
    fluentClasses("uk-nav").fluentClasses(style.applicableStyles).attrs(style.applicableAttr)
}

val <T : HTMLElement> AttrsBuilder<T>.ukActive get() = fluentClasses("uk-active")
val <T : HTMLElement> AttrsBuilder<T>.ukParent get() = fluentClasses("uk-parent")
val <T : HTMLUListElement> AttrsBuilder<T>.ukNavSub get() = fluentClasses("uk-nav-sub")

val <T : HTMLLIElement> AttrsBuilder<T>.ukNavHeader get() = fluentClasses("uk-nav-header")
val <T : HTMLLIElement> AttrsBuilder<T>.ukNavDivider get() = fluentClasses("uk-nav-divider")


//Option 	Value 	Default 	Description
//targets 	CSS selector 	> .uk-parent 	The element(s) to toggle.
//toggle 	CSS selector 	> a 	The toggle element(s).
//content 	CSS selector 	> ul 	The content element(s).
//collapsible 	Boolean 	true 	Allow all items to be closed.
//multiple 	Boolean 	false 	Allow multiple open items.
//transition 	String 	ease 	The transition to use.
//animation 	String 	true 	The space separated names of animations to use. Comma separate for animation out.
//duration 	Number 	200 	The animation duration in milliseconds.

class UkNavStyle(val applicableStyles: MutableSet<String>, val applicableAttr: MutableMap<String, String>) {
    fun accordion(multiple: Boolean = false): UkNavStyle {
        if (multiple) applicableAttr["uk-nav"] = "multiple: true"
        else applicableAttr["uk-nav"] = ""
        return this
    }

    val center: UkNavStyle
        get() {
            applicableStyles.add("uk-nav-center")
            return this
        }

    val divider: UkNavStyle
        get() {
            applicableStyles.add("uk-nav-divider")
            return this
        }

    val dropdown: UkNavStyle
        get() {
            applicableStyles.add("uk-dropdown-nav")
            return this
        }


    companion object {
        fun default() = UkNavStyle(mutableSetOf("uk-nav-default"), mutableMapOf())
        fun primary() = UkNavStyle(mutableSetOf("uk-nav-primary"), mutableMapOf())
        fun navbarDropDown() = UkNavStyle(mutableSetOf("uk-navbar-dropdown-nav"), mutableMapOf())
    }
}
