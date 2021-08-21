package fr.javatic.util

import kotlinx.serialization.Serializable

@Serializable(with = UUIDv4Serializer::class)
class UUIDv4(val value: String) {
    override fun toString(): String {
        return value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UUIDv4) return false

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }


}
