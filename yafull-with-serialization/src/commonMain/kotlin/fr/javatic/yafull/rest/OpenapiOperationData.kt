package fr.javatic.yafull.rest

import kotlinx.serialization.json.JsonObject

internal data class OpenapiOperationData(
    val path: String,
    val method: String,
    val tags: Set<String>,
    val securityRequirements: Set<SecurityRequirement?>,
    val operation: JsonObject
)
