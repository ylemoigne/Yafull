package fr.javatic.noteapp.page

import androidx.compose.runtime.Composable
import fr.javatic.noteapp.AppContext
import fr.javatic.noteapp.page.content.NotesContent
import fr.javatic.noteapp.page.content.ProfileContent
import fr.javatic.noteapp.page.content.UserManagementContent
import fr.javatic.yafull.router.Renderer
import fr.javatic.yafull.router.Router
import fr.javatic.yafull.router.RoutingContext

class MainRoutes(private val appCtx: AppContext) {
    val router = Router()
    val myNoteRoute = router.register("/", object : Renderer {
        @Composable
        override fun render(ctx: RoutingContext) {
            mainPage(appCtx, this@MainRoutes, ctx) { NotesContent.render(appCtx) }
        }
    })

    val userManagementRoute = router.register("/users", object : Renderer {
        @Composable
        override fun render(ctx: RoutingContext) {
            mainPage(appCtx, this@MainRoutes, ctx) { UserManagementContent.render(appCtx) }
        }
    })

    val userProfileRoute = router.register("/profile", object : Renderer {
        @Composable
        override fun render(ctx: RoutingContext) {
            mainPage(appCtx, this@MainRoutes, ctx) { ProfileContent.render(appCtx) }
        }
    })
}
