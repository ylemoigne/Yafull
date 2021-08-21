package fr.javatic.yafull.router

import fr.javatic.kotlinSdkExtensions.uriComponentEncoded

sealed class BrowserPathBuilder(private val pathElements: MutableList<BrowserPathElement>) {
    class Root internal constructor() : BrowserPathBuilder(mutableListOf()) {
        fun literal(value: String) = addLiteral(value)
        fun wildcard() = addWildcard()
        fun separator() = addSeparator()
        fun parameter(param: BrowserParameterPath<*>) = addParameter(param)
    }

    class LastIsLiteral internal constructor(
        pathElements: MutableList<BrowserPathElement>
    ) : BrowserPathBuilder(pathElements) {
        fun literal(value: String) = addLiteral(value)
        fun wildcard() = addWildcard()
        fun separator() = addSeparator()
        fun parameter(param: BrowserParameterPath<*>) = addParameter(param)
    }

    class LastIsWildcard internal constructor(
        pathElements: MutableList<BrowserPathElement>
    ) : BrowserPathBuilder(pathElements)

    class LastIsSeparator internal constructor(
        pathElements: MutableList<BrowserPathElement>
    ) : BrowserPathBuilder(pathElements) {
        fun literal(value: String) = addLiteral(value)
        fun wildcard() = addWildcard()
        fun parameter(param: BrowserParameterPath<*>) = addParameter(param)
    }

    class LastIsParameter internal constructor(
        pathElements: MutableList<BrowserPathElement>
    ) : BrowserPathBuilder(pathElements) {
        fun literal(value: String) = addLiteral(value)
        fun separator() = addSeparator()
    }

    protected fun addLiteral(value: String): LastIsLiteral {
        pathElements.add(createLiteral(value))
        return LastIsLiteral(pathElements)
    }

    protected fun addWildcard(): LastIsWildcard {
        pathElements.add(BrowserPathElement.Wildcard)
        return LastIsWildcard(pathElements)
    }

    protected fun addSeparator(): LastIsSeparator {
        pathElements.add(BrowserPathElement.Separator)
        return LastIsSeparator(pathElements)
    }

    protected fun addParameter(parameterPath: BrowserParameterPath<*>): LastIsParameter {
        pathElements.add(BrowserPathElement.Parameter(parameterPath))
        return LastIsParameter(pathElements)
    }

    fun build(): List<BrowserPathElement> = pathElements

    companion object {
        fun create(): Root = Root()
        fun buildFromPath(path: String?): List<BrowserPathElement> {
            if (path == null) return emptyList()
            var sanitizedPath = path
            var wildcardSuffix = emptyList<BrowserPathElement>()

            if (path.endsWith("*")) {
                sanitizedPath = path.dropLast(1)
                wildcardSuffix = listOf(BrowserPathElement.Wildcard)
            }

            sanitizedPath = HttpUtils.normalizePath(sanitizedPath)
            if (sanitizedPath == "/") return listOf(BrowserPathElement.Separator) + wildcardSuffix

            return buildList {
                val sb = StringBuilder()
                for (c in sanitizedPath) {
                    when (c) {
                        '/' -> {
                            if (sb.isNotEmpty()) {
                                add(createLiteral(sb.toString()))
                                sb.clear()
                            }
                            add(BrowserPathElement.Separator)
                        }
                        else -> sb.append(c)
                    }
                }
                if (sb.isNotEmpty()) {
                    add(createLiteral(sb.toString()))
                }
            } + wildcardSuffix
        }

        private fun createLiteral(value: String): BrowserPathElement.Literal {
            require(value.isNotEmpty()) { "Literal can't be empty" }
            require(!value.contains('/')) { "Literal can't contains `/`" }
            return BrowserPathElement.Literal(value.uriComponentEncoded(null))
        }
    }
}
