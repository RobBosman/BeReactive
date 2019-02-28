package nl.cerios.reactive.chocolate.factory.model

import io.vertx.core.json.JsonObject
import nl.cerios.reactive.chocolate.factory.verticle.Color

internal data class ColorNut(
    private val chocoNut: ChocoNut,
    private val color: Color) {

  companion object {
    fun fromJson(json: JsonObject): ColorNut {
      return ColorNut(ChocoNut.fromJson(json), Color.valueOf(json.getString("color")))
    }
  }

  fun toJson(): JsonObject {
    return chocoNut.toJson()
        .put("color", color)
  }
}