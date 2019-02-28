package nl.cerios.reactive.chocolate.factory.model

import io.vertx.core.json.JsonObject
import nl.cerios.reactive.chocolate.factory.verticle.Flavor

internal data class ChocoNut(
    private val peanut: Peanut,
    private val flavor: Flavor) {

  companion object {
    fun fromJson(json: JsonObject): ChocoNut {
      return ChocoNut(Peanut.fromJson(json), Flavor.valueOf(json.getString("flavor")))
    }
  }

  fun toJson(): JsonObject {
    return peanut.toJson()
        .put("flavor", flavor)
  }
}