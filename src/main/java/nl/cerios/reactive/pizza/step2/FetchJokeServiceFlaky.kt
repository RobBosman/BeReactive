package nl.cerios.reactive.pizza.step2

import nl.cerios.reactive.pizza.step1.FetchJokeService.fetchJoke
import org.slf4j.LoggerFactory
import kotlin.random.Random

object FetchJokeServiceFlaky {

  private val log = LoggerFactory.getLogger(javaClass)

  fun fetchJokeFlaky(): String {
    return when (Random.nextInt(3)) {
      0 -> {
        log.info("=== throw exception ===")
        throw Exception("EXCEPTION")
      }
      1 -> {
        log.info("=== do not respond ===")
        Thread.sleep(Long.MAX_VALUE)
        throw Exception("TIMEOUT")
      }
      2 -> {
        log.info("=== reply an error ===")
        "ERROR"
      }
      else -> {
        log.info("    invoke FetchJokeService")
        fetchJoke()
      }
    }
  }
}
