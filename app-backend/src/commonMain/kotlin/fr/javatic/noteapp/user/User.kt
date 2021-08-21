package fr.javatic.noteapp.user

import fr.javatic.util.UUIDv4
import kotlinx.serialization.Serializable

@Serializable
data class User(val id: UUIDv4, val login: String, val fullname: String, val roles: Set<String>)
