package nl.cerios.reactive.chocolate.factory.model

import nl.cerios.reactive.chocolate.factory.verticle.Color
import nl.cerios.reactive.chocolate.factory.verticle.Flavor
import java.util.*
import java.util.UUID.randomUUID

object TestHelper {

  internal fun createChocoNut(id: UUID = randomUUID(), quality: Double = 0.5, flavor: Flavor = Flavor.MILK) =
      ChocoNut(Peanut(id, quality), flavor)

  internal fun createColorNut(id: UUID = randomUUID(), quality: Double = 0.5, flavor: Flavor = Flavor.MILK, color: Color = Color.GREEN) =
      ColorNut(createChocoNut(id, quality, flavor), color)

  internal fun createMnM(id: UUID = randomUUID(), quality: Double = 0.5, flavor: Flavor = Flavor.MILK, color: Color = Color.GREEN) =
      MnM(createColorNut(id, quality, flavor, color))
}