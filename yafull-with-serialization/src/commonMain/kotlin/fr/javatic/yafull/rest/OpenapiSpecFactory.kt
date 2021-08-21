package fr.javatic.yafull.rest

import kotlinx.serialization.json.*

internal object OpenapiSpecFactory {
    data class SchemasInfoHolder(
        val jsonConfiguration: JsonConfiguration,
        val schemas: MutableMap<String, JsonObject> = mutableMapOf(),
    )

    fun createOpenapiSpec(
        jsonConfiguration: JsonConfiguration,
        defaultProtocol: String,
        bindAddress: String = "localhost",
        port: Int,
        mountPoint: String,
        title: String,
        description: String? = null,
        version: String,
        defaultSecurityRequirements: Set<SecurityRequirement>,
        endpoints: List<Endpoint>
    ): JsonObject {
        require(mountPoint.startsWith('/')) { "Mount point must start with '/'" }
        require(!mountPoint.endsWith('/')) { "Mount point must not end with '/'" }

        val schemasInfoHolder = SchemasInfoHolder(jsonConfiguration)

        val operationDatas = endpoints.map { it.createOpenapiOperationData(schemasInfoHolder) }
        val groupedEndpoints = operationDatas.groupBy { it.path }
        val tags = operationDatas.flatMap { it.tags }.toSet()
        val securitySchemes = (
                defaultSecurityRequirements.map { it.scheme }
                        +
                        operationDatas.asSequence()
                            .flatMap { it.securityRequirements }
                            .filterNotNull()
                            .map { it.scheme }
                ).toSet()


        return buildJsonObject {
            put("openapi", "3.0.2")
            putJsonObject("info") {
                put("title", title)
                description?.let { put("description", it) }
                put("version", version)
            }
            putJsonArray("servers") {
                add(buildJsonObject {
                    put("url", "{protocol}://{host}:{port}$mountPoint")
                    putJsonObject("variables") {
                        putJsonObject("protocol") {
                            put("default", defaultProtocol.lowercase())
                            putJsonArray("enum") {
                                add("http")
                                add("https")
                                if (defaultProtocol.lowercase() != "http" && defaultProtocol.lowercase() != "https") {
                                    add(defaultProtocol.lowercase())
                                }
                            }
                        }
                        putJsonObject("host") {
                            put("default", bindAddress)
                        }
                        putJsonObject("port") {
                            put("default", port.toString())
                        }
                    }
                })
            }
            putJsonArray("tags") {
                for (tag in tags) {
                    add(buildJsonObject {
                        put("name", tag)
                    })
                }
            }
            putJsonObject("paths") {
                for ((path, pathEndpoints) in groupedEndpoints.entries) {
                    putJsonObject(path) {
                        for (endpoint in pathEndpoints) {
                            put(endpoint.method, endpoint.operation)
                        }
                    }
                }
            }
            if (securitySchemes.isNotEmpty()) {
                putJsonObject("components") {
                    putJsonObject("securitySchemes") {
                        for (securityScheme in securitySchemes) {
                            put(securityScheme.name, securityScheme.createOpenapiSecurityScheme())
                        }
                    }
                    putJsonObject("schemas") {
                        for ((k, v) in schemasInfoHolder.schemas) {
                            put(k, v)
                        }
                    }
                }
            }
            if (defaultSecurityRequirements.isNotEmpty()) {
                putJsonArray("security") {
                    for (defaultSecurityRequirement in defaultSecurityRequirements) {
                        add(buildJsonObject {
                            put(defaultSecurityRequirement.scheme.name, buildJsonArray {
                                if (defaultSecurityRequirement.scopes.isNotEmpty()) {
                                    defaultSecurityRequirement.scopes.forEach { add(it) }
                                }
                            })
                        })
                    }
                }
            }
        }
    }
}
