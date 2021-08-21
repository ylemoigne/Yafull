package fr.javatic.jooq

import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.jooq.Converter
import kotlinx.datetime.LocalDate as KotlinDate
import java.time.LocalDate as JavaDate

class KotlinLocalDateConverter : Converter<JavaDate, KotlinDate> {
    override fun from(databaseObject: JavaDate?): KotlinDate? = databaseObject?.toKotlinLocalDate()
    override fun to(userObject: KotlinDate?): JavaDate? = userObject?.toJavaLocalDate()

    override fun fromType(): Class<JavaDate> = JavaDate::class.java
    override fun toType(): Class<KotlinDate> = KotlinDate::class.java
}
