package nl.cerios.reactive.chocolate.factory.verticle

import io.vertx.core.json.JsonObject
import io.vertx.rxjava.core.AbstractVerticle
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit.MILLISECONDS

class PeanutPaceLogger : AbstractVerticle() {

  private val log = LoggerFactory.getLogger(javaClass)

  override fun start() {
    vertx.eventBus()
        .consumer<JsonObject>("peanut.pace.set")
        .toObservable()
        .debounce(200, MILLISECONDS)
        .map { message -> Math.floor(message.body().getDouble("value") * 100.0) }
        .subscribe { percentage -> log.debug("Peanut production pace: $percentage%") }
  }
}
