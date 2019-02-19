package nl.cerios.reactive.chocolate.chunk1

import nl.cerios.reactive.chocolate.chunk1.FetchJokeService.fetchJoke
import nl.cerios.reactive.chocolate.chunk1.StorageService.convertAndStore
import nl.cerios.reactive.chocolate.chunk1.StorageService.getMongoClient
import nl.cerios.reactive.chocolate.chunk1.StorageService.printAllJokes
import org.junit.jupiter.api.Test

internal object ImperativeStyleTest {

  @Test
  fun run() {
    val jokeRaw = fetchJoke()
    getMongoClient().use { mongoClient ->
      convertAndStore(jokeRaw, mongoClient)
      printAllJokes(mongoClient)
    }
  }
}
