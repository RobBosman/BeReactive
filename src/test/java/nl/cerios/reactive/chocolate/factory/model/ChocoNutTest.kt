package nl.cerios.reactive.chocolate.factory.model

import nl.cerios.reactive.chocolate.factory.verticle.Flavor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ChocoNutTest {

  @Test
  fun json() {
    val quality = 0.25F
    val flavor = Flavor.MILK

    val chocoNut1 = ChocoNut(Peanut(quality), flavor)
    val json = chocoNut1.toJson()
    val chocoNut2 = ChocoNut.fromJson(json)

    assertEquals(quality, json.getFloat("quality"))
    assertEquals(flavor.name, json.getString("flavor"))
    assertEquals("""{"quality":0.25,"flavor":"MILK"}""", json.encode())
    assertEquals(chocoNut1, chocoNut2)
  }
}