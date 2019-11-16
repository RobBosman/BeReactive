package nl.bransom.reactive.chunk0;

import com.mongodb.client.MongoClient;
import org.junit.jupiter.api.Test;

class ImperativeStyleJavaTest {

  @Test
  void run() {
    final String jokeRaw = nl.bransom.reactive.chunk0.FetchJokeServiceJava.fetchJoke();
    try (final MongoClient mongoClient = nl.bransom.reactive.chunk0.StorageServiceJava.getMongoClient()) {
      nl.bransom.reactive.chunk0.StorageServiceJava.convertAndStore(jokeRaw, mongoClient);
      nl.bransom.reactive.chunk0.StorageServiceJava.printAllJokes(mongoClient);
    }
  }
}
