package nl.cerios.reactive.pizza

import org.slf4j.LoggerFactory
import java.net.HttpURLConnection
import java.net.URL

object FetchJokeService {

  private val log = LoggerFactory.getLogger(javaClass)
  private const val url = "http://api.icndb.com/jokes/random?limitTo=[nerdy,explicit]" // Chuck Norris jokes

  fun fetchJoke(): String {
    log.debug("fetch joke")
    val httpConnection = URL(url).openConnection() as HttpURLConnection
    try {
      httpConnection.inputStream.reader()
          .use { httpReader ->
            return httpReader.readText()
          }
    } finally {
      httpConnection.disconnect()
      log.debug("fetched joke")
    }
  }
}