package fr.javatic.yafull.router

import androidx.compose.runtime.Composable

class Router() {
    private val mountedRoutes = mutableListOf<MountedRoute<*>>()
    private var defaultRenderer: (Renderer)? = null

    internal fun setParent(parent: MountedRoute<*>) {
        mountedRoutes.forEach { it.parent = parent }
    }

    fun registerDefault(renderer: Renderer) {
        defaultRenderer = renderer
    }

    fun register(route: String, renderer: Renderer): MountedRoute<RouteStaticPath> {
        val mountedRoute = MountedRouteSimple(RouteStaticPath(route), renderer)
        mountedRoutes.add(mountedRoute)
        return mountedRoute
    }


    fun <T : Route> register(route: T, renderer: Renderer): MountedRoute<T> {
        val mountedRoute = MountedRouteSimple<T>(route, renderer)
        mountedRoutes.add(mountedRoute)
        return mountedRoute
    }

    fun <T : Route> register(route: T, router: Router): MountedRoute<T> {
        val mountedRoute = MountedRouteSubrouter(route, router)
        mountedRoutes.add(mountedRoute)
        return mountedRoute
    }

    @Composable
    fun render(ctx: RoutingContext) {
        for (route in mountedRoutes) {
            val routeMatch = route.matches(ctx.normalizedPath)
            if (routeMatch != null) {
                val childCtx = RoutingContext(ctx.normalizedPath, routeMatch)
                route.render(childCtx)
                if (!childCtx.continueSearch) return
            }
        }

        defaultRenderer?.render(RoutingContext(ctx.normalizedPath, ctx.routeMatch))
    }
}
