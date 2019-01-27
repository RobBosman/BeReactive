package nl.cerios.reactive.pizza.step1

import nl.cerios.reactive.pizza.step1.FetchJokeService.fetchJoke
import nl.cerios.reactive.pizza.step1.StorageService.convertAndStore
import nl.cerios.reactive.pizza.step1.StorageService.getMongoClient
import nl.cerios.reactive.pizza.step1.StorageService.getMongoCollection
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture

internal object AsyncWithCompletableFuturesTest {

  private val log = LoggerFactory.getLogger(javaClass)

  @Test
  fun hitchhikersGuideToTheCompletableFuture() {
    val provideAnswerCF = CompletableFuture
        .supplyAsync {
          log.debug("Determine required processing time")
          1500L
        }
        .thenApply { delayMillis ->
          log.debug("Pondering...")
          Thread.sleep(delayMillis)
          42
        }
        .thenApply { answer ->
          log.debug("Got it!")
          answer
        }

    log.debug("Do you know the answer? - ${provideAnswerCF.isDone}")
    log.debug("Ah, the answer is ${provideAnswerCF.get()}.") // blocking wait
  }

  @Test
  fun run() {
    log.debug("here we go")
    val jokeRawCF = CompletableFuture
        .supplyAsync(::fetchJoke)

    val allDoneCF = CompletableFuture
        .supplyAsync(::getMongoClient)
        .thenCombine(jokeRawCF) { mongoClient, jokeRaw ->
          val mongoCollection = getMongoCollection(mongoClient)
          val joke = convertAndStore(jokeRaw, mongoCollection)
          log.debug("close MongoDB client")
          mongoClient.close()
          joke
        }

    log.debug("wait until all is done")
    log.info("'${allDoneCF.get()}'") // blocking wait
    log.debug("there you are!")
  }
}
