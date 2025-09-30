package ms.homemonitor.shared.tools

import java.time.Instant

class TimedCache<T>() {
    private var expireTime = Instant.MIN
    private var cachedValue:T? = null

    fun put(value: T, expireTimeInSeconds: Int) {
        expireTime = Instant.now().plusSeconds(expireTimeInSeconds.toLong())
        cachedValue = value
    }

    fun get(): T? {
        if (Instant.now().isBefore(expireTime))
            return cachedValue
        return null
    }
}