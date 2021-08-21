package fr.javatic.yafull.rest

import fr.javatic.yafull.codecs.HeaderCodec
import fr.javatic.yafull.codecs.QueryParameterFormCodec
import fr.javatic.yafull.codecs.UriComponentCodec
import kotlin.reflect.KType

sealed class ParameterFormat(val style: String, val explode: Boolean) {
    sealed class Header(style: String, explode: Boolean) : ParameterFormat(style, explode) {
        internal abstract fun encode(
            headerName: String,
            valueType: KType,
            value: Any?,
            res: MutableList<Pair<String, String>> = mutableListOf()
        ): List<Pair<String, String>>

        internal abstract fun <T> decode(
            headerName: String,
            valueType: KType,
            headers: List<Pair<String, String>>
        ): T

        // "Exploded header are not supported by fetch api ( https://developer.mozilla.org/en-US/docs/Web/API/Headers/append )"
        // class Simple(explode: Boolean = false) : Header("simple", explode){
        object Simple : Header("simple", false) {
            override fun encode(
                headerName: String,
                valueType: KType,
                value: Any?,
                res: MutableList<Pair<String, String>>
            ): List<Pair<String, String>> =
                HeaderCodec.encode(headerName, valueType, value, explode, res)

            override fun <T> decode(headerName: String, valueType: KType, headers: List<Pair<String, String>>): T =
                HeaderCodec.decode(headerName, valueType, explode, headers)
        }
    }

    sealed class Query(style: String, explode: Boolean) : ParameterFormat(style, explode) {
        /*internal*/ abstract fun encode(
            // https://github.com/JetBrains/compose-jb/issues/738
            paramName: String,
            valueType: KType,
            value: Any?,
        ): String

        /*internal*/ abstract fun <T> decode( // https://github.com/JetBrains/compose-jb/issues/738
            parameterName: String,
            valueType: KType,
            queryString: String?
        ): T

        /*internal*/ abstract fun <T> decode( // https://github.com/JetBrains/compose-jb/issues/738
            parameterName: String,
            valueType: KType,
            values: Map<String, List<String>>?
        ): T

        /*internal*/ abstract fun <T> decode( // https://github.com/JetBrains/compose-jb/issues/738
            valueType: KType,
            values: List<String>?
        ): T

        class Form(explode: Boolean = true) : Query("form", explode) {
            override fun encode(paramName: String, valueType: KType, value: Any?): String =
                QueryParameterFormCodec.encode(paramName, valueType, value, explode)

            override fun <T> decode(
                parameterName: String,
                valueType: KType,
                queryString: String?
            ): T = QueryParameterFormCodec.decode(parameterName, valueType, queryString)

            override fun <T> decode(
                parameterName: String,
                valueType: KType,
                values: Map<String, List<String>>?
            ): T = QueryParameterFormCodec.decode(parameterName, valueType, values)

            override fun <T> decode(
                valueType: KType,
                values: List<String>?
            ): T = QueryParameterFormCodec.decode(valueType, values)
        }
//    class SpaceDelimited(explode:Boolean=true):ParameterQueryFormat("spaceDelimited",explode)
//    class PipeDelimited(explode:Boolean=true):ParameterQueryFormat("pipeDelimited",explode)
//    class DeepObject(explode:Boolean=true):ParameterQueryFormat("deepObject",explode)
    }

    sealed class Path(style: String, explode: Boolean) : ParameterFormat(style, explode) {
        abstract fun encode(type: KType, value: Any?): String
        abstract fun <T> decode(type: KType, value: String?): T

        object Simple : Path("simple", false) {
            override fun encode(type: KType, value: Any?): String = UriComponentCodec.encode(type, value)
            override fun <T> decode(type: KType, value: String?): T = UriComponentCodec.decode(type, value)
        }

//    class Matrix(explode:Boolean=false):ParameterPathFormat("matrix", explode)
//    class Label(explode: Boolean=false):ParameterPathFormat("label", explode)
    }

}
