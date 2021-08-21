package fr.javatic.util

import fr.javatic.yafull.rest.OpenapiSpecFactory
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.serializerOrNull
import kotlin.reflect.KType

internal fun KType.serializableOpenapiSchema(schemasInfoHolder: OpenapiSpecFactory.SchemasInfoHolder): JsonElement {
    val serializer = serializerOrNull(this) ?: throw Error("KType `$this` is not serializable")
    return serializer.descriptor.openapiSchema(schemasInfoHolder)
}
