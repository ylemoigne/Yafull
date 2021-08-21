package fr.javatic.util

import fr.javatic.kotlinSdkExtensions.uriComponentEncoded
import fr.javatic.yafull.rest.OpenapiSpecFactory
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.json.*

private val SerialDescriptor.enumValuesName: List<String>
    get() {
        if (kind != SerialKind.ENUM) throw error("enumValuesName must be used on enum")
        return elementNames.map { it.removePrefix(serialName) }
    }

private fun SerialDescriptor.openapiSchemaName(containedInOpenapiSchemaName: String? = null): String {
    require(kind == StructureKind.CLASS || kind == StructureKind.OBJECT || kind == PolymorphicKind.SEALED) {
        "Kind other than CLASS or OBJECT or SEALED should not be used in openapi schema reference"
    }
    val base = serialName
    val suffix = containedInOpenapiSchemaName?.let { "_$it" } ?: ""
    return (base + suffix).uriComponentEncoded(null)
}

internal fun SerialDescriptor.openapiSchema(
    schemasInfoHolder: OpenapiSpecFactory.SchemasInfoHolder,
    containedInOpenapiSchemaName: String? = null
): JsonElement = when (kind) {
    is PrimitiveKind.BOOLEAN -> buildJsonObject {
        put("type", "boolean")
        put("nullable", isNullable)
    }
    is PrimitiveKind.BYTE -> buildJsonObject {
        put("type", "integer")
        put("format", "int1")
        put("nullable", isNullable)
    }
    is PrimitiveKind.CHAR -> buildJsonObject {
        put("type", "string")
        put("maxLength", 1)
        put("nullable", isNullable)
    }
    is PrimitiveKind.SHORT -> buildJsonObject {
        put("type", "integer")
        put("format", "int16")
        put("nullable", isNullable)
    }
    is PrimitiveKind.INT -> buildJsonObject {
        put("type", "integer")
        put("format", "int32")
        put("nullable", isNullable)
    }
    is PrimitiveKind.LONG -> buildJsonObject {
        put("type", "integer")
        put("format", "int64")
        put("nullable", isNullable)
    }
    is PrimitiveKind.FLOAT -> buildJsonObject {
        put("type", "number")
        put("format", "float")
        put("nullable", isNullable)
    }
    is PrimitiveKind.DOUBLE -> buildJsonObject {
        put("type", "number")
        put("format", "double")
        put("nullable", isNullable)
    }
    is PrimitiveKind.STRING -> buildJsonObject {
        put("type", "string")
        when (serialName) {
            LocalDateTime.serializer().descriptor.serialName -> put("format", "date-time")
            LocalDate.serializer().descriptor.serialName -> put("format", "date")
            Instant.serializer().descriptor.serialName -> put("format", "date")
            UUIDv4.serializer().descriptor.serialName -> put("format", "uuid")
        }
        put("nullable", isNullable)
    }
    is SerialKind.ENUM -> buildJsonObject {
        put("type", JsonPrimitive("string"))
        put("enum", buildJsonArray {
            enumValuesName.forEach { add(it) }
        })
        put("nullable", isNullable)
    }
    is SerialKind.CONTEXTUAL -> buildJsonObject {
        fun display(item: SerialDescriptor, depth: Int) {
            val prerix = " ".repeat(depth)
            println("$prerix serialName: ${item.serialName}, nullable: ${item.isNullable}, elementsCount: ${item.elementsCount}")
            for (i in 0 until item.elementsCount) {
                println("$prerix elementName: ${item.getElementName(i)}")
                display(item.getElementDescriptor(i), depth + 2)
            }
        }

        display(this@openapiSchema, 0)
        TODO("Not yet implemented: SerialDescriptorExtension::SerialKind.CONTEXTUAL // ${this@openapiSchema}")
    }
    is StructureKind.LIST -> buildJsonObject {
        put("type", "array")
        put("items", getElementDescriptor(0).openapiSchema(schemasInfoHolder))
        put("nullable", isNullable)
    }
    is StructureKind.CLASS, is StructureKind.OBJECT -> buildJsonObject {
        val schemaName = openapiSchemaName(containedInOpenapiSchemaName)
        put("${'$'}ref", "#/components/schemas/$schemaName")
        put("nullable", isNullable)
        if (!schemasInfoHolder.schemas.containsKey(schemaName)) {
            val typeDesc = buildJsonObject {
                put("type", "object")
                putJsonObject("properties") {
                    for ((idx, descriptor) in elementDescriptors.withIndex()) {
                        put(getElementName(idx), descriptor.openapiSchema(schemasInfoHolder))
                    }
                }
                put("nullable", isNullable)
                putJsonArray("required") {
                    elementDescriptors.asSequence()
                        .withIndex()
                        .filter { (_, desc) -> !desc.isNullable }
                        .map { (idx, _) -> getElementName(idx) }
                        .forEach { add(it) }
                }
            }

            schemasInfoHolder.schemas[schemaName] = if (containedInOpenapiSchemaName == null) {
                typeDesc
            } else {
                buildJsonObject {
                    putJsonArray("allOf") {
                        addJsonObject {
                            put("${'$'}ref", "#/components/schemas/$containedInOpenapiSchemaName")
                        }
                        add(typeDesc)
                    }
                }
            }
        }
    }
    is StructureKind.MAP -> buildJsonObject {
        put("type", "object")

        val valueDesc = getElementDescriptor(1)
        put("additionalProperties", valueDesc.openapiSchema(schemasInfoHolder))
    }
    is PolymorphicKind.SEALED -> buildJsonObject {
        val schemaName = openapiSchemaName(containedInOpenapiSchemaName)
        put("${'$'}ref", "#/components/schemas/$schemaName")
        put("nullable", isNullable)
        if (!schemasInfoHolder.schemas.containsKey(schemaName)) {
            schemasInfoHolder.schemas[schemaName] = buildJsonObject {
                put("type", "object")

                require(elementsCount == 2) { "Internal error: Sealed descriptor should have 2 element, first is discriminator, second is subtype" }
                require(getElementName(0) == "type") { "Internal error: Sealed descriptor first element should be the type" }
                require(getElementName(1) == "value") { "Internal error: Sealed descriptor second element should be the value" }

                val valueDescriptor = getElementDescriptor(1)

                putJsonObject("properties") {
                    putJsonObject(schemasInfoHolder.jsonConfiguration.classDiscriminator) {
                        put("type", "string")
                        putJsonArray("enum") {
                            for (desc in valueDescriptor.elementDescriptors) {
                                add(desc.serialName)
                            }
                        }
                    }
                }
                putJsonArray("required") {
                    add(schemasInfoHolder.jsonConfiguration.classDiscriminator)
                }
                putJsonArray("oneOf") {
                    for (desc in valueDescriptor.elementDescriptors) {
                        addJsonObject {
                            put("${'$'}ref", "#/components/schemas/${desc.openapiSchemaName(schemaName)}")
                        }
                    }
                }
                putJsonObject("discriminator") {
                    put("propertyName", schemasInfoHolder.jsonConfiguration.classDiscriminator)
                    putJsonObject("mapping") {
                        for (desc in valueDescriptor.elementDescriptors) {
                            desc.openapiSchema(schemasInfoHolder, schemaName)
                            put(desc.serialName, desc.openapiSchemaName(schemaName))
                        }
                    }
                }
            }
        }
    }
    is PolymorphicKind.OPEN -> buildJsonObject {
        fun display(item: SerialDescriptor, depth: Int) {
            val prerix = " ".repeat(depth)
            println("$prerix serialName: ${item.serialName}, nullable: ${item.isNullable}, elementsCount: ${item.elementsCount}")
            for (i in 0 until item.elementsCount) {
                println("$prerix elementName: ${item.getElementName(i)}")
                display(item.getElementDescriptor(i), depth + 2)
            }
        }

        display(this@openapiSchema, 0)
        TODO("Not yet implemented SerialDescriptorExtension::StructureKind.OPEN // ${this@openapiSchema}")
    }
}
