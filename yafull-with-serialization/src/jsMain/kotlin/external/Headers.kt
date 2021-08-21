package external

open external class Headers(init: dynamic = definedExternally) {
    fun append(name: String, value: String)
    fun delete(name: String)
    fun get(name: String): String?
    fun has(name: String): Boolean
    fun set(name: String, value: String)
    fun entries(): JsIterator<dynamic>
}
