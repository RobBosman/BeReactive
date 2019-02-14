package nl.cerios.reactive.pizza.step3

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.IntStream
import kotlin.concurrent.thread

internal object CoroutineTest {

  private val log = LoggerFactory.getLogger(javaClass)
  private const val LOOP_MAX = 50

  @Test
  fun withThreads() {
    val total = AtomicInteger()
    val startNano = System.nanoTime()
    var endNano = 0L
        IntStream
        .range(0, LOOP_MAX)
        .forEach {

          thread(start = true) {
            log.debug("performing task")
            Thread.sleep(1_000)
            if (total.incrementAndGet() == LOOP_MAX)
              endNano = System.nanoTime()
          }

        }
    Thread.sleep(2_000)
    log.debug("${total.get()} Thread-tasks took ${endNano.minus(startNano) / 1_000_000} ms")
  }

  @Test
  fun withCoroutines() {
    val total = AtomicInteger()
    val startNano = System.nanoTime()
    var endNano = 0L
    IntStream
        .range(0, LOOP_MAX)
        .forEach {

          GlobalScope.launch {
            log.debug("performing task")
            delay(1_000)
            if (total.incrementAndGet() == LOOP_MAX)
              endNano = System.nanoTime()
          }

        }
    Thread.sleep(2_000)
    log.debug("${total.get()} coroutines-tasks took ${endNano.minus(startNano) / 1_000_000} ms")
  }
}