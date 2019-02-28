package nl.cerios.reactive.chocolate.factory.verticle

import io.vertx.core.json.JsonObject
import io.vertx.rxjava.core.AbstractVerticle
import nl.cerios.reactive.chocolate.factory.model.ChocoNut
import nl.cerios.reactive.chocolate.factory.model.Peanut
import nl.cerios.reactive.chocolate.factory.timesHalfToTwo
import org.slf4j.LoggerFactory
import java.security.SecureRandom
import java.util.concurrent.TimeUnit.MILLISECONDS

enum class Flavor { PURE, MILK, WHITE }

class Chocolatifier : AbstractVerticle() {

  private val log = LoggerFactory.getLogger(javaClass)
  private val processingTime = 2000L

  override fun start() {
    vertx.eventBus()
        .consumer<JsonObject>("peanut")
        .toObservable()
        .map { json -> Peanut.fromJson(json.body()) }
        .delay(processingTime.timesHalfToTwo(), MILLISECONDS)
        .map { peanut -> ChocoNut(peanut, chooseFlavor()).toJson() }
        .subscribe(
            { chocoNutJson -> vertx.eventBus().publish("chocoNut", chocoNutJson) },
            { throwable -> log.error("Error making chocolate of peanuts.", throwable) })
  }

  private fun chooseFlavor() = Flavor.values()[SecureRandom().nextInt(Flavor.values().size)]
}