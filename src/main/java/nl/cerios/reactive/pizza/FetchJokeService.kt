package nl.cerios.reactive.pizza

import org.slf4j.LoggerFactory
import java.net.HttpURLConnection
import java.net.URL

object FetchJokeService {

  private val log = LoggerFactory.getLogger(javaClass)

  fun fetchJoke(): String {
    log.debug("fetch joke")
    val url = URL("http://api.icndb.com/jokes/random?limitTo=[nerdy,explicit]") // Chuck Norris jokes
    val httpConnection = url.openConnection() as HttpURLConnection
    httpConnection.connectTimeout = 5000
    httpConnection.readTimeout = 5000
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