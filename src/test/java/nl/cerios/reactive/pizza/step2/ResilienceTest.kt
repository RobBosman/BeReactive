package nl.cerios.reactive.pizza.step2

import com.mongodb.client.MongoClient
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import nl.cerios.reactive.pizza.step1.StorageService.convertAndStore
import nl.cerios.reactive.pizza.step1.StorageService.getMongoCollection
import nl.cerios.reactive.pizza.step2.FetchJokeServiceFlaky.fetchJokeFlaky
import nl.cerios.reactive.pizza.step2.StorageServiceFlaky.getMongoClientFlaky
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit.MILLISECONDS

internal object ResilienceTest {

  private val log = LoggerFactory.getLogger(javaClass)

  @Test
  fun hitchhikersGuideToResilience_1() {
    log.debug("here we go")
    val processControl = Semaphore(0)

    Single
        .create<String> { emitter -> emitter.onSuccess(fetchJokeFlaky()) }
        .doOnEvent { s, _ -> if (s == "ERROR") throw Exception("invalid data: $s") }
        .timeout(200, MILLISECONDS)
        .subscribeOn(Schedulers.io())
        .doOnError { t -> log.warn("error detected: '${t.message}'") }
        .retry(3)
        .onErrorResumeNext(Single.just("fallback joke"))
        .doFinally { processControl.release() }
        .subscribe(
            { joke -> log.info("'$joke'") },
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
        .create<String> { emitter -> emitter.onSuccess(fetchJokeFlaky()) }
        .doOnEvent { s, _ -> if (s == "ERROR") throw Exception("invalid data: $s") }
        .timeout(200, MILLISECONDS)
        .subscribeOn(Schedulers.io())
        .retry(3)
        .onErrorResumeNext(Single.just("""{ "type": "success", "value": { "joke": "fallback joke" } }"""))

    Single
        .create<MongoClient> { emitter -> emitter.onSuccess(getMongoClientFlaky()) }
        .timeout(2_000, MILLISECONDS)
        .subscribeOn(Schedulers.io())
        .retry(2)
        .zipWith(jokeRawO,
            BiFunction { mongoClient: MongoClient, jokeRaw: String ->
              val mongoCollection = getMongoCollection(mongoClient)
              val joke = convertAndStore(jokeRaw, mongoCollection)
              log.debug("close MongoDB client")
              mongoClient.close()
              joke
            })
        .doFinally { processControl.release() }
        .subscribe(
            { joke -> log.info("'$joke'") },
            { t -> log.error("an ERROR occurred", t) }
        )

    log.debug("wait until all is done")
    processControl.tryAcquire(10_000, MILLISECONDS)
    log.debug("there you are!")
  }
}
