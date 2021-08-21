package fr.javatic.yafull.router

import fr.javatic.yafull.rest.ParameterFormat
import kotlin.reflect.KType
import kotlin.reflect.typeOf

abstract class Route {
    private val parametersPath = mutableMapOf<String, BrowserParameterPath<*>>()
    private val parametersQuery = mutableMapOf<String, BrowserParameterQuery<*>>()

    abstract val pathElements: List<BrowserPathElement>

    protected inline fun <reified T> path(
        name: String,
        format: ParameterFormat.Path = ParameterFormat.Path.Simple
    ) = registerPathParameter<T>(typeOf<T>(), name, format)

    protected fun <T> registerPathParameter(
        typeOf: KType,
        name: String,
        format: ParameterFormat.Path
    ): BrowserParameterPath<T> {
        val parameterPath: BrowserParameterPath<T> = BrowserParameterPath(typeOf, name, format)
        if (parametersPath.put(
                name,
                parameterPath
            ) != null
        ) throw Error("Path parameter with name `$name` is already registered")
        return parameterPath
    }

    protected inline fun <reified T> query(
        name: String,
        format: ParameterFormat.Query = ParameterFormat.Query.Form()
    ) =
        registerQueryParameter<T>(typeOf<T>(), name, format)

    protected fun <T> registerQueryParameter(
        typeOf: KType,
        name: String,
        format: ParameterFormat.Query
    ): BrowserParameterQuery<T> {
        val parameterQuery: BrowserParameterQuery<T> = BrowserParameterQuery(typeOf, name, format)
        if (parametersQuery.put(
                name,
                parameterQuery
            ) != null
        ) throw Error("Path parameter with name `$name` is already registered")
        return parameterQuery
    }

    override fun toString(): String {
        return "Route(pathElements=$pathElements)"
    }
}
