package nl.cerios.reactive.pizza

import nl.cerios.reactive.pizza.FetchJokeService.fetchJoke
import nl.cerios.reactive.pizza.StorageService.convertAndStore
import nl.cerios.reactive.pizza.StorageService.getMongoClient
import nl.cerios.reactive.pizza.StorageService.getMongoCollection
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
        .thenAccept { delayMillis ->
          log.debug("Pondering...")
          Thread.sleep(delayMillis)
        }
        .thenApply {
          log.debug("Got it!")
          42
        }

    log.debug("Do you know the answer? - ${provideAnswerCF.isDone}")
    val answer = provideAnswerCF.get() // blocking wait
    log.debug("Ah, the answer is $answer.")
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
          convertAndStore(jokeRaw, mongoCollection)
          log.debug("close MongoDB client")
          mongoClient.close()
        }

    log.debug("wait until all is done")
    allDoneCF.get() // blocking wait

    log.debug("there you are!")
  }
}
