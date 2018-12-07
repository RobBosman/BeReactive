package nl.cerios.reactive.pizza

import com.mongodb.client.MongoClient
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import nl.cerios.reactive.pizza.FetchJokeServiceFlaky.fetchJokeFlaky
import nl.cerios.reactive.pizza.StorageService.convertAndStore
import nl.cerios.reactive.pizza.StorageService.getMongoCollection
import nl.cerios.reactive.pizza.StorageServiceFlaky.getMongoClientFlaky
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

internal object ResilienceTest {

  private val log = LoggerFactory.getLogger(javaClass)

  @Test
  fun hitchhikersGuideToResilience() {
    log.debug("here we go")

    Observable
        .create<String> { emitter -> emitter.onNext(fetchJokeFlaky()) }
        .retry(3)
        .subscribeOn(Schedulers.computation())
        .subscribe(
            { log.debug(it) },
            { t -> log.error("an ERROR occurred", t) }
        )

    log.debug("wait a second...")
    Thread.sleep(2000)

    log.debug("there you are!")
  }

  @Test
  fun run() {
    log.debug("here we go")

    val jokeRawO = Observable
        .create<String> { emitter -> emitter.onNext(fetchJokeFlaky()) }
        .subscribeOn(Schedulers.computation())
        .retry(3)

    Observable
        .create<MongoClient> { emitter -> emitter.onNext(getMongoClientFlaky()) }
        .subscribeOn(Schedulers.computation())
        .retry(2)
        .zipWith(jokeRawO,
            BiFunction { mongoClient: MongoClient, jokeRaw: String ->
              val mongoCollection = getMongoCollection(mongoClient)
              convertAndStore(jokeRaw, mongoCollection)
              log.debug("close MongoDB client")
              mongoClient.close()
            })
        .retry(1)
        .subscribe(
            {},
            { t -> log.error("an ERROR occurred", t) }
        )

    log.debug("wait until all is done")
    Thread.sleep(3000)

    log.debug("there you are!")
  }
}
