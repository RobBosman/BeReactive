package nl.cerios.reactive.pizza.step2

import com.mongodb.client.MongoClient
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import nl.cerios.reactive.pizza.step1.StorageService.convertAndStore
import nl.cerios.reactive.pizza.step2.FetchJokeServiceFlaky.fetchJokeFlaky
import nl.cerios.reactive.pizza.step2.StorageServiceFlaky.getMongoClientFlaky
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
        .doOnEvent { jokeRaw, _ -> if (!jokeRaw.contains("success")) throw Exception("invalid data: $jokeRaw") }
        .timeout(200, MILLISECONDS)
        .subscribeOn(Schedulers.io())
        .doOnError { t -> log.warn("error detected: '${t.message}'") }
        .retry(3)
        .onErrorResumeNext(Single.just("fallback joke"))
        .doFinally { processControl.release() }
        .subscribe(
            { jokeRaw -> log.info("'$jokeRaw'") },
            { t -> log.error("an ERROR occurred", t) }
        )

    log.debug("wait a second...")
    processControl.tryAcquire(1_000, MILLISECONDS)
    log.debug("there you are!")
  }

  @Test
  fun run() {
    log.debug("here we go")
    val processControl = Semaphore(0)

    val jokeRawO = Single
        .create<String> { it.onSuccess(fetchJokeFlaky()) }
        .doOnEvent { jokeRaw, _ -> if (!jokeRaw.contains("success")) throw Exception("invalid data: $jokeRaw") }
        .timeout(200, MILLISECONDS)
        .subscribeOn(Schedulers.io())
        .retry(3)
        .onErrorResumeNext(Single.just("""{ "type": "success", "value": { "joke": "fallback joke" } }"""))

    Single
        .create<MongoClient> { it.onSuccess(getMongoClientFlaky()) }
        .timeout(1_000, MILLISECONDS)
        .subscribeOn(Schedulers.io())
        .retry(2)
        .zipWith(jokeRawO,
            BiFunction { mongoClient: MongoClient, jokeRaw: String ->
              val joke = convertAndStore(jokeRaw, mongoClient)
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
