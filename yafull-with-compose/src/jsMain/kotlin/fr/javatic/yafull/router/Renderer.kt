package fr.javatic.yafull.router

import androidx.compose.runtime.Composable

// Sadly : https://github.com/JetBrains/compose-jb/issues/1052
// typealias Renderer = @Composable (RoutingContext)->Any?

interface Renderer {
    @Composable
    fun render(ctx: RoutingContext)
}
//
//class Renderer {
//    @Composable
//    fun render(ctx:RoutingContext) = r(ctx)
//}
