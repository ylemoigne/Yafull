package fr.javatic.yafull.router

sealed class BrowserPathElement {
    object Separator : BrowserPathElement() {
        override fun toString(): String = "<separator>"
    }

    object Wildcard : BrowserPathElement() {
        override fun toString(): String = "<wildcard>"
    }

    data class Parameter(val parameter: BrowserParameterPath<*>) : BrowserPathElement()
    data class Literal(val value: String) : BrowserPathElement()
}
