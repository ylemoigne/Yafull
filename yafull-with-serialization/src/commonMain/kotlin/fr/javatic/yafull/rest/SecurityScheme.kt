package fr.javatic.yafull.rest

import kotlinx.serialization.json.*

abstract class SecurityScheme(val name: String, val type: String) {
    internal abstract fun createOpenapiSecurityScheme(): JsonObject


    //    class ApiKey(val name:String, val paramContainer: Container, val paramName:String, val description:String?=null):SecurityScheme("apiKey"){
//        enum class Container {
//            QUERY,
//            HEADER,
//            COOKIE
//        }
//    }
    abstract class Http(name: String, private val scheme: String) : SecurityScheme(name, "http") {
        internal open val openapiAdditionalProperties: Map<String, JsonElement> = emptyMap()
        override fun createOpenapiSecurityScheme() = buildJsonObject {
            put("type", type)
            put("scheme", scheme)
            for ((prop, value) in openapiAdditionalProperties) {
                put(prop, value)
            }
        }

        //        class Basic(name: String) : Http(name, "basic")
//        class Digest(name: String) : Http(name, "digest")
        class Bearer(name: String, val format: String) : Http(name, "bearer") {
            override val openapiAdditionalProperties: Map<String, JsonElement> =
                mapOf("bearerFormat" to JsonPrimitive(format))

            override fun toString(): String = "Bearer($format)"
        }
    }
//    class OAuth2():SecurityScheme("oauth2")
//    class OpenIdConnect():SecurityScheme("openIdConnect")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SecurityScheme) return false

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}
