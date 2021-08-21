package fr.javatic.jooq

import fr.javatic.util.UUIDv4
import org.jooq.Converter
import java.util.*

class UUIDv4Converter : Converter<UUID, UUIDv4> {
    override fun from(databaseObject: UUID?): UUIDv4? = databaseObject?.let { UUIDv4(it.toString()) }
    override fun to(userObject: UUIDv4?): UUID? = userObject?.let { UUID.fromString(it.value) }

    override fun fromType(): Class<UUID> = UUID::class.java
    override fun toType(): Class<UUIDv4> = UUIDv4::class.java
}
