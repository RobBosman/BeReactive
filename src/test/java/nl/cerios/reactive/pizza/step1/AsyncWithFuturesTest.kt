package nl.cerios.reactive.pizza.step1

import nl.cerios.reactive.pizza.step1.FetchJokeService.fetchJoke
import nl.cerios.reactive.pizza.step1.StorageService.convertAndStore
import nl.cerios.reactive.pizza.step1.StorageService.getMongoClient
import nl.cerios.reactive.pizza.step1.StorageService.getMongoCollection
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors

internal object AsyncWithFuturesTest {

  private val log = LoggerFactory.getLogger(javaClass)

  private fun findTheAnswer(): Int {
    log.debug("Pondering...")
    Thread.sleep(1000)
    return 42
  }

  @Test
  fun hitchhikersGuideToTheFuture() {
    val executor = Executors.newFixedThreadPool(2)

    val future = executor.submit(::findTheAnswer)

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

    val jokeRawF = executor.submit(::fetchJoke)

    val mongoClientF = executor.submit(::getMongoClient)

    val allDoneF = executor.submit {
      log.debug("wait for async tasks to complete")
      val jokeRaw = jokeRawF.get() // blocking wait
      val mongoClient = mongoClientF.get() // blocking wait

      val mongoCollection = getMongoCollection(mongoClient)
      convertAndStore(jokeRaw, mongoCollection)
      log.debug("close MongoDB client")
      mongoClient.close()
    }

    log.debug("wait until all is done")
    allDoneF.get() // blocking wait

    executor.shutdown()
    log.debug("there you are!")
  }
}
