package nl.cerios.reactive.chocolate.chunk2

import com.mongodb.client.MongoClient
import nl.cerios.reactive.chocolate.chunk1.StorageService.getMongoClient
import org.slf4j.LoggerFactory
import kotlin.random.Random

object StorageServiceFlaky {

  private val log = LoggerFactory.getLogger(javaClass)

  fun getMongoClientFlaky(): MongoClient {
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
      else -> {
        log.info("    invoke StorageService")
        getMongoClient()
      }
    }
  }
}