package ms.homemonitor.shared.scheduler

import org.slf4j.LoggerFactory

abstract class AbstractBaseScheduler {
    protected val log = LoggerFactory.getLogger(javaClass)

    protected fun runSafely(block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            log.error("Scheduler job failed", e)
        }
    }
}