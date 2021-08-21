package fr.javatic.yafull.rest.plugin.jwt

import fr.javatic.yafull.rest.SecurityRequirement
import fr.javatic.yafull.rest.SecurityScheme
import fr.javatic.yafull.rest.plugin.ApiPlugin

open class JWTApiPlugin : ApiPlugin {
    protected val baseSecurityRequirement = SecurityRequirement(scheme, emptySet())
    override val defaultSecurityRequirements: Set<SecurityRequirement> = setOf(baseSecurityRequirement)

    companion object {
        val scheme = SecurityScheme.Http.Bearer("jwt", "JWT")
    }
}
