package nl.cerios.reactive.chocolate.factory.verticle

import io.vertx.core.json.JsonObject
import io.vertx.rxjava.core.AbstractVerticle
import nl.cerios.reactive.chocolate.factory.model.ColorNut
import nl.cerios.reactive.chocolate.factory.model.MnM
import nl.cerios.reactive.chocolate.factory.timesHalfToTwo
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit.MILLISECONDS

class LetterStamper : AbstractVerticle() {

  private val log = LoggerFactory.getLogger(javaClass)
  private val processingTime = 500L

  override fun start() {
    vertx.eventBus()
        .consumer<JsonObject>("colorNut")
        .toObservable()
        .map { json -> ColorNut.fromJson(json.body()) }
        .delay(processingTime.timesHalfToTwo(), MILLISECONDS)
        .map { colorNut -> MnM(colorNut).toJson() }
        .subscribe(
            { mnmJson -> vertx.eventBus().publish("mnm", mnmJson) },
            { throwable -> log.error("Error stamping colorNuts.", throwable) })
  }
}