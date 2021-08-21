package fr.javatic.util

import external.npm.generateUUIDV4

fun UUIDv4.Companion.create(): UUIDv4 = UUIDv4(generateUUIDV4())
