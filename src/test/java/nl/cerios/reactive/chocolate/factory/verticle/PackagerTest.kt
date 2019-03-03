package nl.cerios.reactive.chocolate.factory.verticle

import io.vertx.core.json.JsonObject
import io.vertx.rxjava.ext.unit.TestContext
import nl.cerios.reactive.chocolate.factory.model.TestHelper

internal object PackagerTest : VerticleTest(Packager::class.java.name, "mnm", "mnmParty") {

  private const val defaultNumMnMs = 5
  private const val collectAfterMillis = 1_000L

  override fun configureDeployment(configJson: JsonObject) {
    configJson.put("$verticleName.numMnMs", defaultNumMnMs)
    configJson.put("$verticleName.collectAfterMillis", collectAfterMillis)
  }

  override fun composeInputMessage(): JsonObject = TestHelper.createMnM().toJson()

  @TestAsync(
      numInputMessages = defaultNumMnMs - 1,
      numResultMessages = 1,
      maxDurationMillis = collectAfterMillis)
  fun testLessMnMs(testContext: TestContext, json: JsonObject) {
    val content = json.getInteger("numMnMs")
    testContext.assertEquals(defaultNumMnMs - 1, content)
  }

  @TestAsync(
      numInputMessages = defaultNumMnMs,
      numResultMessages = 1)
  fun testDefaultMnMs(testContext: TestContext, json: JsonObject) {
    val content = json.getInteger("numMnMs")
    testContext.assertEquals(defaultNumMnMs, content)
  }

  @TestAsync(
      numInputMessages = defaultNumMnMs + 1,
      numResultMessages = 2,
      maxDurationMillis = collectAfterMillis)
  fun testMoreMnMs(testContext: TestContext, json: JsonObject) {
    val content = json.getInteger("numMnMs")
    testContext.assertTrue(content == defaultNumMnMs || content == 1)
  }
}