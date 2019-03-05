package nl.cerios.reactive.chocolate.factory.model

import nl.cerios.reactive.chocolate.factory.model.TestHelper.createChocoNut
import nl.cerios.reactive.chocolate.factory.verticle.Flavor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.UUID.randomUUID

internal class ChocoNutTest {

  @Test
  fun json() {
    val id = randomUUID()
    val quality = 0.25
    val flavor = Flavor.MILK

    val chocoNut1 = createChocoNut(id, quality, flavor)
    val json = chocoNut1.toJson()
    val chocoNut2 = ChocoNut.fromJson(json)

    assertEquals(id.toString(), json.getString("id"))
    assertEquals(quality, json.getDouble("quality"))
    assertEquals(flavor.name, json.getString("flavor"))
    assertEquals(chocoNut1, chocoNut2)
  }
}