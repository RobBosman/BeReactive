package nl.cerios.reactive.chocolate.factory

import io.vertx.core.json.JsonObject
import java.security.SecureRandom

enum class Flavor { PURE, MILK, WHITE }

internal class Choconut {

  private val flavor = Flavor.values()[SecureRandom().nextInt(Flavor.values().size)]

  fun toJson(): JsonObject {
    return JsonObject()
        .put("flavor", flavor)
  }
}