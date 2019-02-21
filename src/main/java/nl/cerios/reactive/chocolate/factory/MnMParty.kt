package nl.cerios.reactive.chocolate.factory

import io.vertx.core.json.JsonObject

internal class MnMParty(val weight: Int) {

  fun toJson(): JsonObject {
    return JsonObject()
        .put("weight", weight)
  }
}