package nl.cerios.reactive.chocolate.factory.verticle

import io.vertx.core.json.JsonObject
import io.vertx.rxjava.core.AbstractVerticle
import org.slf4j.LoggerFactory

class PeanutPaceMonitor : AbstractVerticle() {

  private val log = LoggerFactory.getLogger(javaClass)

  @Suppress("UnstableApiUsage")
  override fun start() {
    val paceObservable = vertx.eventBus()
        .consumer<JsonObject>("peanut.pace.set")
        .toObservable()
        .map { message -> message.body().getDouble("value") }

    vertx.eventBus()
        .consumer<JsonObject>("peanut.pace.get")
        .toObservable()
        .withLatestFrom(paceObservable) { request, pace ->
          request.reply(JsonObject().put("value", pace))
        }
        .subscribe(
            {},
            { t -> log.error("Error monitoring peanut pace.", t) })
  }
}
