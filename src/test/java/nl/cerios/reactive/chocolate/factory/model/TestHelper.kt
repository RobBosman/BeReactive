package nl.cerios.reactive.chocolate.factory.model

import nl.cerios.reactive.chocolate.factory.verticle.Color
import nl.cerios.reactive.chocolate.factory.verticle.Flavor
import java.util.stream.IntStream
import kotlin.streams.toList

object TestHelper {

  internal fun createChocoNut(quality: Double = 0.5, flavor: Flavor = Flavor.MILK) =
      ChocoNut(Peanut(quality), flavor)

  internal fun createColorNut(quality: Double = 0.5, flavor: Flavor = Flavor.MILK, color: Color = Color.GREEN) =
      ColorNut(createChocoNut(quality, flavor), color)

  internal fun createMnM(quality: Double = 0.5, flavor: Flavor = Flavor.MILK, color: Color = Color.GREEN) =
      MnM(createColorNut(quality, flavor, color))

  internal fun createMnMs(numMnMs: Int) =
      IntStream.range(0, numMnMs)
          .mapToObj { createMnM() }
          .toList()
}