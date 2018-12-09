package nl.cerios.reactive.pizza.step2

import nl.cerios.reactive.pizza.step1.FetchJokeService.fetchJoke
import org.slf4j.LoggerFactory
import kotlin.random.Random

object FetchJokeServiceFlaky {

  private val log = LoggerFactory.getLogger(javaClass)

  fun fetchJokeFlaky(): String {
    return when (Random.nextInt(4)) {
      0 -> {
        log.debug("==> throw an exception")
        throw RuntimeException("EXCEPTION")
      }
      1 -> {
        log.debug("==> do not respond")
        Thread.sleep(Long.MAX_VALUE)
        throw RuntimeException("TIMEOUT")
      }
      2 -> {
        log.debug("==> return an error")
        "ERROR"
      }
      else -> {
        log.debug("==> invoke service")
        fetchJoke()
      }
    }
  }
}
