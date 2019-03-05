package nl.cerios.reactive.chocolate.factory.model

import io.vertx.core.json.JsonObject
import nl.cerios.reactive.chocolate.factory.verticle.Flavor
import java.util.*

data class ChocoNut(
    private val peanut: Peanut,
    val flavor: Flavor) {

  val id: UUID
    get() = peanut.id

  companion object {
    fun fromJson(json: JsonObject): ChocoNut = ChocoNut(Peanut.fromJson(json), Flavor.valueOf(json.getString("flavor")))
  }

  fun toJson(): JsonObject = peanut.toJson()
      .put("flavor", flavor)
}
