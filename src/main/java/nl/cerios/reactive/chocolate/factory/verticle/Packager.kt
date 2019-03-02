package nl.cerios.reactive.chocolate.factory.verticle

import io.vertx.core.json.JsonObject
import io.vertx.rxjava.core.AbstractVerticle
import nl.cerios.reactive.chocolate.factory.model.MnM
import nl.cerios.reactive.chocolate.factory.model.MnMParty
import nl.cerios.reactive.chocolate.factory.publish
import org.slf4j.LoggerFactory
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit.MILLISECONDS

class Packager : AbstractVerticle() {

  private val log = LoggerFactory.getLogger(javaClass)
  private val weight = 25
  private val timeoutMillis = 30_000L

  override fun start() {
    vertx.eventBus()
        .consumer<JsonObject>("mnm")
        .toObservable()
        .map { json -> MnM.fromJson(json.body()) }
        .window(timeoutMillis, timeoutMillis, MILLISECONDS, weight, Schedulers.computation())
        .flatMap { mnms -> mnms.toList() }
        .filter { mnms -> !mnms.isEmpty() }
        .map { mnm -> MnMParty(mnm).toJson() }
        .subscribe(
            { mnmPartyJson -> vertx.eventBus().publish("mnmParty", mnmPartyJson, log) },
            { throwable -> log.error("Error packaging mnm's.", throwable) })
  }
}