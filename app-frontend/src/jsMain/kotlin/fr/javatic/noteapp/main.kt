package fr.javatic.noteapp

import external.webpackRequire
import fr.javatic.yafull.rest.RestClient
import fr.javatic.yafull.rest.plugin.jwt.JWTClientApiPlugin
import fr.javatic.yafull.router.BrowserNavigation
import fr.javatic.yafull.router.RootRoute
import fr.javatic.yafull.uikit.UIKitStylesheet
import fr.javatic.yafull.uikit.UIkit
import fr.javatic.yafull.uikit.requireUIKitCss
import fr.javatic.yafull.uikit.requireUIKitIcons
import kotlinx.coroutines.coroutineScope
import mu.KotlinLoggingConfiguration
import mu.KotlinLoggingLevel
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.renderComposable

suspend fun main() = coroutineScope {
    requireUIKitCss()
    UIkit.use(requireUIKitIcons())
    webpackRequire(".//base.css")

    KotlinLoggingConfiguration.LOG_LEVEL = KotlinLoggingLevel.TRACE

    val jwt = JWTClientApiPlugin()
    val restClient = RestClient("/api", listOf(jwt))
    val appContext = AppContext(restClient, jwt)

    val appRoutes = AppRoutes(appContext)
    val router = RootRoute(appRoutes.router)

    renderComposable(rootElementId = "root") {
        Style(AppStyleSheet)
        Style(UIKitStylesheet)
        router.render(BrowserNavigation.relativeReference)
    }
    Unit
}
