package fr.javatic.yafull.router

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.browser.document
import kotlinx.browser.window

object BrowserNavigation {
    var relativeReference by mutableStateOf<String>(with(window.location) { pathname + search + hash })
        private set

    init {
        window.onpopstate = {
            relativeReference = with(window.location) { pathname + search + hash }
            Unit
        }
    }

    fun reroute(relativeReference: String) {
        window.history.replaceState(null, document.title, relativeReference)
        BrowserNavigation.relativeReference = relativeReference
    }

    fun <T : Route> reroute(mountedRoute: MountedRoute<T>, block: RouteConfigurator.() -> Unit = {}) {
        reroute(computeRealPath(mountedRoute, block))
    }

    fun goTo(relativeReference: String) {
        window.history.pushState(null, document.title, relativeReference)
        BrowserNavigation.relativeReference = relativeReference
    }

    fun goTo(mountedRoute: MountedRoute<*>, block: RouteConfigurator.() -> Unit = {}) {
        val rp = computeRealPath(mountedRoute, block)
        println("realPath: $rp")
        goTo(rp)
    }

    private fun computeRealPath(mountedRoute: MountedRoute<*>, block: RouteConfigurator.() -> Unit): String {
        val browserParameterPathValueByMountedRoute: MutableMap<MountedRoute<*>, MutableMap<BrowserParameterPath<*>, Any?>> =
            mutableMapOf()
        val configurator = RouteConfigurator(browserParameterPathValueByMountedRoute)
        configurator.block()

        val sb = StringBuilder()
        for (mountedRouteWithPathElement in mountedRoute.pathElementsToHereWithMountedRoute) {
            when (val el = mountedRouteWithPathElement.pathElement) {
                is BrowserPathElement.Separator -> sb.append('/')
                is BrowserPathElement.Literal -> sb.append(el.value)
                is BrowserPathElement.Parameter -> {
                    val browserParameterPathValue =
                        browserParameterPathValueByMountedRoute[mountedRouteWithPathElement.mountedRoute] ?: emptyMap()
                    sb.append(el.parameter.encode(browserParameterPathValue[el.parameter]))
                }
                is BrowserPathElement.Wildcard -> Unit
            }
        }

        return sb.toString()
    }

    class RouteConfigurator(private val browserParameterPathValueByMountedRoute: MutableMap<MountedRoute<*>, MutableMap<BrowserParameterPath<*>, Any?>>) {
        /**
         * Not thread safe approach. Ok, we are in the browser, but other option are too much verbose for me
         * hope to see this solved by https://github.com/Kotlin/KEEP/issues/259
         */
        private var currentMountedRoute: MountedRoute<*>? = null

        fun <V> BrowserParameterPath<V>.set(value: V) {
            val currentRoute = currentMountedRoute
            requireNotNull(currentRoute) { "Use the configure method." }
            val browserParameterPathValue =
                browserParameterPathValueByMountedRoute.getOrPut(currentRoute) { mutableMapOf() }
            browserParameterPathValue[this] = value
        }

        fun <T : Route> configure(mountedRoute: MountedRoute<T>, block: T.() -> Unit) {
            currentMountedRoute = mountedRoute
            mountedRoute.route.block()
            currentMountedRoute = null
        }
    }
}
