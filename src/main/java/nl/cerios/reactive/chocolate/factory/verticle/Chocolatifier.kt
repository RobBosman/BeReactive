package nl.cerios.reactive.chocolate.factory.verticle

import io.vertx.core.json.JsonObject
import io.vertx.rxjava.core.AbstractVerticle
import nl.cerios.reactive.chocolate.factory.delayRandomly
import nl.cerios.reactive.chocolate.factory.model.ChocoNut
import nl.cerios.reactive.chocolate.factory.model.Peanut
import nl.cerios.reactive.chocolate.factory.pickEnum
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit.MILLISECONDS

enum class Flavor { PURE, MILK, WHITE }

class Chocolatifier : AbstractVerticle() {

  private val log = LoggerFactory.getLogger(javaClass)

  override fun start() {
    val processingMillis = config().getLong("${javaClass.name}.processingMillis")

    vertx.eventBus()
        .consumer<JsonObject>("peanut.produced")
        .toObservable()
        .map { message -> Peanut.fromJson(message.body()) }
        .delay(2_000, MILLISECONDS)
        .doOnNext { peanut -> vertx.eventBus().publish("peanut.consumed", JsonObject().put("id", peanut.id.toString())) }
        .delayRandomly(processingMillis)
        .map { peanut -> ChocoNut(peanut, pickEnum(Flavor.values())).toJson() }
        .subscribe(
            { chocoNutJson -> vertx.eventBus().publish("chocoNut.produced", chocoNutJson) },
            { throwable -> log.error("Error making chocolate of peanuts.", throwable) })
  }
}
