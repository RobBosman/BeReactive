package nl.cerios.reactive.pizza.step2

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicLong
import kotlin.concurrent.thread

internal object CoroutineTest {

  private val log = LoggerFactory.getLogger(javaClass)

  @Test
  fun run() {
    println("Start")

    thread(start = true) {
      log.debug("say")
      Thread.sleep(1_000)
      log.debug("Hello")
    }

    GlobalScope.launch {
      log.debug("say again")
      delay(1_000)
      log.debug("Hello again")
    }

    log.debug("and...")
    Thread.sleep(2_000)
    log.debug("Stop")


    val c = AtomicLong()
    for (i in 1..1_000_000L) {
      GlobalScope.launch {
        c.addAndGet(1)
      }
    }
    Thread.sleep(1)
    log.debug("result: ${c.get()}")
  }
}