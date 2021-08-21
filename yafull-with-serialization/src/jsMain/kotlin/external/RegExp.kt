package external

import kotlin.js.RegExp

fun Regex.findWithIndices(input: CharSequence, startIndex: Int = 0): MatchResult? {
    val nativePattern = RegExp(pattern, options.joinToString(separator = "", prefix = "gud") { it.value })
    if (startIndex < 0 || startIndex > input.length) {
        throw IndexOutOfBoundsException("Start index out of bounds: $startIndex, input length: ${input.length}")
    }

    return nativePattern.findNext(input.toString(), startIndex)
}

private fun RegExp.findNext(input: String, from: Int): MatchResult? {
    this.lastIndex = from
    val match = exec(input)
    if (match == null) return null
    val range = match.index..lastIndex - 1
    val matchIndices = match.asDynamic().indices
    val matchGroups = match.asDynamic().groups

    return object : MatchResult {
        override val range: IntRange = range
        override val value: String
            get() = match[0]!!

        override val groups: NamedMatchGroupCollection =
            object : NamedMatchGroupCollection, AbstractCollection<MatchGroup?>() {
                override val size: Int get() = match.length
                override fun iterator(): Iterator<MatchGroup?> = indices.asSequence().map { this[it] }.iterator()
                override fun get(index: Int): MatchGroup? = match[index]?.let {
                    val startIndex = matchIndices[index][0].unsafeCast<Int>()
                    val endIndexExclusive = matchIndices[index][1].unsafeCast<Int>()
                    MatchGroup(it, startIndex until endIndexExclusive)
                }

                override fun get(name: String): MatchGroup? {
                    val value = matchGroups[name].unsafeCast<String?>() ?: return null
                    val startIndex = matchIndices.groups[name][0].unsafeCast<Int>()
                    val endIndexExclusive = matchIndices.groups[name][1].unsafeCast<Int>()
                    return MatchGroup(value, startIndex until endIndexExclusive)
                }
            }


        private var groupValues_: List<String>? = null

        override val groupValues: List<String>
            get() {
                if (groupValues_ == null) {
                    groupValues_ = object : AbstractList<String>() {
                        override val size: Int get() = match.length
                        override fun get(index: Int): String = match[index] ?: ""
                    }
                }
                return groupValues_!!
            }

        override fun next(): MatchResult? =
            this@findNext.findNext(input, if (range.isEmpty()) range.start + 1 else range.endInclusive + 1)
    }
}


interface NamedMatchGroupCollection : Collection<MatchGroup?> {
    operator fun get(index: Int): MatchGroup?
    operator fun get(name: String): MatchGroup?
}

data class MatchGroup(val value: String, val range: IntRange)


/**
 * Represents the results from a single regular expression match.
 */
public interface MatchResult {
    /** The range of indices in the original string where match was captured. */
    public val range: IntRange

    /** The substring from the input string captured by this match. */
    public val value: String

    /**
     * A collection of groups matched by the regular expression.
     *
     * This collection has size of `groupCount + 1` where `groupCount` is the count of groups in the regular expression.
     * Groups are indexed from 1 to `groupCount` and group with the index 0 corresponds to the entire match.
     */
    public val groups: NamedMatchGroupCollection

    /**
     * A list of matched indexed group values.
     *
     * This list has size of `groupCount + 1` where `groupCount` is the count of groups in the regular expression.
     * Groups are indexed from 1 to `groupCount` and group with the index 0 corresponds to the entire match.
     *
     * If the group in the regular expression is optional and there were no match captured by that group,
     * corresponding item in [groupValues] is an empty string.
     *
     * @sample samples.text.Regexps.matchDestructuringToGroupValues
     */
    public val groupValues: List<String>

    /**
     * An instance of [MatchResult.Destructured] wrapper providing components for destructuring assignment of group values.
     *
     * component1 corresponds to the value of the first group, component2 — of the second, and so on.
     *
     * @sample samples.text.Regexps.matchDestructuringToGroupValues
     */
    public val destructured: Destructured get() = Destructured(this)

    /** Returns a new [MatchResult] with the results for the next match, starting at the position
     *  at which the last match ended (at the character after the last matched character).
     */
    public fun next(): MatchResult?

    /**
     * Provides components for destructuring assignment of group values.
     *
     * [component1] corresponds to the value of the first group, [component2] — of the second, and so on.
     *
     * If the group in the regular expression is optional and there were no match captured by that group,
     * corresponding component value is an empty string.
     *
     * @sample samples.text.Regexps.matchDestructuringToGroupValues
     */
    public class Destructured internal constructor(public val match: MatchResult) {
        public operator inline fun component1(): String = match.groupValues[1]
        public operator inline fun component2(): String = match.groupValues[2]
        public operator inline fun component3(): String = match.groupValues[3]
        public operator inline fun component4(): String = match.groupValues[4]
        public operator inline fun component5(): String = match.groupValues[5]
        public operator inline fun component6(): String = match.groupValues[6]
        public operator inline fun component7(): String = match.groupValues[7]
        public operator inline fun component8(): String = match.groupValues[8]
        public operator inline fun component9(): String = match.groupValues[9]
        public operator inline fun component10(): String = match.groupValues[10]

        /**
         *  Returns destructured group values as a list of strings.
         *  First value in the returned list corresponds to the value of the first group, and so on.
         *
         * @sample samples.text.Regexps.matchDestructuringToGroupValues
         */
        public fun toList(): List<String> = match.groupValues.subList(1, match.groupValues.size)
    }
}
