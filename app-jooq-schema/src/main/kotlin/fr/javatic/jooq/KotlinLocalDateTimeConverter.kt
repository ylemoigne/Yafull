package fr.javatic.jooq

import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import org.jooq.Converter
import kotlinx.datetime.LocalDateTime as KotlinDateTime
import java.time.LocalDateTime as JavaDateTime

class KotlinLocalDateTimeConverter : Converter<JavaDateTime, KotlinDateTime> {
    override fun from(databaseObject: JavaDateTime?): KotlinDateTime? = databaseObject?.toKotlinLocalDateTime()
    override fun to(userObject: KotlinDateTime?): JavaDateTime? = userObject?.toJavaLocalDateTime()

    override fun fromType(): Class<JavaDateTime> = JavaDateTime::class.java
    override fun toType(): Class<KotlinDateTime> = KotlinDateTime::class.java
}
