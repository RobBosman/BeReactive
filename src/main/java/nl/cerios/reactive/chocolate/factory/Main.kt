package nl.cerios.reactive.chocolate.factory

import io.vertx.core.AsyncResult
import io.vertx.core.CompositeFuture
import io.vertx.core.Future
import io.vertx.rxjava.core.Vertx
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("nl.cerios.reactive.chocolate.factory.Main")

fun main() {
  val vertx = Vertx.vertx()

  val firstBatch = CompositeFuture.all(
      deployVerticle(vertx, PeanutPooper::class.java.name),
      deployVerticle(vertx, Chocolatifier::class.java.name),
      deployVerticle(vertx, Painter::class.java.name),
      deployVerticle(vertx, LetterStamper::class.java.name),
      deployVerticle(vertx, MnMPackager::class.java.name))

  val secondBatch = CompositeFuture.all(
      deployVerticle(vertx, PeanutSpeedLogger::class.java.name),
      deployVerticle(vertx, HttpEventServer::class.java.name))

  CompositeFuture
      .all(firstBatch, secondBatch)
      .setHandler { result: AsyncResult<CompositeFuture> ->
        if (result.succeeded()) {
          log.info("We have hyperdrive, captain.")
        } else {
          log.error("Error", result.cause())
        }
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
