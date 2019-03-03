package nl.cerios.reactive.chocolate.factory.verticle

import io.vertx.core.DeploymentOptions
import io.vertx.core.json.JsonObject
import io.vertx.rxjava.core.Vertx
import io.vertx.rxjava.core.eventbus.EventBus
import io.vertx.rxjava.ext.unit.TestContext
import io.vertx.rxjava.ext.unit.TestSuite
import nl.cerios.reactive.chocolate.factory.model.MnM
import nl.cerios.reactive.chocolate.factory.model.TestHelper.createMnMs
import org.junit.jupiter.api.Test

internal class PackagerTest {

  private val verticleName = Packager::class.java.name
  private val defaultNumMnMs = 10
  private val packagerTimeoutMillis = 1_000L
  private val maxTestProcessingMillis = 100L

  @Test
  fun test() {
    val deploymentOptions = DeploymentOptions()
        .setConfig(JsonObject()
            .put("$verticleName.numMnMs", defaultNumMnMs)
            .put("$verticleName.collectAfterMillis", packagerTimeoutMillis)
        )

    val vertx = Vertx.vertx()
    TestSuite
        .create("testSuite")
        .before { testContext ->
          vertx
              .exceptionHandler(testContext.exceptionHandler())
              .deployVerticle(verticleName, deploymentOptions, testContext.asyncAssertSuccess())
        }
        .test("testLessMnMs") { testContext -> testLessMnMs(vertx, testContext) }
        .test("testDefaultMnMs") { testContext -> testDefaultMnMs(vertx, testContext) }
        .test("testMoreMnMs") { testContext -> testMoreMnMs(vertx, testContext) }
        .run()
        .awaitSuccess()
  }

  private fun testDefaultMnMs(vertx: Vertx, testContext: TestContext) {
    val abortTimerID = vertx.setTimer(maxTestProcessingMillis) {
      testContext.fail("This took too long")
    }

    val async = testContext.async()
    val messageConsumer = vertx
        .eventBus()
        .consumer<JsonObject>("mnmParty")

    messageConsumer
        .handler { message ->
          val content = message.body().getInteger("numMnMs")
          testContext.assertEquals(defaultNumMnMs, content)

          messageConsumer.unregister()
          vertx.cancelTimer(abortTimerID)
          async.complete()
        }

    publishMnMs(vertx.eventBus(), createMnMs(defaultNumMnMs))
  }

  private fun testLessMnMs(vertx: Vertx, testContext: TestContext) {
    val abortTimerID = vertx.setTimer(packagerTimeoutMillis + maxTestProcessingMillis) {
      testContext.fail("This took too long")
    }

    val async = testContext.async()
    val messageConsumer = vertx
        .eventBus()
        .consumer<JsonObject>("mnmParty")

    val actualNumMnMs = defaultNumMnMs - 1
    messageConsumer
        .handler { message ->
          val content = message.body().getInteger("numMnMs")
          testContext.assertEquals(actualNumMnMs, content)

          messageConsumer.unregister()
          vertx.cancelTimer(abortTimerID)
          async.complete()
        }

    vertx.setTimer(maxTestProcessingMillis) {
      testContext.assertFalse(async.isCompleted)
    }

    publishMnMs(vertx.eventBus(), createMnMs(actualNumMnMs))
  }

  private fun testMoreMnMs(vertx: Vertx, testContext: TestContext) {
    val abortTimerID = vertx.setTimer(packagerTimeoutMillis * 2) {
      testContext.fail("This took too long")
    }

    val async = testContext.async(2)
    val messageConsumer = vertx
        .eventBus()
        .consumer<JsonObject>("mnmParty")

    val actualNumMnMs = defaultNumMnMs + 1
    messageConsumer
        .handler { message ->
          val content = message.body().getInteger("numMnMs")

          if (async.count() > 1) {
            testContext.assertEquals(defaultNumMnMs, content)
            async.countDown()
          } else {
            testContext.assertEquals(actualNumMnMs % defaultNumMnMs, content)

            messageConsumer.unregister()
            vertx.cancelTimer(abortTimerID)
            async.complete()
          }
        }

    publishMnMs(vertx.eventBus(), createMnMs(actualNumMnMs))
  }

  private fun publishMnMs(eventBus: EventBus, mnms: List<MnM>) =
      mnms.forEach { mnm -> eventBus.publish("mnm", mnm.toJson()) }
}