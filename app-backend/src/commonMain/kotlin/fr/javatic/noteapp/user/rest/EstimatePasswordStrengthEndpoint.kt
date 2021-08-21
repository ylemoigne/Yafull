package fr.javatic.noteapp.user.rest

import fr.javatic.noteapp.RestJson
import fr.javatic.yafull.rest.Endpoint
import fr.javatic.yafull.rest.HttpMethod
import fr.javatic.yafull.rest.RestPathBuilder
import fr.javatic.yafull.utils.password.PasswordStrengthEstimation

object EstimatePasswordStrengthEndpoint : Endpoint(RestJson, tags) {
    override val method: HttpMethod = HttpMethod.GET
    override fun declarePath(builder: RestPathBuilder.Root): RestPathBuilder =
        builder.segment("users").literal("validate-password-strength")

    val password = requestHeader<String>("password")

    val responsePasswordStrengthEstimation = responseOk().withJsonBody<PasswordStrengthEstimation>()
}
