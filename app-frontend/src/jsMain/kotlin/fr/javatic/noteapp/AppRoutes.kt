package fr.javatic.noteapp

import androidx.compose.runtime.Composable
import fr.javatic.noteapp.page.LoginPage
import fr.javatic.noteapp.page.MainRoutes
import fr.javatic.yafull.router.*
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Text

class AppRoutes(appContext: AppContext) {
    val router = Router()

    val notFoundRoute = router.registerDefault(object : Renderer {
        @Composable
        override fun render(ctx: RoutingContext) {
            H1 { Text("Route not found") }
        }
    })
    val redirectLoginRoute = router.register(RouteStaticPath("/*"), object : Renderer {
        @Composable
        override fun render(ctx: RoutingContext) {
            println("Render redirectRouteLoginRoute")
            if (appContext.currentUserId == null && !ctx.requestMatchRoute(loginRoute)) BrowserNavigation.reroute(
                loginRoute
            )
            else ctx.next()
        }
    })
    val loginRoute = router.register(RouteStaticPath("/login"), object : Renderer {
        @Composable
        override fun render(ctx: RoutingContext) {
            LoginPage.render(appContext)
        }
    })

    val mainRoute = router.register(RouteStaticPath("/*"), MainRoutes(appContext).router)
}
