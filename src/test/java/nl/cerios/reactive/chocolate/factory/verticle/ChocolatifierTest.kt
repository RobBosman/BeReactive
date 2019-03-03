package nl.cerios.reactive.chocolate.factory.verticle

import io.vertx.core.json.JsonObject
import io.vertx.rxjava.ext.unit.TestContext
import nl.cerios.reactive.chocolate.factory.model.Peanut

internal object ChocolatifierTest : VerticleTest(
    Chocolatifier::class.java.name,
    "peanut",
    "chocoNut") {

  private const val processingMillis = 100L

  override fun configureDeployment(configJson: JsonObject) {
    configJson.put("$verticleName.processingMillis", processingMillis)
  }

  override fun composeInputMessage(): JsonObject = Peanut(0.5).toJson()

  @TestAsync(
      numInputMessages = 10,
      numResultMessages = 10,
      maxDurationMillis = processingMillis * 20)
  fun test(testContext: TestContext, json: JsonObject) {
    val quality = json.getDouble("quality")
    testContext.assertInRange(0.5, quality, 0.5)
    val flavor = json.getString("flavor")
    testContext.assertNotNull(Flavor.valueOf(flavor))
  }
}