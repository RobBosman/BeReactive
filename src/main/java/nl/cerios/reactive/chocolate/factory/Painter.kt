package nl.cerios.reactive.chocolate.factory

import io.vertx.core.json.JsonObject
import io.vertx.rxjava.core.AbstractVerticle
import org.slf4j.LoggerFactory
import java.security.SecureRandom
import java.util.concurrent.TimeUnit.MILLISECONDS

enum class Color { GREEN, YELLOW, BLUE, RED, ORANGE }

class Painter : AbstractVerticle() {

  private val log = LoggerFactory.getLogger(javaClass)
  private val processingTime = 1000L

  override fun start() {
    vertx.eventBus()
        .consumer<JsonObject>("choconut")
        .toObservable()
        .delay(processingTime.timesHalfToTwo(), MILLISECONDS)
        .map<Color> { Color.values()[SecureRandom().nextInt(Color.values().size)] }
        .map<JsonObject> { Colornut(it).toJson() }
        .subscribe(
            { choconutJson -> vertx.eventBus().publish("colornut", choconutJson) },
            { throwable -> log.error("Error painting choconut.", throwable) })
  }
}