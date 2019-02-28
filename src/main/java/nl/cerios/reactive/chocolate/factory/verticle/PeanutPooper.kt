package nl.cerios.reactive.chocolate.factory.verticle

import io.vertx.core.json.JsonObject
import io.vertx.rxjava.core.AbstractVerticle
import nl.cerios.reactive.chocolate.factory.model.Peanut
import nl.cerios.reactive.chocolate.factory.timesHalfToTwo
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Subscriber

class PeanutPooper : AbstractVerticle() {

  private val log = LoggerFactory.getLogger(javaClass)
  private val maxIntervalMillis = 10_000.0

  override fun start() {
    vertx.eventBus()
        .consumer<JsonObject>("peanut.speed.set")
        .toObservable()
        .map { json -> json.body().getDouble("value") }
        .map { intensity -> intensityToIntervalMillis(intensity) }
        .switchMap { intervalMillis -> createPeanutObservable(intervalMillis) }
        .map { peanut -> peanut.toJson() }
        .subscribe(
            { peanutJson -> vertx.eventBus().publish("peanut", peanutJson) },
            { throwable -> log.error("Error producing peanuts.", throwable) })
  }

  private fun createPeanutObservable(intervalMillis: Long): Observable<out Peanut> {
    return if (intervalMillis < maxIntervalMillis) {
      Observable.create<Peanut> { createDelayedPeanut(intervalMillis, it) }
    } else {
      Observable.never<Peanut>()
    }
  }

  private fun createDelayedPeanut(intervalMillis: Long, subscriber: Subscriber<in Peanut>) {
    vertx.setTimer(intervalMillis.timesHalfToTwo()) {
      if (!subscriber.isUnsubscribed) {
        subscriber.onNext(Peanut())
        createDelayedPeanut(intervalMillis, subscriber)
      }
    }
  }

  private fun intensityToIntervalMillis(intensity: Double): Long {
    val effectiveIntensity = Math.min(Math.max(0.0, intensity), 1.0)
    return Math.round(Math.pow(Math.E, Math.log(maxIntervalMillis) * (1.0 - effectiveIntensity)))
  }
}
