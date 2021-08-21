package fr.javatic.yafull.router

class RoutingContext(
    val normalizedPath: String,
    val routeMatch: RouteMatch,
) {
    internal var continueSearch: Boolean = false

    fun next() {
        continueSearch = true
    }

    fun requestMatchRoute(route: MountedRoute<RouteStaticPath>): Boolean =
        route.matches(normalizedPath) != null//routeMatch.routes.contains(route)

    fun isInRoute(route: MountedRoute<RouteStaticPath>): Boolean =
        routeMatch.routes.contains(route)
}
