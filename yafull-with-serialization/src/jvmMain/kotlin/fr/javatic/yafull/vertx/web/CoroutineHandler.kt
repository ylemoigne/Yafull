package fr.javatic.yafull.vertx.web

fun interface CoroutineHandler<E> {
    /**
     * Something has happened, so handle it.
     *
     * @param event  the event to handle
     */
    suspend fun handle(event: E)
}
