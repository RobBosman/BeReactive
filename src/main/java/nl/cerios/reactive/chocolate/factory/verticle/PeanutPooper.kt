package nl.cerios.reactive.chocolate.factory.verticle

import io.vertx.core.json.JsonObject
import io.vertx.rxjava.core.AbstractVerticle
import nl.cerios.reactive.chocolate.factory.model.Peanut
import nl.cerios.reactive.chocolate.factory.publish
import nl.cerios.reactive.chocolate.factory.timesHalfToTwo
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Subscriber
import java.security.SecureRandom

class PeanutPooper : AbstractVerticle() {

  private val log = LoggerFactory.getLogger(javaClass)
  private val minIntervalMillis = 100L
  private val maxIntervalMillis = 5_000L

  override fun start() {
    vertx.eventBus()
        .consumer<JsonObject>("peanut.pace.set")
        .toObservable()
        .map { json -> json.body().getDouble("value") }
        .map { pace -> paceToIntervalMillis(pace) }
        .switchMap { intervalMillis -> createPeanutObservable(intervalMillis) }
        .map { peanut -> peanut.toJson() }
        .subscribe(
            { peanutJson -> vertx.eventBus().publish("peanut", peanutJson) },
            { throwable -> log.error("Error producing peanuts.", throwable) })
  }

  private fun paceToIntervalMillis(pace: Double): Long {
    return if (pace <= 0.0)
      Long.MAX_VALUE
    else
      (minIntervalMillis / pace).toLong().coerceIn(minIntervalMillis, maxIntervalMillis)
  }

  private fun createPeanutObservable(intervalMillis: Long): Observable<out Peanut> {
    return if (intervalMillis > maxIntervalMillis) {
      Observable.never<Peanut>()
    } else {
      Observable.create<Peanut> { subscriber -> createDelayedPeanut(intervalMillis, subscriber) }
    }
  }

  private fun createDelayedPeanut(intervalMillis: Long, subscriber: Subscriber<in Peanut>) {
    vertx.setTimer(intervalMillis.timesHalfToTwo()) {
      if (!subscriber.isUnsubscribed) {
        subscriber.onNext(Peanut(chooseQuality()))
        createDelayedPeanut(intervalMillis, subscriber)
      }
    }
  }

  private fun chooseQuality() = SecureRandom().nextInt(1_000) / 1_000.0
}
