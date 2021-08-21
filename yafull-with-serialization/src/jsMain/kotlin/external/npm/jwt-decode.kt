package external.npm

import external.webpackRequire
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromDynamic

private val jwtDecodeModule = webpackRequire("jwt-decode")

fun jwtDecode(token: String): JsonObject {
    return Json.decodeFromDynamic(jwtDecodeModule.default(token))
}
