package nl.cerios.reactive.pizza.step0;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.function.Consumer;

public class StorageServiceJava {

  private static final Logger LOG = LoggerFactory.getLogger(StorageServiceJava.class);
  private static final String CONNECTION_STRING = "mongodb://localhost:27017";

  public static MongoClient getMongoClient() {
    LOG.debug("get MongoDB client");
    final MongoClient mongoClient = MongoClients.create(CONNECTION_STRING);
    LOG.debug("got MongoDB client");
    return mongoClient;
  }

  public static MongoCollection<Document> getMongoCollection(final MongoClient mongoClient) {
    LOG.debug("get MongoDB collection");
    final MongoCollection<Document> mongoCollection = mongoClient
        .getDatabase("reactive-pizza")
        .getCollection("jokes");
    LOG.debug("got MongoDB collection");
    return mongoCollection;
  }

  public static void convertAndStore(final String jokeRaw, final MongoCollection<Document> mongoCollection) {
    LOG.debug("convert and store joke");
    final Document jokeValue = (Document) Document.parse(jokeRaw).get("value");
    final Document jokeDocument = new Document()
        .append("at", LocalDateTime.now())
        .append("joke", jokeValue.get("joke"))
        .append("categories", jokeValue.get("categories"));

    mongoCollection.insertOne(jokeDocument);
    LOG.debug("converted and stored joke");
  }

  public static void printAllJokes(final MongoCollection<Document> mongoCollection) {
    mongoCollection
        .find()
        .projection(Projections.excludeId())
        .map(Document::toJson)
        .forEach((Consumer<String>) System.out::println);
  }
}
