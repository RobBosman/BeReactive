package nl.cerios.reactive.pizza.step0;

import com.mongodb.client.MongoClient;
import org.junit.jupiter.api.Test;

import static nl.cerios.reactive.pizza.step0.FetchJokeServiceJava.fetchJoke;
import static nl.cerios.reactive.pizza.step0.StorageServiceJava.convertAndStore;
import static nl.cerios.reactive.pizza.step0.StorageServiceJava.getMongoClient;
import static nl.cerios.reactive.pizza.step0.StorageServiceJava.printAllJokes;

class ImperativeStyleJavaTest {

  @Test
  void run() {
    final String jokeRaw = fetchJoke();
    try (final MongoClient mongoClient = getMongoClient()) {
      convertAndStore(jokeRaw, mongoClient);
      printAllJokes(mongoClient);
    }
  }
}
