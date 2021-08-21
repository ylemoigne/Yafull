package fr.javatic.yafull.router

import androidx.compose.runtime.Composable
import fr.javatic.util.UUIDv4
import fr.javatic.util.create

class MountedRouteSubrouter<T : Route>(
    route: T,
    internal val router: Router,
    private val mountedRouteId: String = UUIDv4.create().value,
) : MountedRoute<T>(route) {
    override var parent: MountedRoute<*>? = null
        set(value) {
            field = value
            router.setParent(this)
        }

    @Composable
    override fun render(ctx: RoutingContext) {
        router.render(ctx)
    }

    override fun toString(): String {
        return "MountedRouteSubrouter[$mountedRouteId](route=$route)"
    }
}
