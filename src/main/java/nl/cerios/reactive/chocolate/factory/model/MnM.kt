package nl.cerios.reactive.chocolate.factory.model

import io.vertx.core.json.JsonObject
import java.util.*

internal data class MnM(
    private val colorNut: ColorNut) {

  val id: UUID
    get() = colorNut.id

  companion object {
    fun fromJson(json: JsonObject): MnM = MnM(ColorNut.fromJson(json))
  }

  fun toJson(): JsonObject = colorNut.toJson()
}
