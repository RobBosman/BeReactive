package nl.cerios.reactive.chocolate.factory.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.UUID.randomUUID

internal class PeanutTest {

  @Test
  fun json() {
    val id = randomUUID()
    val quality = 0.25

    val peanut1 = Peanut(id, quality)
    val json = peanut1.toJson()
    val peanut2 = Peanut.fromJson(json)

    assertEquals(id.toString(), json.getString("id"))
    assertEquals(quality, json.getDouble("quality"))
    assertEquals(peanut1, peanut2)
  }
}