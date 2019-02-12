package nl.cerios.reactive.pizza.step1

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.model.Projections
import org.bson.Document
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

object StorageService {

  private val log = LoggerFactory.getLogger(javaClass)
  private val connectionString = System.getProperty("connectionString", "mongodb://localhost:27017")

  fun getMongoClient(): MongoClient {
    log.debug("get MongoDB client")
    val mongoClient = MongoClients.create(connectionString)
    log.debug("got MongoDB client")
    return mongoClient
  }

  fun convertAndStore(jokeRaw: String, mongoClient: MongoClient): String {
    log.debug("convert and store joke")
    val jokeValue = Document.parse(jokeRaw)["value"] as Document
    val joke = jokeValue["joke"] as String
    val jokeDocument = Document()
        .append("at", LocalDateTime.now())
        .append("joke", joke)
        .append("categories", jokeValue["categories"])

    mongoClient
        .getDatabase("reactive-pizza")
        .getCollection("jokes")
        .insertOne(jokeDocument)
    log.debug("converted and stored joke")
    return joke
  }

  fun printAllJokes(mongoClient: MongoClient) {
    mongoClient
        .getDatabase("reactive-pizza")
        .getCollection("jokes")
        .find()
        .projection(Projections.excludeId())
        .map(Document::toJson)
        .forEach { println(it) }
  }
}