package nl.cerios.reactive.chocolate.factory.model

import io.vertx.core.json.JsonObject
import nl.cerios.reactive.chocolate.factory.verticle.Color
import java.util.*

data class ColorNut(
    private val chocoNut: ChocoNut,
    val color: Color) {

  val id: UUID
    get() = chocoNut.id

  companion object {
    fun fromJson(json: JsonObject): ColorNut =
        ColorNut(ChocoNut.fromJson(json), Color.valueOf(json.getString("color")))
  }

  fun toJson(): JsonObject = chocoNut.toJson()
      .put("color", color)
}
