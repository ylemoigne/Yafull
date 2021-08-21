package fr.javatic.jdklike.util

import kotlin.math.max
import kotlin.math.min

class BitSet {
    private var words: LongArray = LongArray(wordIndex(BITS_PER_WORD - 1) + 1)
    private var wordsInUse = 0

    fun set(bitIndex: Int) {
        if (bitIndex < 0) throw IndexOutOfBoundsException("bitIndex < 0: $bitIndex")
        val wordIndex = wordIndex(bitIndex)
        expandTo(wordIndex)
        words[wordIndex] = words[wordIndex] or (1L shl bitIndex)
    }

    operator fun set(fromIndex: Int, toIndex: Int) {
        if (fromIndex == toIndex) return

        val startWordIndex = wordIndex(fromIndex)
        val endWordIndex = wordIndex(toIndex - 1)
        expandTo(endWordIndex)

        val firstWordMask = WORD_MASK shl fromIndex
        val lastWordMask = WORD_MASK ushr -toIndex
        if (startWordIndex == endWordIndex) {
            words[startWordIndex] = words[startWordIndex] or (firstWordMask and lastWordMask)
        } else {
            words[startWordIndex] = words[startWordIndex] or firstWordMask
            for (i in startWordIndex + 1 until endWordIndex) words[i] = WORD_MASK
            words[endWordIndex] = words[endWordIndex] or lastWordMask
        }
    }

    operator fun get(bitIndex: Int): Boolean {
        if (bitIndex < 0) throw IndexOutOfBoundsException("bitIndex < 0: $bitIndex")
        val wordIndex = wordIndex(bitIndex)
        return (wordIndex < wordsInUse
                && words[wordIndex] and (1L shl bitIndex) != 0L)
    }


    fun or(set: BitSet) {
        if (this === set) return
        val wordsInCommon: Int = min(wordsInUse, set.wordsInUse)
        if (wordsInUse < set.wordsInUse) {
            ensureCapacity(set.wordsInUse)
            wordsInUse = set.wordsInUse
        }

        for (i in 0 until wordsInCommon) words[i] = words[i] or set.words[i]

        if (wordsInCommon < set.wordsInUse)
            set.words.copyInto(words, wordsInCommon, wordsInCommon)
    }

    private fun expandTo(wordIndex: Int) {
        val wordsRequired = wordIndex + 1
        if (wordsInUse < wordsRequired) {
            ensureCapacity(wordsRequired)
            wordsInUse = wordsRequired
        }
    }

    private fun ensureCapacity(wordsRequired: Int) {
        if (words.size < wordsRequired) {
            val request: Int = max(2 * words.size, wordsRequired)
            words = words.copyOf(request)
        }
    }

    companion object {
        private const val ADDRESS_BITS_PER_WORD = 6
        private const val BITS_PER_WORD = 1 shl ADDRESS_BITS_PER_WORD
        private const val WORD_MASK = -0x1L

        private fun wordIndex(bitIndex: Int): Int {
            return bitIndex shr ADDRESS_BITS_PER_WORD
        }
    }
}
