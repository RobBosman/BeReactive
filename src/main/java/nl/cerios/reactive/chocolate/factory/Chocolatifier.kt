package nl.cerios.reactive.chocolate.factory

import io.vertx.core.json.JsonObject
import io.vertx.rxjava.core.AbstractVerticle
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit.MILLISECONDS

class Chocolatifier : AbstractVerticle() {

  private val log = LoggerFactory.getLogger(javaClass)
  private val processingTime = 2000L

  override fun start() {
    vertx.eventBus()
        .consumer<JsonObject>("peanut")
        .toObservable()
        .delay(processingTime.timesHalfToTwo(), MILLISECONDS)
        .map<JsonObject> { Choconut().toJson() }
        .subscribe(
            { choconutJson -> vertx.eventBus().publish("choconut", choconutJson) },
            { throwable -> log.error("Error makingchocolate of peanut.", throwable) })
  }
}