package nl.cerios.reactive.chocolate.factory.model

import io.vertx.core.json.JsonObject
import java.util.*

data class Peanut(
    val id: UUID,
    val quality: Double) {

  companion object {
    fun fromJson(json: JsonObject): Peanut = Peanut(UUID.fromString(json.getString("id")), json.getDouble("quality"))
  }

  fun toJson(): JsonObject = JsonObject()
      .put("id", id.toString())
      .put("quality", quality)
}
