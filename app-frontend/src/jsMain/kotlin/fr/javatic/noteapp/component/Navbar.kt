package fr.javatic.noteapp.component

import androidx.compose.runtime.Composable
import fr.javatic.noteapp.AppContext
import fr.javatic.noteapp.AppStyleSheet
import fr.javatic.noteapp.page.MainRoutes
import fr.javatic.yafull.router.BrowserNavigation
import fr.javatic.yafull.router.RoutingContext
import fr.javatic.yafull.uikit.*
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.*

@Composable
fun navbar(appCtx: AppContext, routes: MainRoutes, ctx: RoutingContext) {
    Nav({ ukNavbar().ukNavbarContainer.ukMarginBottom }) {
        Div({ ukNavbarLeft }) {
            A(attrs = { ukNavbarItem.ukLogo }) {
                Img("/yafull-logo.svg", attrs = { classes(AppStyleSheet.css { height(80.px) }) })
            }

            Ul({ ukNavbarNav }) {
                Li({
                    ukMarginLeft
                    if (ctx.isInRoute(routes.myNoteRoute)) ukActive
                }) {
                    A(attrs = {
                        onClick { BrowserNavigation.goTo(routes.myNoteRoute) }
                    }) {
                        Span({ ukIcon("album") })
                        Text("My Notes")
                    }
                }
            }
        }
        Div({ ukNavbarRight }) {
            Ul({ ukNavbarNav }) {
                Li({
                    ukMarginRight
                    if (ctx.isInRoute(routes.userManagementRoute)) ukActive
                }) {
                    A(attrs = { onClick { BrowserNavigation.goTo(routes.userManagementRoute) } }) {
                        Span({ ukIcon("users") })
                        Text("User Management")
                    }
                }
                Li({
                    ukMarginRight
                    if (ctx.isInRoute(routes.userProfileRoute)) ukActive
                }) {
                    A {
                        Span({ ukIcon("user") })
                        Text(appCtx.currentUserFullname ?: "<no name>")
                        Span({ ukIcon("chevron-down") })
                    }
                    Div({ ukNavbarDropDown }) {
                        Ul({ ukNav(UkNavStyle.navbarDropDown()) }) {
                            Li({
                                if (ctx.isInRoute(routes.userProfileRoute)) ukActive
                            }) {
                                A(attrs = { onClick { BrowserNavigation.goTo(routes.userProfileRoute) } }) {
                                    Span({ ukIcon("settings") })
                                    Text("Edit Profile")
                                }
                            }
                            Li {
                                A(attrs = {
                                    onClick {
                                        appCtx.bearer = null
                                        // TODO It's an horrible hack to make logout work from any location, client navigation/routing need an overhaul
                                        BrowserNavigation.goTo("/anyAddressThatCantBeTheCurrentOne")
                                    }
                                }) {
                                    Span({ ukIcon("sign-out") })
                                    Text("Logout")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
