package nl.bransom.reactive.chunk1

import nl.bransom.reactive.chunk1.FetchJokeService.fetchJoke
import nl.bransom.reactive.chunk1.StorageService.convertAndStore
import nl.bransom.reactive.chunk1.StorageService.getMongoClient
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors

internal object AsyncWithFuturesTest {

  private val log = LoggerFactory.getLogger(javaClass)

  private fun findTheAnswer(): Int {
    log.debug("pondering...")
    Thread.sleep(1_000)
    return 42
  }

  @Test
  fun backToTheFuture() {
    val executor = Executors.newFixedThreadPool(2)

    val future = executor.submit(AsyncWithFuturesTest::findTheAnswer)

    log.debug("are you done? - ${future.isDone}")

    // do other stuff

    val answer = future.get() // blocking wait
    log.debug("ah, the answer is $answer.")

    executor.shutdown()
  }

  @Test
  fun run() {
    log.debug("here we go")
    val executor = Executors.newFixedThreadPool(4)

    val jokeRawF = executor.submit(::fetchJoke)

    val mongoClientF = executor.submit(::getMongoClient)

    val allDoneF = executor.submit<String> {
      log.debug("wait for async tasks to complete")
      val jokeRaw = jokeRawF.get() // blocking wait
      val mongoClient = mongoClientF.get() // blocking wait

      val joke = convertAndStore(jokeRaw, mongoClient)
      mongoClient.close()
      log.debug("closed MongoDB client")
      joke
    }

    log.debug("wait until all is done")
    log.info("'${allDoneF.get()}'") // blocking wait

    executor.shutdown()
    log.debug("there you are!")
  }
}
