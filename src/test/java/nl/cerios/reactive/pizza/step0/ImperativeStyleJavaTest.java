package nl.cerios.reactive.pizza.step0;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.junit.jupiter.api.Test;

import static nl.cerios.reactive.pizza.step0.FetchJokeServiceJava.fetchJoke;
import static nl.cerios.reactive.pizza.step0.StorageServiceJava.convertAndStore;
import static nl.cerios.reactive.pizza.step0.StorageServiceJava.getMongoClient;
import static nl.cerios.reactive.pizza.step0.StorageServiceJava.getMongoCollection;
import static nl.cerios.reactive.pizza.step0.StorageServiceJava.printAllJokes;

class ImperativeStyleJavaTest {

  @Test
  void run() {
    final String jokeRaw = fetchJoke();
    try (final MongoClient mongoClient = getMongoClient()) {
      final MongoCollection<Document> mongoCollection = getMongoCollection(mongoClient);
      convertAndStore(jokeRaw, mongoCollection);
      printAllJokes(mongoCollection);
    }
  }
}
