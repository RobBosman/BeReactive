package nl.cerios.reactive.pizza

import nl.cerios.reactive.pizza.FetchJokeService.fetchJoke
import nl.cerios.reactive.pizza.StorageService.convertAndStore
import nl.cerios.reactive.pizza.StorageService.getMongoClient
import nl.cerios.reactive.pizza.StorageService.getMongoCollection
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture

internal class AsyncWithCompletableFuturesTest {

  private val log = LoggerFactory.getLogger(javaClass)

  @Test
  fun completableFutureProof() {
    val completableFuture = CompletableFuture<Int>()

    CompletableFuture
        .supplyAsync {
          log.debug("Determine required processing time")
          1000L
        }
        .thenAcceptAsync { delayMillis ->
          log.debug("Processing...")
          Thread.sleep(delayMillis)
        }
        .thenApply {
          log.debug("Got it!")
          42
        }
        .thenAccept { answer ->
          completableFuture.complete(answer)
        }

    log.debug("Do you know the answer? - ${completableFuture.isDone}")
    val answer = completableFuture.get() // blocking wait
    log.debug("Ah, the answer is $answer.")
  }

  @Test
  fun run() {
    log.debug("here we go")
    val jokeRawCF = CompletableFuture.supplyAsync(::fetchJoke)

    val mongoClientCF = CompletableFuture.supplyAsync(::getMongoClient)

    val allDoneCF =
        mongoClientCF
            .thenApply(::getMongoCollection)
            .thenCombine(jokeRawCF) { mongoCollection, jokeRaw -> convertAndStore(jokeRaw, mongoCollection) }
            .thenAccept {
              log.debug("close MongoDB client")
              mongoClientCF.get().close()
            }

    log.debug("wait until all is done")
    allDoneCF.get() // blocking wait

    log.debug("there you are!")
  }
}
