package nl.bransom.reactive.chunk1

import nl.bransom.reactive.chunk1.FetchJokeService.fetchJoke
import nl.bransom.reactive.chunk1.StorageService.convertAndStore
import nl.bransom.reactive.chunk1.StorageService.getMongoClient
import nl.bransom.reactive.chunk1.StorageService.printAllJokes
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
