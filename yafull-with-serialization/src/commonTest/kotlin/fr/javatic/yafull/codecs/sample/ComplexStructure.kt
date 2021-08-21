package fr.javatic.yafull.codecs.sample

import kotlinx.serialization.Serializable

@Serializable
class ComplexStructure(val valueA: String, val flat: FlatStructure)
