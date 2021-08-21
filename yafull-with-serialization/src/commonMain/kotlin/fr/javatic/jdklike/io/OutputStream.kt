package fr.javatic.jdklike.io

abstract class OutputStream : Closeable, Flushable {
    abstract fun write(b: Int)

    open fun write(b: ByteArray) {
        write(b, 0, b.size)
    }

    open fun write(b: ByteArray, off: Int, len: Int) {
        if (off or len < 0 || len > b.size - off) throw IndexOutOfBoundsException()
        for (i in 0 until len) {
            write(b[off + i].toInt())
        }
    }

    override fun flush() {
    }

    override fun close() {
    }
}
