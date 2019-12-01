package nl.bransom.reactive.chunk2

import com.mongodb.client.MongoClient
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import nl.bransom.reactive.chunk1.StorageService.convertAndStore
import nl.bransom.reactive.chunk2.FetchJokeServiceFlaky.fetchJokeFlaky
import nl.bransom.reactive.chunk2.StorageServiceFlaky.getMongoClientFlaky
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit.MILLISECONDS

internal object ResilienceTest {

  private val log = LoggerFactory.getLogger(javaClass)

  @Test
  fun backToResilience() {
    log.debug("here we go")
    val processControl = Semaphore(0)

    Single
        .create<String> { emitter -> emitter.onSuccess(fetchJokeFlaky()) }
        .timeout(200, MILLISECONDS)
        .subscribeOn(Schedulers.io())
        .doOnSuccess { jokeJson ->
          if (!jokeJson.contains("success"))
            throw Throwable("invalid data: $jokeJson")
        }
        .doOnError { t -> log.warn("error detected: '${t.message}'") }
        .retry(3)
        .onErrorResumeNext(Single.just("fallback joke"))
        .doFinally { processControl.release() }
        .subscribe({ jokeJson -> log.info("'$jokeJson'") },
            { t -> log.error("an ERROR occurred", t) })

    log.debug("wait a second...")
    processControl.tryAcquire(1_000, MILLISECONDS)
    log.debug("there you are!")
  }

  @Test
  fun run() {
    log.debug("here we go")
    val processControl = Semaphore(0)

    val jokeJsonO = Single
        .create<String> { it.onSuccess(fetchJokeFlaky()) }
        .timeout(200, MILLISECONDS)
        .subscribeOn(Schedulers.io())
        .doOnSuccess { jokeJson -> if (!jokeJson.contains("success")) throw Throwable("invalid data: $jokeJson") }
        .retry(3)
        .onErrorResumeNext(
            Single.just("""{ "type": "success", "value": { "categories": [], "joke": "fallback joke" } }"""))

    Single
        .create<MongoClient> { it.onSuccess(getMongoClientFlaky()) }
        .timeout(1_000, MILLISECONDS)
        .subscribeOn(Schedulers.io())
        .retry(2)
        .zipWith(jokeJsonO,
            BiFunction { mongoClient: MongoClient, jokeJson: String ->
              val joke = convertAndStore(jokeJson, mongoClient)
              mongoClient.close()
              log.debug("closed MongoDB client")
              joke
            })
        .doFinally { processControl.release() }
        .subscribe(
            { joke -> log.info("'$joke'") },
            { t -> log.error("an ERROR occurred", t) }
        )

    log.debug("wait until all is done")
    processControl.tryAcquire(4_000, MILLISECONDS)
    log.debug("there you are!")
  }
}
