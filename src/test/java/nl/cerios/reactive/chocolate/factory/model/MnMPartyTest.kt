package nl.cerios.reactive.chocolate.factory.model

import nl.cerios.reactive.chocolate.factory.verticle.Color
import nl.cerios.reactive.chocolate.factory.verticle.Flavor
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class MnMPartyTest {

  @Test
  fun json() {
    val mnm1 = createMnM(0.25, Flavor.MILK, Color.YELLOW)
    val mnm2 = createMnM(0.35, Flavor.PURE, Color.GREEN)
    val mnm3 = createMnM(0.45, Flavor.WHITE, Color.RED)
    val mnms = listOf(mnm1, mnm2, mnm3, mnm1, mnm2)

    val mnmParty1 = MnMParty(mnms)
    val json = mnmParty1.toJson()

    Assertions.assertEquals(mnms.size, json.getInteger("content"))
    Assertions.assertEquals("""{"content":5}""", json.encode())
  }

  private fun createMnM(quality: Double, flavor: Flavor, color: Color): MnM = MnM(ColorNut(ChocoNut(Peanut(quality), flavor), color))
}