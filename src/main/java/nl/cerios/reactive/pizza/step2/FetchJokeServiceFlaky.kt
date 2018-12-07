package nl.cerios.reactive.pizza.step2

import nl.cerios.reactive.pizza.step1.FetchJokeService.fetchJoke
import org.slf4j.LoggerFactory
import kotlin.random.Random

object FetchJokeServiceFlaky {

  private val log = LoggerFactory.getLogger(javaClass)

  fun fetchJokeFlaky(): String {
    // inject some chaos
    return when (Random.nextInt(3)) {
      0 -> {
        log.debug("cause an error")
        throw RuntimeException("ERROR")
      }
      1 -> {
        log.debug("return an error")
        "error"
      }
      else -> {
        log.debug("invoke service")
        fetchJoke()
      }
    }
  }
}
