package nl.bransom.reactive.chunk3

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.stream.IntStream
import kotlin.concurrent.thread

internal object ParallelProcessingTest {

  private val log = LoggerFactory.getLogger(javaClass)
  private const val NUM_PARALLEL_PROCESSES = 10

  @Test
  fun withThreads() {
    val startNano = System.nanoTime()
    val endNano = AtomicLong(0)
    val count = AtomicInteger(0)

    IntStream.range(0, NUM_PARALLEL_PROCESSES)
        .forEach {

          thread(start = true) {
            log.debug("performing task in thread")
            Thread.sleep(1_000)
            if (count.incrementAndGet() == NUM_PARALLEL_PROCESSES)
              endNano.set(System.nanoTime())
          }
        }

    while (endNano.get() == 0L) Thread.yield()
    log.debug("${count.get()} Thread-tasks took ${endNano.get().minus(startNano) / 1_000_000} ms")
  }

  @Test
  fun withCoroutines() {
    val startNano = System.nanoTime()
    val endNano = AtomicLong(0)
    val count = AtomicInteger(0)

    IntStream.range(0, NUM_PARALLEL_PROCESSES)
        .forEach {

          GlobalScope.launch {
            log.debug("performing task in fiber")
            delay(1_000)
            if (count.incrementAndGet() == NUM_PARALLEL_PROCESSES)
              endNano.set(System.nanoTime())
          }
        }

    while (endNano.get() == 0L) Thread.yield()
    log.debug("${count.get()} coroutines-tasks took ${endNano.get().minus(startNano) / 1_000_000} ms")
  }
}