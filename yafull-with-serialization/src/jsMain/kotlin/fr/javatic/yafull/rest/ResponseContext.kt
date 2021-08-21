package fr.javatic.yafull.rest

import external.Headers

class ResponseContext(val status: Int, private val headers: Headers, val content: suspend () -> ByteArray) {
    val contentType get() = headers.get("content-type")
}
