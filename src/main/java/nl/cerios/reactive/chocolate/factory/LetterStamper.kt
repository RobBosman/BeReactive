package nl.cerios.reactive.chocolate.factory

import io.vertx.core.json.JsonObject
import io.vertx.rxjava.core.AbstractVerticle
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit.MILLISECONDS

class LetterStamper : AbstractVerticle() {

  private val log = LoggerFactory.getLogger(javaClass)
  private val processingTime = 500L

  override fun start() {
    vertx.eventBus()
        .consumer<JsonObject>("colornut")
        .toObservable()
        .delay(processingTime.timesHalfToTwo(), MILLISECONDS)
        .map<Color> { Color.valueOf(it.body().getString("color")) }
        .map<JsonObject> { MnM(it).toJson() }
        .subscribe(
            { mnmJson -> vertx.eventBus().publish("mnm", mnmJson) },
            { throwable -> log.error("Error stamping colornut.", throwable) })
  }
}