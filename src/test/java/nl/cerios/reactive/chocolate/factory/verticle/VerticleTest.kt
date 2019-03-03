package nl.cerios.reactive.chocolate.factory.verticle

import io.vertx.core.DeploymentOptions
import io.vertx.core.json.JsonObject
import io.vertx.rxjava.core.Vertx
import io.vertx.rxjava.ext.unit.TestContext
import io.vertx.rxjava.ext.unit.TestSuite
import org.junit.jupiter.api.Test
import java.lang.reflect.Method
import java.util.*

abstract class VerticleTest(
    internal val verticleName: String,
    private val inputAddress: String,
    private val resultAddress: String) {

  open fun configureDeployment(configJson: JsonObject) {}

  abstract fun composeInputMessage(): JsonObject

  @Test
  fun doTest() {
    val configJson = JsonObject()
    configureDeployment(configJson)
    val deploymentOptions = DeploymentOptions().setConfig(configJson)

    val vertx = Vertx.vertx()
    val testSuite = TestSuite
        .create("testSuite")
        .before { testContext ->
          vertx
              .exceptionHandler(testContext.exceptionHandler())
              .deployVerticle(verticleName, deploymentOptions, testContext.asyncAssertSuccess())
        }

    Arrays.stream(javaClass.declaredMethods)
        .filter { method -> method.isAnnotationPresent(TestAsync::class.java) }
        .forEach { method ->
          testSuite.test(method.simpleName()) { testContext ->
            runTest(vertx, testContext, method)
          }
        }

    testSuite
        .run()
        .awaitSuccess()
  }

  private fun runTest(vertx: Vertx, testContext: TestContext, method: Method) {

    val testConfig = method.getAnnotation(TestAsync::class.java)

    val abortAfterMillis = testConfig.maxDurationMillis + testConfig.numInputMessages * 10L
    val abortTimerID = vertx.setTimer(abortAfterMillis) {
      testContext.fail("${method.simpleName()} took more than $abortAfterMillis ms")
    }

    val async = testContext.async(testConfig.numResultMessages)
    val messageConsumer = vertx
        .eventBus()
        .consumer<JsonObject>(resultAddress)

    messageConsumer
        .handler { message ->
          method.invoke(this, testContext, message.body())

          if (async.count() > 1) {
            async.countDown()
          } else {
            messageConsumer.unregister()
            vertx.cancelTimer(abortTimerID)
            async.complete()
          }
        }

    repeat(testConfig.numInputMessages) {
      vertx.eventBus().publish(inputAddress, composeInputMessage())
    }
  }

  private fun Method.simpleName() = "${javaClass.simpleName}.$name()"
}