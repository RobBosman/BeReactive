package nl.cerios.reactive.chocolate.factory.verticle

import io.vertx.core.json.JsonObject
import io.vertx.rxjava.core.AbstractVerticle
import nl.cerios.reactive.chocolate.factory.delayRandomized
import nl.cerios.reactive.chocolate.factory.model.ChocoNut
import nl.cerios.reactive.chocolate.factory.model.Peanut
import org.slf4j.LoggerFactory
import java.security.SecureRandom

enum class Flavor { PURE, MILK, WHITE }

class Chocolatifier : AbstractVerticle() {

  private val log = LoggerFactory.getLogger(javaClass)

  override fun start() {
    val processingMillis = config().getLong("${javaClass.name}.processingMillis")

    vertx.eventBus()
        .consumer<JsonObject>("peanut")
        .toObservable()
        .map { json -> Peanut.fromJson(json.body()) }
        .delayRandomized(processingMillis)
        .map { peanut -> ChocoNut(peanut, chooseFlavor()).toJson() }
        .subscribe(
            { chocoNutJson -> vertx.eventBus().publish("chocoNut", chocoNutJson) },
            { throwable -> log.error("Error making chocolate of peanuts.", throwable) })
  }

  private fun chooseFlavor() = Flavor.values()[SecureRandom().nextInt(Flavor.values().size)]
}