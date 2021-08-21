package fr.javatic.noteapp.note

import fr.javatic.util.UUIDv4
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
class Note(val id: UUIDv4, val author: String, val creationDate: LocalDateTime, val title: String, val content: String)
