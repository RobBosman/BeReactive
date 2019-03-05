package nl.cerios.reactive.chocolate.factory.model

import nl.cerios.reactive.chocolate.factory.model.TestHelper.createColorNut
import nl.cerios.reactive.chocolate.factory.verticle.Color
import nl.cerios.reactive.chocolate.factory.verticle.Flavor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.UUID.randomUUID

internal class ColorNutTest {

  @Test
  fun json() {
    val id = randomUUID()
    val quality = 0.25
    val flavor = Flavor.MILK
    val color = Color.YELLOW

    val colorNut1 = createColorNut(id, quality, flavor, color)
    val json = colorNut1.toJson()
    val colorNut2 = ColorNut.fromJson(json)

    assertEquals(id.toString(), json.getString("id"))
    assertEquals(quality, json.getDouble("quality"))
    assertEquals(flavor.name, json.getString("flavor"))
    assertEquals(color.name, json.getString("color"))
    assertEquals(colorNut1, colorNut2)
  }
}