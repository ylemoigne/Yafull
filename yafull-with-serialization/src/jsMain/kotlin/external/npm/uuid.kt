package external.npm

import external.webpackRequire

private val uuid = webpackRequire("uuid")

fun generateUUIDV4(): String = uuid.v4().unsafeCast<String>()
