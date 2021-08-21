package fr.javatic.yafull.router

import androidx.compose.runtime.Composable
import fr.javatic.util.UUIDv4
import fr.javatic.util.create

class MountedRouteSimple<T : Route>(
    route: T,
    private val renderer: Renderer,
    private val mountedRouteId: String = UUIDv4.create().value,
) : MountedRoute<T>(route) {
    override var parent: MountedRoute<*>? = null
        set(value) {
            if (value == this) throw Error("no no no")
            field = value
        }

    @Composable
    override fun render(ctx: RoutingContext) {
        this.renderer.render(ctx)
    }

    override fun toString(): String {
        return "MountedRouteSimple[$mountedRouteId](route=$route)"
    }
}
