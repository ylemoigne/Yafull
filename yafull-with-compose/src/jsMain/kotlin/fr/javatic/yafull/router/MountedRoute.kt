package fr.javatic.yafull.router

import androidx.compose.runtime.Composable

abstract class MountedRoute<T : Route>(internal val route: T) {
    internal abstract var parent: MountedRoute<*>?

    private val mountedRouteToHere: List<MountedRoute<*>> by lazy {
        (parent?.mountedRouteToHere ?: emptyList()) + this@MountedRoute
    }

    internal data class MountedRouteAndPathElement(val mountedRoute: MountedRoute<*>, val pathElement: BrowserPathElement)

    internal val pathElementsToHereWithMountedRoute: List<MountedRouteAndPathElement> by lazy {
        buildList {
            (parent?.mountedRouteToHere ?: emptyList()).forEach { mountedRoute ->
                // TODO Yes it seems ad-hoc patch. It's because it is.
                val retained = if (mountedRoute.route.pathElements.takeLast(2) == badEnding) {
                    mountedRoute.route.pathElements.dropLast(2)
                } else if (route.pathElements.last() == BrowserPathElement.Wildcard) {
                    mountedRoute.route.pathElements.dropLast(1)
                } else {
                    mountedRoute.route.pathElements
                }

                retained.forEach { add(MountedRouteAndPathElement(mountedRoute, it)) }
            }

            route.pathElements.forEach { add(MountedRouteAndPathElement(this@MountedRoute, it)) }
        }
    }

    internal fun matches(normalizedPath: String): RouteMatch? {
        data class ParameterPathHandler(
            val route: Route,
            val parameter: BrowserParameterPath<*>,
            var stopAtChar: Char?,
            val value: StringBuilder
        )

        val parametersValueByRoute: MutableMap<Route, MutableMap<BrowserParameterPath<*>, Any?>> = mutableMapOf()

        val patterns = pathElementsToHereWithMountedRoute

        var patternIdx = 0
        var inLiteralIdx = 0
        var currentParameterPathHandler: ParameterPathHandler? = null
        for (c in normalizedPath) {
            val precheckPattern = patterns.getOrNull(patternIdx)
            val pathElement = precheckPattern?.pathElement

            // Close parameter context is c is closing char
            val parameterPathHandler = currentParameterPathHandler
            if (pathElement is BrowserPathElement.Parameter && parameterPathHandler != null && c == parameterPathHandler.stopAtChar) {
                val parametersValue =
                    parametersValueByRoute.getOrPut(precheckPattern.mountedRoute.route) { mutableMapOf() }
                parametersValue[parameterPathHandler.parameter] =
                    parameterPathHandler.parameter.decode(parameterPathHandler.value.toString())

                currentParameterPathHandler = null
                patternIdx++
            }

            // Return if normalized path still has content, but no more route pattern available
            val pattern = patterns.getOrNull(patternIdx) ?: return null

            when (val el = pattern.pathElement) {
                is BrowserPathElement.Separator -> {
                    if (c != '/') return null
                    patternIdx++
                }
                is BrowserPathElement.Literal -> {
                    if (c != el.value[inLiteralIdx]) return null
                    if (inLiteralIdx == el.value.lastIndex) {
                        // Got the last char of litteral, time to close literal context
                        inLiteralIdx = 0
                        patternIdx++
                    } else {
                        // Still got literal to consume
                        inLiteralIdx++
                    }
                }
                is BrowserPathElement.Parameter -> {
                    if (currentParameterPathHandler == null) {
                        val nextPattern = patterns.getOrNull(patternIdx + 1)
                        val stopAtChar = when (val nextEl = nextPattern?.pathElement) {
                            null -> null
                            is BrowserPathElement.Separator -> '/'
                            is BrowserPathElement.Literal -> nextEl.value.getOrNull(0)
                                ?: throw Error("A Literal path element must not be empty (Guaranteed by BrowserPathBuilder)")
                            is BrowserPathElement.Wildcard -> throw Error("A Parameter path element can't be followed by the Wildcard path element (Guaranteed by BrowserPathBuilder)")
                            is BrowserPathElement.Parameter -> throw Error("A Parameter path element can't be followed by another Parameter path element (Guaranteed by BrowserPathBuilder)")
                        }
                        currentParameterPathHandler =
                            ParameterPathHandler(
                                pattern.mountedRoute.route,
                                el.parameter,
                                stopAtChar,
                                StringBuilder().append(c)
                            )
                    } else {
                        currentParameterPathHandler.value.append(c)
                    }
                }
                is BrowserPathElement.Wildcard -> return RouteMatch(mountedRouteToHere, parametersValueByRoute)
            }
        }

        val finalPattern = patterns.getOrNull(patternIdx)
        val validEnd = when (finalPattern?.pathElement) {
            // Whatever we had, the pathElement was complete and we are now out of patterns
            null -> true
            // A wildcard may be empty, and it's garanteed to be last element, so it's ok
            is BrowserPathElement.Wildcard -> true
            // the patternIdx was advanced by a closing context, so now we expected a separator but did not get it
            is BrowserPathElement.Separator -> false
            // the patternIdx was advanced by a closing context, so now we expected a separator but did not get it
            is BrowserPathElement.Literal -> false
            // if the parameter element was the last element, we do not have occasion to close his context, stop it's ok
            // if not, it means the patternIdx was advanced by a closing context, and we expected a parameter but did not get it
            is BrowserPathElement.Parameter -> {
                val parameterPathHandler = currentParameterPathHandler
                if (parameterPathHandler != null && parameterPathHandler.stopAtChar == null) {
                    // Valid end, we finish/close the context
                    val parametersValue =
                        parametersValueByRoute.getOrPut(finalPattern.mountedRoute.route) { mutableMapOf() }
                    parametersValue[parameterPathHandler.parameter] =
                        parameterPathHandler.parameter.decode(parameterPathHandler.value.toString())
                    true
                } else {
                    false
                }
            }
        }

        return if (validEnd) RouteMatch(mountedRouteToHere, parametersValueByRoute) else null
    }

    @Composable
    abstract fun render(ctx: RoutingContext)

    companion object {
        private val badEnding = listOf(BrowserPathElement.Separator, BrowserPathElement.Wildcard)
    }
}
