package nl.cerios.reactive.chocolate.factory.verticle

import io.vertx.core.json.JsonObject
import io.vertx.rxjava.core.AbstractVerticle
import nl.cerios.reactive.chocolate.factory.logIt
import nl.cerios.reactive.chocolate.factory.model.Peanut
import nl.cerios.reactive.chocolate.factory.timesHalfToTwo
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Subscriber
import java.util.UUID.randomUUID
import kotlin.random.Random

class PeanutPooper : AbstractVerticle() {

  private val log = LoggerFactory.getLogger(javaClass)

  override fun start() {
    val minIntervalMillis = config().getLong("${javaClass.name}.minIntervalMillis")
    val maxIntervalMillis = config().getLong("${javaClass.name}.maxIntervalMillis")

    vertx.eventBus()
        .consumer<JsonObject>("peanut.pace.set")
        .toObservable()
        .map { message -> message.body().getDouble("value") }
        .map { pace -> paceToIntervalMillis(pace, minIntervalMillis..maxIntervalMillis) }
        .switchMap { averageIntervalMillis -> createPeanutObservable(averageIntervalMillis) }
        .map { peanut -> peanut.toJson() }
        .logIt(log, "consumed")
        .subscribe(
            { peanutJson -> vertx.eventBus().publish("peanut.produced", peanutJson) },
            { throwable -> log.error("Error producing peanuts.", throwable) })
  }

  private fun paceToIntervalMillis(pace: Double, intervalMillisRange: LongRange): Long {
    return if (pace <= 0.0)
      Long.MAX_VALUE
    else {
      (intervalMillisRange.first / pace).toLong().coerceIn(intervalMillisRange)
    }
  }

  private fun createPeanutObservable(averageIntervalMillis: Long): Observable<out Peanut> =
      Observable.create<Peanut> { subscriber -> createDelayedPeanut(averageIntervalMillis, subscriber) }

  private fun createDelayedPeanut(averageIntervalMillis: Long, subscriber: Subscriber<in Peanut>) {
    vertx.setTimer(averageIntervalMillis.timesHalfToTwo()) {
      if (!subscriber.isUnsubscribed) {
        subscriber.onNext(createPeanut())
        createDelayedPeanut(averageIntervalMillis, subscriber)
      }
    }
  }

  private fun createPeanut() = Peanut(randomUUID(), Random.nextInt(1_000) / 1_000.0)
}
