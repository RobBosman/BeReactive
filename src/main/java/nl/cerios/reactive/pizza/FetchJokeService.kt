package nl.cerios.reactive.pizza

import org.slf4j.LoggerFactory
import java.net.HttpURLConnection
import java.net.URL

object FetchJokeService {

  private val log = LoggerFactory.getLogger(javaClass)
  private const val apiUrl = "http://api.icndb.com/jokes/random?limitTo=[nerdy,explicit]" // Chuck Norris jokes

  fun fetchJoke(): String {
    log.debug("fetch joke")
    val connection = URL(apiUrl).openConnection() as HttpURLConnection
    try {
      connection.inputStream.reader()
          .use { reader ->
            return reader.readText()
          }
    } finally {
      connection.disconnect()
      log.debug("fetched joke")
    }
  }
}
