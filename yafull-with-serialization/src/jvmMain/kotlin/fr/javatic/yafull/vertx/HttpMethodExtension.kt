package fr.javatic.yafull.vertx

import fr.javatic.yafull.rest.HttpMethod
import io.vertx.core.http.HttpMethod as VertxHttpMethod

internal val HttpMethod.asVertx: VertxHttpMethod
    get() = when (this) {
        HttpMethod.OPTIONS -> VertxHttpMethod.OPTIONS
        HttpMethod.GET -> VertxHttpMethod.GET
        HttpMethod.HEAD -> VertxHttpMethod.HEAD
        HttpMethod.POST -> VertxHttpMethod.POST
        HttpMethod.PUT -> VertxHttpMethod.PUT
        HttpMethod.DELETE -> VertxHttpMethod.DELETE
        HttpMethod.TRACE -> VertxHttpMethod.TRACE
        HttpMethod.CONNECT -> VertxHttpMethod.CONNECT
        HttpMethod.PATCH -> VertxHttpMethod.PATCH
        HttpMethod.PROPFIND -> VertxHttpMethod.PROPFIND
        HttpMethod.PROPPATCH -> VertxHttpMethod.PROPPATCH
        HttpMethod.MKCOL -> VertxHttpMethod.MKCOL
        HttpMethod.COPY -> VertxHttpMethod.COPY
        HttpMethod.MOVE -> VertxHttpMethod.MOVE
        HttpMethod.LOCK -> VertxHttpMethod.LOCK
        HttpMethod.UNLOCK -> VertxHttpMethod.UNLOCK
        HttpMethod.MKCALENDAR -> VertxHttpMethod.MKCALENDAR
        HttpMethod.VERSION_CONTROL -> VertxHttpMethod.VERSION_CONTROL
        HttpMethod.REPORT -> VertxHttpMethod.REPORT
        HttpMethod.CHECKIN -> VertxHttpMethod.CHECKIN
        HttpMethod.CHECKOUT -> VertxHttpMethod.CHECKOUT
        HttpMethod.UNCHECKOUT -> VertxHttpMethod.UNCHECKOUT
        HttpMethod.MKWORKSPACE -> VertxHttpMethod.MKWORKSPACE
        HttpMethod.UPDATE -> VertxHttpMethod.UPDATE
        HttpMethod.LABEL -> VertxHttpMethod.LABEL
        HttpMethod.MERGE -> VertxHttpMethod.MERGE
        HttpMethod.BASELINE_CONTROL -> VertxHttpMethod.BASELINE_CONTROL
        HttpMethod.MKACTIVITY -> VertxHttpMethod.MKACTIVITY
        HttpMethod.ORDERPATCH -> VertxHttpMethod.ORDERPATCH
        HttpMethod.ACL -> VertxHttpMethod.ACL
        HttpMethod.SEARCH -> VertxHttpMethod.SEARCH
    }
