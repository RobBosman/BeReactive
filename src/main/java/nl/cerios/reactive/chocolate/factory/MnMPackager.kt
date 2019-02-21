package nl.cerios.reactive.chocolate.factory

import io.vertx.core.json.JsonObject
import io.vertx.rxjava.core.AbstractVerticle
import org.slf4j.LoggerFactory

class MnMPackager : AbstractVerticle() {

  private val log = LoggerFactory.getLogger(javaClass)
  private val weight = 1000

  override fun start() {
    vertx.eventBus()
        .consumer<JsonObject>("mnm")
        .toObservable()
        .window(weight)
        .map<JsonObject> { MnMParty(weight).toJson() }
        .subscribe(
            { mnmJson -> vertx.eventBus().publish("mnmparty", mnmJson) },
            { throwable -> log.error("Error packaging mnm's.", throwable) })
  }
}