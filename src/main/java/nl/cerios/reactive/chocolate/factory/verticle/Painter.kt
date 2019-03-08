package nl.cerios.reactive.chocolate.factory.verticle

import io.vertx.core.json.JsonObject
import io.vertx.rxjava.core.AbstractVerticle
import nl.cerios.reactive.chocolate.factory.delayAndNotifyConsumption
import nl.cerios.reactive.chocolate.factory.model.ChocoNut
import nl.cerios.reactive.chocolate.factory.model.ColorNut
import nl.cerios.reactive.chocolate.factory.pickEnum
import org.slf4j.LoggerFactory

enum class Color { GREEN, YELLOW, BLUE, RED, ORANGE }

class Painter : AbstractVerticle() {

  private val log = LoggerFactory.getLogger(javaClass)

  override fun start() {
    val processingMillis = config().getLong("${javaClass.name}.processingMillis")

    vertx.eventBus()
        .consumer<JsonObject>("chocoNut.produced")
        .toObservable()
        .delayAndNotifyConsumption(vertx, 2_000, processingMillis)
        .map { message -> ChocoNut.fromJson(message.body()) }
        .map { chocoNut -> ColorNut(chocoNut, pickEnum(Color.values())).toJson() }
        .subscribe(
            { chocoNutJson -> vertx.eventBus().publish("colorNut.produced", chocoNutJson) },
            { throwable -> log.error("Error painting chocoNuts.", throwable) })
  }
}
