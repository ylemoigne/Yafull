package fr.javatic.yafull.rest

data class SecurityRequirement(val scheme: SecurityScheme, val scopes: Set<String>)
