package fr.javatic.util

import org.slf4j.Logger

fun Logger.debug(block: () -> String) {
    if (this.isDebugEnabled) debug(block())
}

fun Logger.trace(block: () -> String) {
    if (this.isTraceEnabled) debug(block())
}

fun Logger.error(block: () -> String) {
    if (this.isErrorEnabled) error(block())
}

fun Logger.info(block: () -> String) {
    if (this.isInfoEnabled) info(block())
}

fun Logger.warn(block: () -> String) {
    if (this.isWarnEnabled) warn(block())
}
