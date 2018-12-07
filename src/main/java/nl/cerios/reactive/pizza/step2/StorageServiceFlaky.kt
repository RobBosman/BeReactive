package nl.cerios.reactive.pizza.step2

import com.mongodb.client.MongoClient
import nl.cerios.reactive.pizza.step1.StorageService.getMongoClient
import org.slf4j.LoggerFactory
import kotlin.random.Random

object StorageServiceFlaky {

  private val log = LoggerFactory.getLogger(javaClass)

  fun getMongoClientFlaky(): MongoClient {
    // inject some chaos
    return when (Random.nextInt(2)) {
      0 -> {
        log.debug("cause an ERROR")
        throw RuntimeException("ERROR")
      }
      else -> {
        log.debug("invoke service")
        getMongoClient()
      }
    }
  }
}