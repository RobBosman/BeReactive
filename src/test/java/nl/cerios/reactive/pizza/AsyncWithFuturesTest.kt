package nl.cerios.reactive.pizza

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import nl.cerios.reactive.pizza.FetchJokeService.fetchJoke
import nl.cerios.reactive.pizza.StorageService.convertAndStore
import nl.cerios.reactive.pizza.StorageService.getMongoClient
import nl.cerios.reactive.pizza.StorageService.getMongoCollection
import org.bson.Document
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors

internal class AsyncWithFuturesTest {

  private val log = LoggerFactory.getLogger(javaClass)

  private fun findTheAnswer(): Int {
    log.debug("Pondering...")
    Thread.sleep(1000)
    return 42
  }

  @Test
  fun futureProof() {
    val executor = Executors.newFixedThreadPool(2)

    val future = executor.submit<Int>(::findTheAnswer)
    log.debug("Are you done? - ${future.isDone}")

    // do other stuff

    val answer = future.get() // blocking wait
    log.debug("Ah, the answer is $answer.")

    executor.shutdown()
  }

  @Test
  fun run() {
    log.debug("here we go")
    val executor = Executors.newFixedThreadPool(4)

    val jokeRawF = executor.submit<String>(::fetchJoke)

    val mongoClientF = executor.submit<MongoClient>(::getMongoClient)

    val mongoCollectionF = executor.submit<MongoCollection<Document>> {
      log.debug("wait for MongoDB client")
      val mongoClient = mongoClientF.get() // blocking wait
      getMongoCollection(mongoClient)
    }

    val allDoneF = executor.submit<Unit> {
      log.debug("wait for async tasks to complete")
      val jokeRaw = jokeRawF.get() // blocking wait
      val mongoCollection = mongoCollectionF.get() // blocking wait

      convertAndStore(jokeRaw, mongoCollection)
      log.debug("close MongoDB client")
      mongoClientF.get().close()
    }

    log.debug("wait until all is done")
    allDoneF.get() // blocking wait

    executor.shutdown()
    log.debug("there you are!")
  }
}
