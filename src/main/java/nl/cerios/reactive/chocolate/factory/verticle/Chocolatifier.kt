package nl.cerios.reactive.chocolate.factory.verticle

import io.vertx.core.json.JsonObject
import io.vertx.rxjava.core.AbstractVerticle
import nl.cerios.reactive.chocolate.factory.chooseEnum
import nl.cerios.reactive.chocolate.factory.delayRandomly
import nl.cerios.reactive.chocolate.factory.model.ChocoNut
import nl.cerios.reactive.chocolate.factory.model.Peanut
import org.slf4j.LoggerFactory

enum class Flavor { PURE, MILK, WHITE }

class Chocolatifier : AbstractVerticle() {

  private val log = LoggerFactory.getLogger(javaClass)

  override fun start() {
    val processingMillis = config().getLong("${javaClass.name}.processingMillis")

    vertx.eventBus()
        .consumer<JsonObject>("peanut")
        .toObservable()
        .map { message -> Peanut.fromJson(message.body()) }
        .delayRandomly(processingMillis)
        .map { peanut -> ChocoNut(peanut, chooseEnum(Flavor.values())).toJson() }
        .subscribe(
            { chocoNutJson -> vertx.eventBus().publish("chocoNut", chocoNutJson) },
            { throwable -> log.error("Error making chocolate of peanuts.", throwable) })
  }
}
