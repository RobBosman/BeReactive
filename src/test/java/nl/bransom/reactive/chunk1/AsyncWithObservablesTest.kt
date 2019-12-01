package nl.bransom.reactive.chunk1

import com.mongodb.client.MongoClient
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import nl.bransom.reactive.chunk1.FetchJokeService.fetchJoke
import nl.bransom.reactive.chunk1.StorageService.convertAndStore
import nl.bransom.reactive.chunk1.StorageService.getMongoClient
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit.MILLISECONDS

internal object AsyncWithObservablesTest {

  private val log = LoggerFactory.getLogger(javaClass)

  @Test
  fun backToTheObservable_1() {
    val words = mutableListOf("hello", "world")

    val observable = Observable.fromIterable(words)

    observable
        .subscribe { word -> log.debug("subscriber 1: $word") }

    observable
        .filter { word -> word.startsWith("h") }
        .map(String::toUpperCase)
        .subscribe { word -> log.debug("subscriber 2: $word") }

    log.debug("make it better")
    words.add(1, "better") // ["hello", "better", "world"]

    observable
        .subscribe { word -> log.debug("subscriber 3: $word") }
  }

  @Test
  fun backToTheObservable_2() {
    val values = listOf(Math.E, Math.PI, Double.POSITIVE_INFINITY)
    val symbols = listOf("\u212E", "\u03C0", "\u221E")

    Observable
        .fromIterable(values)
        .zipWith(symbols) { value, symbol -> "$symbol = $value" }
        .sorted()
        .delay(100, MILLISECONDS)
        .repeat(3)
        .skipLast(2)
        .subscribe(log::debug)

    log.debug("wait a second...")
    Thread.sleep(1_000)
  }

  @Test
  fun backToTheObservable_3() {
    log.debug("here we go")

    Observable
        .just(fetchJoke())
        .subscribe { joke -> log.info("observable 1: $joke") }

    Observable
        .fromFuture(CompletableFuture.supplyAsync(::fetchJoke))
        .subscribe { joke -> log.info("observable 2: $joke") }

    Observable
        .fromFuture(CompletableFuture.supplyAsync(::fetchJoke))
        .subscribeOn(Schedulers.io())
        .subscribe { joke -> log.info("observable 3: $joke") }

    Observable
        .create<String> { emitter -> emitter.onNext(fetchJoke()) }
        .subscribeOn(Schedulers.io())
        .subscribe { joke -> log.info("observable 4: $joke") }

    log.debug("wait a second...")
    Thread.sleep(1_000)
    log.debug("there you are!")
  }

  @Test
  fun run() {
    log.debug("here we go")
    val processControl = Semaphore(0)

    val jokeJsonO = Observable
        .create<String> { emitter -> emitter.onNext(fetchJoke()) }
        .subscribeOn(Schedulers.io())

    Observable
        .create<MongoClient> { emitter -> emitter.onNext(getMongoClient()) }
        .subscribeOn(Schedulers.io())
        .zipWith(jokeJsonO,
            BiFunction { mongoClient: MongoClient, jokeJson: String ->
              val joke = convertAndStore(jokeJson, mongoClient)
              mongoClient.close()
              log.debug("closed MongoDB client")
              joke
            })
        .subscribe { joke ->
          log.info("'$joke'")
          processControl.release()
        }

    log.debug("wait until all is done")
    processControl.tryAcquire(3_000, MILLISECONDS)
    log.debug("there you are!")
  }
}
