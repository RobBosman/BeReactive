package nl.cerios.reactive.chocolate.factory.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class PeanutTest {

  @Test
  fun json() {
    val quality = 0.25F

    val peanut1 = Peanut(quality)
    val json = peanut1.toJson()
    val peanut2 = Peanut.fromJson(json)

    assertEquals(quality, json.getFloat("quality"))
    assertEquals("""{"quality":0.25}""", json.encode())
    assertEquals(peanut1, peanut2)
  }
}