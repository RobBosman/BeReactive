package nl.cerios.reactive.chocolate.factory

import io.vertx.core.CompositeFuture
import io.vertx.core.Future
import io.vertx.rxjava.core.Vertx
import nl.cerios.reactive.chocolate.factory.verticle.*
import org.slf4j.LoggerFactory
import java.security.SecureRandom

private val log = LoggerFactory.getLogger("nl.cerios.reactive.chocolate.factory.Main")

fun main() {
  val vertx = Vertx.vertx()

  CompositeFuture
      .all(
          CompositeFuture.all(
              deployVerticle(vertx, PeanutPooper::class.java.name),
              deployVerticle(vertx, Chocolatifier::class.java.name),
              deployVerticle(vertx, Painter::class.java.name),
              deployVerticle(vertx, LetterStamper::class.java.name),
              deployVerticle(vertx, Packager::class.java.name)),
          CompositeFuture.all(
              deployVerticle(vertx, PeanutSpeedLogger::class.java.name),
              deployVerticle(vertx, HttpEventServer::class.java.name))
      )
      .setHandler { result ->
        if (result.succeeded()) {
          log.info("We have hyperdrive, captain.")
        } else {
          log.error("Error", result.cause())
        }
      }

  // NOTE - do not log (and swallow!) messages that are 'sent'; only log messages that are 'published'!
  vertx
      .eventBus()
      .addOutboundInterceptor<Any> { context -> log.debug("EVENT '${context.message().address()}' = '${context.message().body()}'") }

  vertx.setTimer(30000) {
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

fun Long.timesHalfToTwo() = Math.max(1, Math.round(this * (0.5 + 1.5 * SecureRandom().nextDouble())))
