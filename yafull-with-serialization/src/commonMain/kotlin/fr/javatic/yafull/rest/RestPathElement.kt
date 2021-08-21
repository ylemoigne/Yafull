package fr.javatic.yafull.rest

sealed class RestPathElement {
    object Separator : RestPathElement() {
        override fun toString(): String = "/"
    }

    //object Wildcard : RestPathElement()
    class Param(val restParameter: RestParameter.Path<*>) : RestPathElement() {
        override fun toString(): String = "{${restParameter.name}}"
    }

    class Literal(val value: String) : RestPathElement() {
        override fun toString(): String = value
    }
}
