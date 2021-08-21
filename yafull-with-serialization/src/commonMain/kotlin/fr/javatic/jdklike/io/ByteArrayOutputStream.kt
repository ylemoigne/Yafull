package fr.javatic.jdklike.io

import kotlin.math.max

class ByteArrayOutputStream(size: Int = 32) : OutputStream() {
    init {
        require(size >= 0) { "Size must be greater than or equals to 0" }
    }

    private var buf: ByteArray = ByteArray(size)
    var size: Int = 0
        private set

    override fun write(b: Int) {
        growIfNeeded(size + 1)
        buf[size] = b.toByte()
        size += 1
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        if (off or len < 0 || len > b.size - off) throw IndexOutOfBoundsException()
        growIfNeeded(size + len)
        b.copyInto(buf, size, off, off + len)
        size += len
    }

    private fun growIfNeeded(requestedCapacity: Int) {
        val oldCapacity = buf.size
        val minGrowth = requestedCapacity - oldCapacity
        if (minGrowth > 0) {
            buf = buf.copyOf(max(minGrowth, oldCapacity) + oldCapacity)
        }
    }

    fun reset() {
        size = 0
    }

    fun toByteArray(): ByteArray {
        return buf.copyOf(size)
    }

    override fun toString(): String {
        return buf.decodeToString(0, size)
    }

    override fun close() {
    }
}
