package nl.cerios.reactive.chocolate.factory.model

import io.vertx.core.json.JsonObject

internal data class MnM(
    private val colorNut: ColorNut) {

  companion object {
    fun fromJson(json: JsonObject): MnM =  MnM(ColorNut.fromJson(json))
  }

  fun toJson(): JsonObject = colorNut.toJson()
}