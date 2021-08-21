package fr.javatic.util

import java.util.*

fun UUIDv4.Companion.create(): UUIDv4 = UUIDv4(UUID.randomUUID().toString())

fun UUIDv4.Companion.fromJavaUUID(uuid: UUID) = UUIDv4(uuid.toString())
fun UUIDv4.toJavaUUID(): UUID = UUID.fromString(value)
