package fr.javatic.noteapp.user.rest

import fr.javatic.yafull.utils.password.PasswordStrengthEstimator
import fr.javatic.yafull.vertx.invoke
import fr.javatic.yafull.vertx.web.EndpointHandler
import io.vertx.ext.web.RoutingContext

class EstimatePasswordStrengthHandler(
    private val passwordStrengthEstimator: PasswordStrengthEstimator
) : EndpointHandler<EstimatePasswordStrengthEndpoint> {
    override suspend fun EstimatePasswordStrengthEndpoint.handle(ctx: RoutingContext) {
        val password = this.password(ctx)

        this.responsePasswordStrengthEstimation(ctx) { passwordStrengthEstimator.estimate(password) }
    }
}
