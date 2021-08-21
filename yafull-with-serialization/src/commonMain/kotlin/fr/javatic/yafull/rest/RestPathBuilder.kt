package fr.javatic.yafull.rest

import fr.javatic.kotlinSdkExtensions.uriComponentEncoded

sealed class RestPathBuilder(private val restPathElements: MutableList<RestPathElement>) {
    class Root internal constructor() : RestPathBuilder(mutableListOf()) {
        fun literal(value: String) = addLiteral(value)

        //fun wildcard() = addWildcard()
        //fun separator() = addSeparator()
        fun parameter(param: RestParameter.Path<*>) = addParameter(param)

        fun segment(value: String) = literal(value).separator()
    }

    class LastIsLiteral internal constructor(
        restPathElements: MutableList<RestPathElement>
    ) : RestPathBuilder(restPathElements) {
        fun literal(value: String) = addLiteral(value)

        //fun wildcard() = addWildcard()
        fun separator() = addSeparator()
        fun parameter(param: RestParameter.Path<*>) = addParameter(param)

        fun segment(value: String) = literal(value).separator()
    }

    class LastIsWildcard internal constructor(
        restPathElements: MutableList<RestPathElement>
    ) : RestPathBuilder(restPathElements)

    class LastIsSeparator internal constructor(
        restPathElements: MutableList<RestPathElement>
    ) : RestPathBuilder(restPathElements) {
        fun literal(value: String) = addLiteral(value)

        //fun wildcard() = addWildcard()
        fun parameter(param: RestParameter.Path<*>) = addParameter(param)

        fun segment(value: String) = literal(value).separator()
    }

    class LastIsParameter internal constructor(
        restPathElements: MutableList<RestPathElement>
    ) : RestPathBuilder(restPathElements) {
        fun literal(value: String) = addLiteral(value)
        fun separator() = addSeparator()

        fun segment(value: String) = literal(value).separator()
    }

    protected fun addLiteral(value: String): LastIsLiteral {
        restPathElements.add(createLiteral(value))
        return LastIsLiteral(restPathElements)
    }

//    protected fun addWildcard(): LastIsWildcard {
//        restPathElements.add(RestPathElement.Wildcard)
//        return LastIsWildcard(restPathElements)
//    }

    protected fun addSeparator(): LastIsSeparator {
        restPathElements.add(RestPathElement.Separator)
        return LastIsSeparator(restPathElements)
    }

    protected fun addParameter(restParameterPath: RestParameter.Path<*>): LastIsParameter {
        restPathElements.add(RestPathElement.Param(restParameterPath))
        return LastIsParameter(restPathElements)
    }

    internal fun build(): List<RestPathElement> = restPathElements

    companion object {
        fun create(): Root = Root()

        private fun createLiteral(value: String): RestPathElement.Literal {
            require(value.isNotEmpty()) { "Literal can't be empty" }
            require(!value.contains('/')) { "Literal can't contains `/`" }
            return RestPathElement.Literal(value.uriComponentEncoded(null))
        }
    }
}
