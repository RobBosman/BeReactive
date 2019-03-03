package nl.cerios.reactive.chocolate.factory.model

import nl.cerios.reactive.chocolate.factory.model.TestHelper.createColorNut
import nl.cerios.reactive.chocolate.factory.verticle.Color
import nl.cerios.reactive.chocolate.factory.verticle.Flavor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ColorNutTest {

  @Test
  fun json() {
    val quality = 0.25
    val flavor = Flavor.MILK
    val color = Color.YELLOW

    val colorNut1 = createColorNut(quality, flavor, color)
    val json = colorNut1.toJson()
    val colorNut2 = ColorNut.fromJson(json)

    assertEquals(quality, json.getFloat("quality"))
    assertEquals(flavor.name, json.getString("flavor"))
    assertEquals(color.name, json.getString("color"))
    assertEquals("""{"quality":0.25,"flavor":"MILK","color":"YELLOW"}""", json.encode())
    assertEquals(colorNut1, colorNut2)
  }
}