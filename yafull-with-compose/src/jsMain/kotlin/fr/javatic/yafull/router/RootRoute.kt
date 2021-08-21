package fr.javatic.yafull.router

import androidx.compose.runtime.Composable

class RootRoute(private val router: Router) : MountedRoute<RouteStaticPath>(RouteStaticPath(null)) {
    override var parent: MountedRoute<*>? = null

    init {
        router.setParent(this)
    }

    @Composable
    override fun render(ctx: RoutingContext) {
        router.render(ctx)
    }

    @Composable
    fun render(path: String) {
        val normalizedPath = HttpUtils.normalizePath(path)
        val ctx = RoutingContext(normalizedPath, RouteMatch(listOf(this), emptyMap()))
        render(ctx)
    }

    override fun toString(): String {
        return "RootRoute"
    }
}
