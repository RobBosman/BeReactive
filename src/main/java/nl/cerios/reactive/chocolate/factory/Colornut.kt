package nl.cerios.reactive.chocolate.factory

import io.vertx.core.json.JsonObject

internal class Colornut(val color: Color) {

  fun toJson(): JsonObject {
    return JsonObject()
        .put("color", color)
  }
}