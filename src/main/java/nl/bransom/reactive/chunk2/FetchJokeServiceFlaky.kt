package nl.bransom.reactive.chunk2

import nl.bransom.reactive.chunk1.FetchJokeService.fetchJoke
import org.slf4j.LoggerFactory
import kotlin.random.Random

object FetchJokeServiceFlaky {

  private val log = LoggerFactory.getLogger(javaClass)

  fun fetchJokeFlaky(): String {
    return when (Random.nextInt(4)) {
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
        "FAILURE"
      }
      else -> {
        log.info("    invoke FetchJokeService")
        fetchJoke()
      }
    }
  }
}
