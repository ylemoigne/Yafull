package fr.javatic.yafull.rest.plugin.jwt

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import external.npm.jwtDecode
import fr.javatic.yafull.compose.observableMutableStateOf
import fr.javatic.yafull.rest.ClientApiPlugin
import fr.javatic.yafull.rest.Endpoint
import fr.javatic.yafull.rest.SecurityScheme
import kotlinx.browser.window

class JWTClientApiPlugin : JWTApiPlugin(), ClientApiPlugin {
    var bearer by observableMutableStateOf(window.localStorage.getItem("auth_bearer")) {
        if (it == null) {
            window.localStorage.removeItem("auth_bearer")
            token = null
        } else {
            window.localStorage.setItem("auth_bearer", it)
            token = JWTToken(jwtDecode(it))
        }
    }

    var token: JWTToken? = bearer?.let { JWTToken(jwtDecode(it)) }

    override suspend fun transformRequestHeaders(
        endpoint: Endpoint,
        headers: List<Pair<String, String>>
    ): List<Pair<String, String>> {
        if (!endpoint.securityRequirements.contains(null) &&
            (endpoint.securityRequirements + defaultSecurityRequirements).any { it?.scheme is SecurityScheme.Http.Bearer }
        ) {
            return headers + ("Authorization" to "Bearer $bearer")
        }
        return headers
    }
}
