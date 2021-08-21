package fr.javatic.yafull.vertx

import io.vertx.core.MultiMap

fun MultiMap.toMap(): Map<String, List<String>> = buildMap {
    for (k in this@toMap.names()) {
        put(k, this@toMap.getAll(k))
    }
}
