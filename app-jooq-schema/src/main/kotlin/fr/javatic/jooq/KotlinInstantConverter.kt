package fr.javatic.jooq

import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import org.jooq.Converter
import kotlinx.datetime.Instant as KotlinInstant
import java.time.Instant as JavaInstant

class KotlinInstantConverter : Converter<JavaInstant, KotlinInstant> {
    override fun from(databaseObject: JavaInstant?): KotlinInstant? = databaseObject?.toKotlinInstant()
    override fun to(userObject: KotlinInstant?): JavaInstant? = userObject?.toJavaInstant()

    override fun fromType(): Class<JavaInstant> = JavaInstant::class.java
    override fun toType(): Class<KotlinInstant> = KotlinInstant::class.java
}
