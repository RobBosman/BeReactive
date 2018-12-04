package nl.cerios.reactive.pizza.crippled

import nl.cerios.reactive.pizza.FetchJokeService.fetchJoke
import org.slf4j.LoggerFactory
import kotlin.random.Random

object FetchJokeServiceCrippled {

  private val log = LoggerFactory.getLogger(javaClass)

  fun fetchJokeCrippled(): String {
    // inject some chaos
    return when (Random.nextInt(3)) {
      0 -> fetchJoke()
      1 -> {
        log.error("returning 'ERROR'")
        "ERROR"
      }
      else -> {
        log.error("causing an ERROR")
        throw RuntimeException("ERROR")
      }
    }
  }
}
