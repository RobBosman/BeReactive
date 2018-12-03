package nl.cerios.reactive.pizza

import com.mongodb.client.MongoClient
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import nl.cerios.reactive.pizza.FetchJokeService.fetchJoke
import nl.cerios.reactive.pizza.StorageService.getMongoClient
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit


internal object AsyncWithObservablesTest {

  private val log = LoggerFactory.getLogger(javaClass)

  @Test
  fun hitchhikersGuideToTheObservable_1() {
    val words = mutableListOf("hello", "world")
    val observable = Observable.fromIterable(words)

    observable
        .subscribe { log.debug("subscriber 1: $it") }

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
    Observable.fromIterable(symbols)
        .zipWith(values) { symbol, value -> "$symbol = $value" }
        .sorted()
        .delay(100, TimeUnit.MILLISECONDS)
        .repeat(3)
        .skipLast(2)
        .subscribe(log::debug)

    log.debug("wait a second...")
    Thread.sleep(1000)
  }

  @Test
  fun run() {
    log.debug("here we go")

//    val fetchO = Observable.just(fetchJoke())
//    val fetchO = Observable.fromFuture(CompletableFuture.supplyAsync(::fetchJoke))
//    val fetchO = Observable
//        .create<CompletableFuture<String>> { emitter ->
//          emitter.onNext(CompletableFuture.supplyAsync(::fetchJoke))
//          emitter.onComplete()
//        }
//        .map { f -> f.get() }
//    fetchO.subscribe(log::debug)
//    fetchO.subscribe(log::debug)


    val jokeRawO = Observable
        .create<Future<String>> { emitter ->
          emitter.onNext(CompletableFuture.supplyAsync(::fetchJoke))
        }

    Observable
        .create<Future<MongoClient>> { emitter ->
          emitter.onNext(CompletableFuture.supplyAsync(::getMongoClient))
        }
        .zipWith(jokeRawO,
            BiFunction { mongoClientF: Future<MongoClient>, jokeRawF: Future<String> ->
              CompletableFuture.supplyAsync {
                log.debug("wait for tasks to complete")
                val mongoClient = mongoClientF.get()
                val jokeRaw = jokeRawF.get()

                val mongoCollection = StorageService.getMongoCollection(mongoClient)
                StorageService.convertAndStore(jokeRaw, mongoCollection)

                log.debug("close MongoDB client")
                mongoClient.close()
              }
            })
        .subscribe()

    log.debug("wait until all is done")
    Thread.sleep(3000)

    log.debug("there you are!")
  }
}