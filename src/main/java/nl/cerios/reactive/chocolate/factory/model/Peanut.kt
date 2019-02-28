package nl.cerios.reactive.chocolate.factory.model

import io.vertx.core.json.JsonObject
import java.security.SecureRandom

internal data class Peanut(
    private val quality: Float) {

  constructor() : this(SecureRandom().nextFloat())

  companion object {
    fun fromJson(json: JsonObject): Peanut {
      return Peanut(json.getFloat("quality"))
    }
  }

  fun toJson(): JsonObject {
    return JsonObject()
        .put("quality", quality)
  }
}