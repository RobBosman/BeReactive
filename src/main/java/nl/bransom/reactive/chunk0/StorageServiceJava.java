package nl.bransom.reactive.chunk0;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.function.Consumer;

class StorageServiceJava {

  private static final Logger LOG = LoggerFactory.getLogger(StorageServiceJava.class);
  private static final String CONNECTION_STRING = System.getProperty("connectionString", "mongodb://localhost:27017");
  private static final String DATABASE_NAME = "be-reactive";
  private static final String COLLECTION_NAME = "jokes";

  static MongoClient getMongoClient() {
    LOG.debug("get MongoDB client");
    final MongoClient mongoClient = MongoClients.create(CONNECTION_STRING);
    LOG.debug("got MongoDB client");
    return mongoClient;
  }

  static void convertAndStore(final String jokeJson, final MongoClient mongoClient) {
    LOG.debug("convert and store joke");
    final Document jokeValue = (Document) Document.parse(jokeJson).get("value");
    final String joke = (String) jokeValue.get("joke");
    final Document jokeDocument = new Document()
        .append("at", LocalDateTime.now())
        .append("joke", joke)
        .append("categories", jokeValue.get("categories"));

    mongoClient
        .getDatabase(DATABASE_NAME)
        .getCollection(COLLECTION_NAME)
        .insertOne(jokeDocument);
    LOG.debug("converted and stored joke");
  }

  static void printAllJokes(final MongoClient mongoClient) {
    mongoClient
        .getDatabase(DATABASE_NAME)
        .getCollection(COLLECTION_NAME)
        .find()
        .map(x -> x.get("joke"))
        .forEach((Consumer<Object>) System.out::println);
  }
}
