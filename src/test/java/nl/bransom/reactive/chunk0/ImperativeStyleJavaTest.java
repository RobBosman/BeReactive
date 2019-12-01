package nl.bransom.reactive.chunk0;

import com.mongodb.client.MongoClient;
import org.junit.jupiter.api.Test;

import static nl.bransom.reactive.chunk0.FetchJokeServiceJava.fetchJoke;
import static nl.bransom.reactive.chunk0.StorageServiceJava.*;

class ImperativeStyleJavaTest {

  @Test
  void run() {
    final String jokeJson = fetchJoke();
    try (final MongoClient mongoClient = getMongoClient()) {
      convertAndStore(jokeJson, mongoClient);
      printAllJokes(mongoClient);
    }
  }
}
