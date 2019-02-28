package nl.cerios.reactive.chocolate.factory.verticle

import io.vertx.core.json.JsonObject
import io.vertx.rxjava.core.AbstractVerticle
import nl.cerios.reactive.chocolate.factory.model.MnM
import nl.cerios.reactive.chocolate.factory.model.MnMParty
import nl.cerios.reactive.chocolate.factory.timesHalfToTwo
import org.slf4j.LoggerFactory

class Packager : AbstractVerticle() {

  private val log = LoggerFactory.getLogger(javaClass)
  private val weight = 1000L

  override fun start() {
    vertx.eventBus()
        .consumer<JsonObject>("mnm")
        .toObservable()
        .map { json -> MnM.fromJson(json.body()) }
        .window(chooseWeight())
        .flatMap { mnms -> mnms.toList() }
        .map { mnm -> MnMParty(mnm).toJson() }
        .subscribe(
            { mnmPartyJson -> vertx.eventBus().publish("mnmParty", mnmPartyJson) },
            { throwable -> log.error("Error packaging mnm's.", throwable) })
  }

  private fun chooseWeight() = weight.timesHalfToTwo().toInt()
}