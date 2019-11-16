package nl.bransom.reactive.chunk3

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.IntStream
import kotlin.concurrent.thread

internal object ParallelProcessingTest {

  private val log = LoggerFactory.getLogger(javaClass)
  private const val NUM_PARALLEL_PROCESSES = 10

  @Test
  fun withThreads() {
    val total = AtomicInteger()
    val startNano = System.nanoTime()
    var endNano = 0L

    IntStream.range(0, NUM_PARALLEL_PROCESSES)
        .forEach {

          thread(start = true) {
            log.debug("performing task in thread")
            Thread.sleep(1_000)
            if (total.incrementAndGet() == NUM_PARALLEL_PROCESSES)
              endNano = System.nanoTime()
          }
        }

    while (endNano == 0L) Thread.yield()
    log.debug("${total.get()} Thread-tasks took ${endNano.minus(startNano) / 1_000_000} ms")
  }

  @Test
  fun withCoroutines() {
    val total = AtomicInteger()
    val startNano = System.nanoTime()
    var endNano = 0L
    IntStream.range(0, NUM_PARALLEL_PROCESSES)
        .forEach {

          GlobalScope.launch {
            log.debug("performing task in fiber")
            delay(1_000)
            if (total.incrementAndGet() == NUM_PARALLEL_PROCESSES)
              endNano = System.nanoTime()
          }
        }

    while (endNano == 0L) Thread.yield()
    log.debug("${total.get()} coroutines-tasks took ${endNano.minus(startNano) / 1_000_000} ms")
  }
}