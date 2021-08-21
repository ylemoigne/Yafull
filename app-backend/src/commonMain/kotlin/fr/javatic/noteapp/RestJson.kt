package fr.javatic.noteapp

import kotlinx.serialization.json.Json

val RestJson = Json { classDiscriminator = "@type" }
