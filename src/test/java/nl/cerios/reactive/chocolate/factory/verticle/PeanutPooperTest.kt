package nl.cerios.reactive.chocolate.factory.verticle

import io.vertx.core.json.JsonObject
import io.vertx.rxjava.ext.unit.TestContext

internal object PeanutPooperTest : VerticleTest(
    PeanutPooper::class.java.name,
    "peanut.pace.set",
    "peanut") {

  private const val maxIntervalMillis = 100L

  override fun configureDeployment(configJson: JsonObject) {
    configJson.put("$verticleName.minIntervalMillis", 10L)
    configJson.put("$verticleName.maxIntervalMillis", maxIntervalMillis)
  }

  override fun composeInputMessage(): JsonObject = JsonObject().put("value", 0.50)

  @TestAsync(
      numInputMessages = 1,
      numResultMessages = 10,
      maxDurationMillis = maxIntervalMillis * 10)
  fun test(testContext: TestContext, json: JsonObject) {
    val quality = json.getDouble("quality")
    testContext.assertInRange(0.5, quality, 0.5)
  }
}