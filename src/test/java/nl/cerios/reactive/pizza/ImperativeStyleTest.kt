package nl.cerios.reactive.pizza

import nl.cerios.reactive.pizza.FetchJokeService.fetchJoke
import nl.cerios.reactive.pizza.StorageService.convertAndStore
import nl.cerios.reactive.pizza.StorageService.getMongoClient
import nl.cerios.reactive.pizza.StorageService.getMongoCollection
import nl.cerios.reactive.pizza.StorageService.printAllJokes
import org.junit.jupiter.api.Test

internal class ImperativeStyleTest {

  @Test
  fun run() {
    val jokeRaw = fetchJoke()
    getMongoClient().use { mongoClient ->
      val mongoCollection = getMongoCollection(mongoClient)
      convertAndStore(jokeRaw, mongoCollection)
      printAllJokes(mongoCollection)
    }
  }
}
