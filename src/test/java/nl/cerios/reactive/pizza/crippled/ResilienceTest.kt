package nl.cerios.reactive.pizza.crippled

import com.mongodb.client.MongoClient
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import nl.cerios.reactive.pizza.StorageService.convertAndStore
import nl.cerios.reactive.pizza.StorageService.getMongoClient
import nl.cerios.reactive.pizza.StorageService.getMongoCollection
import nl.cerios.reactive.pizza.crippled.FetchJokeServiceCrippled.fetchJokeCrippled
import nl.cerios.reactive.pizza.crippled.StorageServiceCrippled.getMongoClientCrippled
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

internal object ResilienceTest {

  private val log = LoggerFactory.getLogger(javaClass)

  @Test
  fun hitchhikersGuideToResilience() {
    log.debug("here we go")

    Observable
        .create<String> { emitter -> emitter.onNext(fetchJokeCrippled()) }
        .subscribeOn(Schedulers.computation())
        .subscribe(
            { log.debug(it) },
            { t -> log.error("An error occurred", t) }
        )

    log.debug("wait a second...")
    Thread.sleep(2000)

    log.debug("there you are!")
  }

  @Test
  fun run() {
    log.debug("here we go")

    val jokeRawO = Observable
        .create<String> { emitter -> emitter.onNext(fetchJokeCrippled()) }
        .subscribeOn(Schedulers.computation())

    Observable
        .create<MongoClient> { emitter -> emitter.onNext(getMongoClient()) }
        .subscribeOn(Schedulers.computation())
        .zipWith(jokeRawO,
            BiFunction { mongoClient: MongoClient, jokeRaw: String ->
              val mongoCollection = getMongoCollection(mongoClient)
              convertAndStore(jokeRaw, mongoCollection)
              log.debug("close MongoDB client")
              mongoClient.close()
            })
        .subscribe(
            {},
            { t -> log.error("An error occurred", t) }
        )

    log.debug("wait until all is done")
    Thread.sleep(3000)

    log.debug("there you are!")
  }
}
