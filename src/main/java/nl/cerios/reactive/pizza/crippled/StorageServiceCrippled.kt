package nl.cerios.reactive.pizza.crippled

import com.mongodb.client.MongoClient
import nl.cerios.reactive.pizza.StorageService.getMongoClient
import org.slf4j.LoggerFactory
import kotlin.random.Random

object StorageServiceCrippled {

  private val log = LoggerFactory.getLogger(javaClass)

  fun getMongoClientCrippled(): MongoClient {
    // inject some chaos
    return if (Random.nextBoolean()) {
      getMongoClient()
    } else {
      log.error("causing an ERROR")
      throw RuntimeException("ERROR")
    }
  }
}