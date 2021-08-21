package fr.javatic.yafull.router

data class RouteMatch(
    val routes: List<MountedRoute<*>>,
    private val parametersValue: Map<Route, Map<BrowserParameterPath<*>, Any?>>
)
