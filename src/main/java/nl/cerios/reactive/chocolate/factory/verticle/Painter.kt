package nl.cerios.reactive.chocolate.factory.verticle

import io.vertx.core.json.JsonObject
import io.vertx.rxjava.core.AbstractVerticle
import nl.cerios.reactive.chocolate.factory.chooseEnum
import nl.cerios.reactive.chocolate.factory.delayRandomly
import nl.cerios.reactive.chocolate.factory.model.ChocoNut
import nl.cerios.reactive.chocolate.factory.model.ColorNut
import org.slf4j.LoggerFactory

enum class Color { GREEN, YELLOW, BLUE, RED, ORANGE }

class Painter : AbstractVerticle() {

  private val log = LoggerFactory.getLogger(javaClass)

  override fun start() {
    val processingMillis = config().getLong("${javaClass.name}.processingMillis")

    vertx.eventBus()
        .consumer<JsonObject>("chocoNut")
        .toObservable()
        .map { message -> ChocoNut.fromJson(message.body()) }
        .delayRandomly(processingMillis)
        .map { chocoNut -> ColorNut(chocoNut, chooseEnum(Color.values())).toJson() }
        .subscribe(
            { chocoNutJson -> vertx.eventBus().publish("colorNut", chocoNutJson) },
            { throwable -> log.error("Error painting chocoNuts.", throwable) })
  }
}