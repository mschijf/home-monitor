package ms.homemonitor.shared.tools.micrometer

class DoubleWrapper(private var aDouble: Double): Number() {

    fun get() = aDouble

    fun set(value: Double) {
        aDouble = value
    }

    override fun toByte(): Byte {
        return aDouble.toInt().toByte()
    }

    override fun toDouble(): Double {
        return aDouble
    }

    override fun toFloat(): Float {
        return aDouble.toFloat()
    }

    override fun toInt(): Int {
        return aDouble.toInt()
    }

    override fun toLong(): Long {
        return aDouble.toLong()
    }

    override fun toShort(): Short {
        return aDouble.toInt().toShort()
    }
}