package nl.cerios.reactive.pizza

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors

internal class AsyncWithFuturesTest {

  private val log = LoggerFactory.getLogger(javaClass)

  private fun findTheAnswer(): Int {
    log.debug("Pondering...")
    Thread.sleep(1000)
    return 42
  }

  @Test
  fun futureProof() {
    val executor = Executors.newFixedThreadPool(2)

    val future = executor.submit<Int>(::findTheAnswer)
    log.debug("Are you done? - ${future.isDone}")

    // do other stuff

    val answer = future.get() // blocking wait
    log.debug("Ah, the answer is $answer.")

    executor.shutdown()
  }

  @Test
  fun run() {
    log.debug("here we go")
    val executor = Executors.newFixedThreadPool(4)

    val taskCompletedF = AsyncWithFutures().run(executor)

    log.debug("wait until done")
    taskCompletedF.get() // blocking wait

    executor.shutdown()
    log.debug("there you are!")
  }
}
