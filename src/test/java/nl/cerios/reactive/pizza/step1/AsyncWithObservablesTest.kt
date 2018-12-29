package nl.cerios.reactive.pizza.step1

import com.mongodb.client.MongoClient
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import nl.cerios.reactive.pizza.step1.FetchJokeService.fetchJoke
import nl.cerios.reactive.pizza.step1.StorageService.convertAndStore
import nl.cerios.reactive.pizza.step1.StorageService.getMongoClient
import nl.cerios.reactive.pizza.step1.StorageService.getMongoCollection
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

internal object AsyncWithObservablesTest {

  private val log = LoggerFactory.getLogger(javaClass)

  @Test
  fun hitchhikersGuideToTheObservable_1() {
    val words = mutableListOf("hello", "world")

    val observable = Observable.fromIterable(words)

    observable
        .subscribe { it -> log.debug("subscriber 1: $it") }

    observable
        .map(String::toUpperCase)
        .filter { it.startsWith("H") }
        .subscribe { log.debug("subscriber 2: $it") }

    log.debug("make it better")
    words.add(1, "better")

    observable
        .subscribe { log.debug("subscriber 3: $it") }
  }

  @Test
  fun hitchhikersGuideToTheObservable_2() {
    val symbols = listOf("\u212E", "\u03C0", "\u221E")
    val values = listOf(Math.E, Math.PI, Double.POSITIVE_INFINITY)
    Observable
        .fromIterable(symbols)
        .zipWith(values) { symbol, value -> "$symbol = $value" }
        .sorted()
        .delay(100, TimeUnit.MILLISECONDS)
        .repeat(3)
        .skipLast(2)
        .subscribe(log::debug)

    log.debug("wait a second...")
    Thread.sleep(1_000)
  }

  @Test
  fun hitchhikersGuideToTheObservable_3() {
    log.debug("here we go")

    when (4) {
      1 -> {
        Observable
            .just(fetchJoke())
            .subscribe(log::debug)
      }
      2 -> {
        Observable
            .fromFuture(CompletableFuture.supplyAsync(::fetchJoke))
            .subscribe(log::debug)
      }
      3 -> {
        Observable
            .fromFuture(CompletableFuture.supplyAsync(::fetchJoke))
            .subscribeOn(Schedulers.io())
            .subscribe(log::debug)
      }
      4 -> {
        Observable
            .create<String> { emitter -> emitter.onNext(fetchJoke()) }
            .subscribeOn(Schedulers.io())
            .subscribe(log::debug)
      }
    }

    log.debug("wait a second...")
    Thread.sleep(1_000)

    log.debug("there you are!")
  }

  @Test
  fun run() {
    log.debug("here we go")
    val yokeControl = Semaphore(0)

    val jokeRawO = Observable
        .create<String> { emitter -> emitter.onNext(fetchJoke()) }
        .subscribeOn(Schedulers.io())

    Observable
        .create<MongoClient> { emitter -> emitter.onNext(getMongoClient()) }
        .subscribeOn(Schedulers.io())
        .zipWith(jokeRawO,
            BiFunction { mongoClient: MongoClient, jokeRaw: String ->
              val mongoCollection = getMongoCollection(mongoClient)
              convertAndStore(jokeRaw, mongoCollection)
              log.debug("close MongoDB client")
              mongoClient.close()
            })
        .doFinally { yokeControl.release() }
        .subscribe()

    log.debug("wait until all is done")
    yokeControl.tryAcquire(3_000, TimeUnit.MILLISECONDS)

    log.debug("there you are!")
  }
}
