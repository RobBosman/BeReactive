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
import java.util.concurrent.TimeUnit

internal object ResilienceTest {

  private val log = LoggerFactory.getLogger(javaClass)

  @Test
  fun hitchhikersGuideToResilience_1() {
    log.debug("here we go")
    val genie = Semaphore(0)

    Single
        .create<String> { emitter -> emitter.onSuccess(fetchJokeFlaky()) }
        .timeout(200, TimeUnit.MILLISECONDS)
        .subscribeOn(Schedulers.io())
        .doOnError { t -> log.warn("error detected: '${t.message}'") }
        .retry(3)
        .onErrorResumeNext(Single.just("fallback joke"))
        .doFinally { genie.release() }
        .subscribe(
            { joke -> log.debug("'$joke'") },
            { t -> log.error("an ERROR occurred", t) }
        )

    log.debug("wait a second...")
    genie.tryAcquire(1000, TimeUnit.MILLISECONDS)

    log.debug("there you are!")
  }

  @Test
  fun run() {
    log.debug("here we go")
    val genie = Semaphore(0)

    val jokeRawO = Single
        .create<String> { emitter -> emitter.onSuccess(fetchJokeFlaky()) }
        .timeout(200, TimeUnit.MILLISECONDS)
        .subscribeOn(Schedulers.io())
        .retry(3)

    Single
        .create<MongoClient> { emitter -> emitter.onSuccess(getMongoClientFlaky()) }
        .timeout(500, TimeUnit.MILLISECONDS)
        .subscribeOn(Schedulers.io())
        .retry(2)
        .zipWith(jokeRawO,
            BiFunction { mongoClient: MongoClient, jokeRaw: String ->
              val mongoCollection = getMongoCollection(mongoClient)
              convertAndStore(jokeRaw, mongoCollection)
              log.debug("close MongoDB client")
              mongoClient.close()
            })
        .retry(1)
        .doFinally { genie.release() }
        .subscribe(
            {},
            { t -> log.error("an ERROR occurred", t) }
        )

    log.debug("wait a second...")
    genie.tryAcquire(1000, TimeUnit.MILLISECONDS)

    log.debug("there you are!")
  }
}
