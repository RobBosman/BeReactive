package nl.cerios.reactive.pizza.step1

import nl.cerios.reactive.pizza.step1.FetchJokeService.fetchJoke
import nl.cerios.reactive.pizza.step1.StorageService.convertAndStore
import nl.cerios.reactive.pizza.step1.StorageService.getMongoClient
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture

internal object AsyncWithCompletableFuturesTest {

  private val log = LoggerFactory.getLogger(javaClass)

  @Test
  fun backToTheCompletableFuture() {
    val provideAnswerCF = CompletableFuture
        .supplyAsync {
          log.debug("determine required processing time")
          1_500L
        }
        .thenApply { delayMillis ->
          log.debug("pondering...")
          Thread.sleep(delayMillis)
          42
        }
        .thenApply { answer ->
          log.debug("got it!")
          answer
        }

    log.debug("do you know the answer? - ${provideAnswerCF.isDone}")
    log.debug("ah, the answer is ${provideAnswerCF.get()}.") // blocking wait
  }

  @Test
  fun run() {
    log.debug("here we go")
    val jokeRawCF = CompletableFuture
        .supplyAsync(::fetchJoke)

    val allDoneCF = CompletableFuture
        .supplyAsync(::getMongoClient)
        .thenCombine(jokeRawCF) { mongoClient, jokeRaw ->
          val joke = convertAndStore(jokeRaw, mongoClient)
          mongoClient.close()
          log.debug("closed MongoDB client")
          joke
        }

    log.debug("wait until all is done")
    log.info("'${allDoneCF.get()}'") // blocking wait
    log.debug("there you are!")
  }
}
