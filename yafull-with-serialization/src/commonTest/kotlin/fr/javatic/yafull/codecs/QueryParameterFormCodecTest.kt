package fr.javatic.yafull.codecs

import fr.javatic.yafull.codecs.sample.FlatStructure
import fr.javatic.yafull.codecs.sample.TestEnum
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.TestContext
import io.kotest.datatest.IsStableType
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerializationException
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class QueryParameterFormCodecTest : BehaviorSpec({
    @IsStableType
    class EncodeTestData(
        val parameterName: String,
        val valueType: KType,
        val value: Any?,
        val explode: Boolean,
        val expectedResult: TestContext.(res: String) -> Unit
    ) {
        fun check(ctx: TestContext, res: String) {
            ctx.expectedResult(res)
        }

        override fun toString(): String {
            return "parameterName='$parameterName', valueType=$valueType, value=$value, explode=$explode"
        }
    }

    val subject = QueryParameterFormCodec
    Given("$subject") {
        When("encode valid data") {
            withData(
                listOf(
                    EncodeTestData("foo", typeOf<Boolean>(), true, false) { it shouldBe "foo=true" },
                    EncodeTestData(
                        "foo",
                        typeOf<List<Boolean>>(),
                        listOf(true, false, true),
                        false
                    ) { it shouldBe "foo=true,false,true" },
                    EncodeTestData(
                        "foo",
                        typeOf<List<Boolean>>(),
                        listOf(true, false, true),
                        true
                    ) { it shouldBe "foo=true&foo=false&foo=true" },

                    EncodeTestData("foo", typeOf<Int>(), 666, false) { it shouldBe "foo=666" },
                    EncodeTestData(
                        "foo",
                        typeOf<List<Int>>(),
                        listOf(1664, 666, 33),
                        false
                    ) { it shouldBe "foo=1664,666,33" },
                    EncodeTestData(
                        "foo",
                        typeOf<List<Int>>(),
                        listOf(1664, 666, 33),
                        true
                    ) { it shouldBe "foo=1664&foo=666&foo=33" },

                    EncodeTestData("foo", typeOf<String>(), "", false) { it shouldBe "foo=" },
                    EncodeTestData("foo", typeOf<String>(), null, true) { it shouldBe "" },
                    EncodeTestData("foo", typeOf<String>(), "bar", false) { it shouldBe "foo=bar" },
                    EncodeTestData("foo&bar", typeOf<String>(), "value", true) { it shouldBe "foo%26bar=value" },
                    EncodeTestData(
                        "foo,bar",
                        typeOf<String>(),
                        "value?using\"invalidChar",
                        false
                    ) { it shouldBe "foo,bar=value%3Fusing%22invalidChar" },
                    EncodeTestData(
                        "foo?bar",
                        typeOf<String>(),
                        "value?using\"invalidChar",
                        true
                    ) { it shouldBe "foo%3Fbar=value%3Fusing%22invalidChar" },

                    EncodeTestData("foo", typeOf<List<String?>>(), listOf(null, null, null), false) { it shouldBe "" },
                    EncodeTestData("foo", typeOf<List<String?>>(), listOf(null, null, null), true) { it shouldBe "" },
                    EncodeTestData(
                        "foo",
                        typeOf<List<String?>>(),
                        listOf("chat", null, "lap,ing"),
                        false
                    ) { it shouldBe "foo=chat,lap%2Cing" },
                    EncodeTestData(
                        "foo",
                        typeOf<List<String>>(),
                        listOf("chat", "chient", "lap,ing"),
                        false
                    ) { it shouldBe "foo=chat,chient,lap%2Cing" },
                    EncodeTestData(
                        "foo",
                        typeOf<List<String>>(),
                        listOf("chat", "chi,ent", "laping"),
                        true
                    ) { it shouldBe "foo=chat&foo=chi,ent&foo=laping" },

                    EncodeTestData("foo", typeOf<TestEnum>(), TestEnum.VALUE_A, false) { it shouldBe "foo=VALUE_A" },
                    EncodeTestData("foo", typeOf<TestEnum?>(), null, false) { it shouldBe "" },

                    EncodeTestData(
                        "foo",
                        typeOf<List<TestEnum>>(),
                        listOf(TestEnum.VALUE_A, TestEnum.VALUE_C),
                        false
                    ) { it shouldBe "foo=VALUE_A,VALUE_C" },
                    EncodeTestData(
                        "foo",
                        typeOf<List<TestEnum>>(),
                        listOf(TestEnum.VALUE_A, TestEnum.VALUE_C),
                        true
                    ) { it shouldBe "foo=VALUE_A&foo=VALUE_C" },

                    EncodeTestData(
                        "fo^o",
                        typeOf<List<String>>(),
                        listOf("ch,at", "chi&ent", "lap?ing"),
                        false
                    ) { it shouldBe "fo%5Eo=ch%2Cat,chi&ent,lap%3Fing" },
                    EncodeTestData(
                        "fo^o",
                        typeOf<List<String>>(),
                        listOf("ch,at", "chi&ent", "lap?ing"),
                        true
                    ) { it shouldBe "fo%5Eo=ch,at&fo%5Eo=chi%26ent&fo%5Eo=lap%3Fing" },
                    EncodeTestData(
                        "foo",
                        typeOf<List<LocalDate>>(),
                        listOf(LocalDate(2020, 1, 1), LocalDate(2020, 6, 7), LocalDate(2020, 4, 28)),
                        false
                    ) { it shouldBe "foo=2020-01-01,2020-06-07,2020-04-28" },
                    EncodeTestData(
                        "foo",
                        typeOf<List<LocalDate>>(),
                        listOf(LocalDate(2020, 1, 1), LocalDate(2020, 6, 7), LocalDate(2020, 4, 28)),
                        true
                    ) { it shouldBe "foo=2020-01-01&foo=2020-06-07&foo=2020-04-28" },
                    EncodeTestData(
                        "fo&o",
                        typeOf<List<LocalDate>>(),
                        listOf(LocalDate(2020, 1, 1), LocalDate(2020, 6, 7), LocalDate(2020, 4, 28)),
                        false
                    ) { it shouldBe "fo%26o=2020-01-01,2020-06-07,2020-04-28" },
                    EncodeTestData(
                        "fo=o",
                        typeOf<List<LocalDate>>(),
                        listOf(LocalDate(2020, 1, 1), LocalDate(2020, 6, 7), LocalDate(2020, 4, 28)),
                        true
                    ) { it shouldBe "fo%3Do=2020-01-01&fo%3Do=2020-06-07&fo%3Do=2020-04-28" },
                )
            ) { data ->
                When("encode with settings $data") {
                    Then("is should produce `${data.expectedResult}`") {
                        data.check(this, subject.encode(data.parameterName, data.valueType, data.value, data.explode))
                    }
                }
            }
        }

        When("try to encode nested list (explode=true)") {
            Then("it should produce an exception") {
                shouldThrow<SerializationException> {
                    subject.encode(
                        "foo",
                        listOf(listOf("1-a", "1-b"), listOf("2-a"), listOf("3-a", "3-b", "3-c")),
                        true
                    )
                }
            }
        }

        When("try to encode nested list (explode=false)") {
            Then("it should produce a SerializationException") {
                shouldThrow<SerializationException> {
                    subject.encode(
                        "foo",
                        listOf(listOf("1-a", "1-b"), listOf("2-a"), listOf("3-a", "3-b", "3-c")),
                        false
                    )
                }
            }
        }

        When("try to encode non primitive/enum") {
            Then("it should produce a SerializationException") {
                shouldThrow<SerializationException> {
                    subject.encode("foo", FlatStructure("propAValue", 12L), false)
                }
            }
        }

        When("try to encode list of non primitive/enum") {
            Then("it should produce a SerializationException") {
                shouldThrow<SerializationException> {
                    subject.encode(
                        "foo",
                        listOf(FlatStructure("1-propAValue", 12L), FlatStructure("p2-ropAValue", 24L)),
                        false
                    )
                }
            }
        }
    }
})
