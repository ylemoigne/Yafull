package fr.javatic.noteapp.page

import androidx.compose.runtime.Composable
import fr.javatic.noteapp.AppContext
import fr.javatic.noteapp.component.navbar
import fr.javatic.yafull.router.RoutingContext

@Composable
fun mainPage(
    appCtx: AppContext,
    routes: MainRoutes,
    ctx: RoutingContext,
    mainComponent: @Composable () -> Unit
) {
    navbar(appCtx, routes, ctx)

    mainComponent()
}
