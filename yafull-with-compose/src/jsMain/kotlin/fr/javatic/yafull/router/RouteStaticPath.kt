package fr.javatic.yafull.router

class RouteStaticPath(path: String?) : Route() {
    override val pathElements: List<BrowserPathElement> = BrowserPathBuilder.buildFromPath(path)
}
