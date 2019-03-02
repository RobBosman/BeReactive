package nl.cerios.reactive.chocolate.factory.model

import io.vertx.core.json.JsonObject

data class Peanut(
    val quality: Double) {

  companion object {
    fun fromJson(json: JsonObject): Peanut = Peanut(json.getDouble("quality"))
  }

  fun toJson(): JsonObject = JsonObject()
      .put("quality", quality)
}