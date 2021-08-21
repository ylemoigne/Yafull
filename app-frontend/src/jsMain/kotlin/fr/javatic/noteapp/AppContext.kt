package fr.javatic.noteapp

import fr.javatic.util.UUIDv4
import fr.javatic.yafull.rest.RestClient
import fr.javatic.yafull.rest.plugin.jwt.JWTClientApiPlugin

class AppContext(val restClient: RestClient, private val jwtInfos: JWTClientApiPlugin) {
    val api = AppClientApi(restClient)

    val currentUserId: UUIDv4? get() = jwtInfos.token?.sub?.let { UUIDv4(it) }
    val currentUserFullname: String? get() = jwtInfos.token?.name
    val currentUserRoles: Set<String>? get() = jwtInfos.token?.scope
    var bearer: String?
        get() = jwtInfos.bearer
        set(value) {
            jwtInfos.bearer = value
        }
}
