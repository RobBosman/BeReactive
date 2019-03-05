package nl.cerios.reactive.chocolate.factory.model

import nl.cerios.reactive.chocolate.factory.model.TestHelper.createMnM
import nl.cerios.reactive.chocolate.factory.verticle.Color
import nl.cerios.reactive.chocolate.factory.verticle.Flavor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.UUID.randomUUID

internal class MnMTest {

  @Test
  fun json() {
    val id = randomUUID()
    val quality = 0.25
    val flavor = Flavor.MILK
    val color = Color.YELLOW

    val mnm1 = createMnM(id, quality, flavor, color)
    val json = mnm1.toJson()
    val mnm2 = MnM.fromJson(json)

    assertEquals(id.toString(), json.getString("id"))
    assertEquals(quality, json.getDouble("quality"))
    assertEquals(flavor.name, json.getString("flavor"))
    assertEquals(color.name, json.getString("color"))
    assertEquals(mnm1, mnm2)
  }
}