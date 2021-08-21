package fr.javatic.noteapp.note

import fr.javatic.util.UUIDv4
import kotlinx.serialization.Serializable

@Serializable
class NoteUpdate(val id: UUIDv4, val title: String, val content: String)
