package nl.cerios.reactive.chocolate.factory

import io.vertx.core.CompositeFuture
import io.vertx.core.Future
import io.vertx.core.VertxOptions
import io.vertx.core.json.JsonObject
import io.vertx.rxjava.core.Vertx
import io.vertx.rxjava.core.eventbus.EventBus
import io.vertx.rxjava.core.eventbus.Message
import io.vertx.rxjava.core.eventbus.MessageConsumer
import nl.cerios.reactive.chocolate.factory.verticle.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rx.Observable
import java.security.SecureRandom

private val log = LoggerFactory.getLogger("nl.cerios.reactive.chocolate.factory.Main")
private const val TIMEOUT_MILLIS = 1_000 * 60 * 3L

fun main() {

  val vertx = Vertx.vertx(VertxOptions()
      .setBlockedThreadCheckInterval(if (log.isDebugEnabled) TIMEOUT_MILLIS else 2_000L))

  CompositeFuture
      .all(
          CompositeFuture.all(
              deployVerticle(vertx, PeanutPooper::class.java.name),
              deployVerticle(vertx, Chocolatifier::class.java.name),
              deployVerticle(vertx, Painter::class.java.name),
              deployVerticle(vertx, LetterStamper::class.java.name),
              deployVerticle(vertx, Packager::class.java.name)
          ),
          CompositeFuture.all(
              deployVerticle(vertx, PeanutPaceLogger::class.java.name),
              deployVerticle(vertx, HttpEventServer::class.java.name)
          )
      )
      .setHandler { result ->
        if (result.succeeded()) {
          log.info("We have hyperdrive, captain.")
//          vertx.eventBus().publish("peanut.pace.set", JsonObject().put("value", 0.20))
        } else {
          log.error("Error", result.cause())
        }
      }

  vertx.setTimer(TIMEOUT_MILLIS) {
    vertx.close()
    log.info("And... it's gone!")
    System.exit(0)
  }
}

private fun deployVerticle(vertx: Vertx, verticleName: String): Future<Void> {
  val result = Future.future<Void>()
  vertx.deployVerticle(verticleName) { deployResult ->
    if (deployResult.succeeded()) {
      result.complete()
    } else {
      result.fail(deployResult.cause())
    }
  }
  return result
}

fun Long.timesHalfToTwo() = Math.round(this * (0.5 + 1.5 * SecureRandom().nextDouble()))

fun EventBus.publish(address: String, messageBody: JsonObject, log: Logger) {
  log.debug("<$address> = $messageBody")
  publish(address, messageBody)
}

fun <T> MessageConsumer<T>.toObservable(log: Logger): Observable<Message<T>> =
    toObservable()
        .doOnNext { msg -> log.debug("<${msg.address()}> = ${msg.body()}") }
