package fr.javatic.yafull.rest.plugin

import fr.javatic.yafull.rest.SecurityRequirement

interface ApiPlugin {
    val defaultSecurityRequirements: Set<SecurityRequirement>
}
