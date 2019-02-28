package nl.cerios.reactive.chocolate.factory.model

import io.vertx.core.json.JsonObject

internal data class MnMParty(
    private val mnms: List<MnM>) {

  fun toJson(): JsonObject {
    return JsonObject()
        .put("content", mnms.size)
  }
}