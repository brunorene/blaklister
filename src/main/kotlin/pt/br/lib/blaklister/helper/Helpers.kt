package pt.br.lib.blaklister.helper

import java.nio.ByteBuffer
import java.util.UUID

fun UUID.toByteArray(): ByteArray {
    val buffer = ByteBuffer.allocate(Long.SIZE_BYTES * 2)
    buffer.putLong(mostSignificantBits)
    buffer.putLong(leastSignificantBits)
    return buffer.array()
}

const val SEPARATOR: Char = '#'

fun List<ByteArray>.joinAsByteArray(): ByteArray {
    val buffer = ByteBuffer.allocate(sumBy { it.size } + 1)
    forEach {
        buffer.put(it)
        buffer.putChar(SEPARATOR)
    }
    return buffer.array()
}
