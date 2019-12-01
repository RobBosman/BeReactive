package nl.bransom.reactive.chunk1

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.bson.Document
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

object StorageService {

  private val log = LoggerFactory.getLogger(javaClass)
  private val connectionString = System.getProperty("connectionString", "mongodb://localhost:27017")
  private const val databaseName = "be-reactive"
  private const val collectionName = "jokes"

  fun getMongoClient(): MongoClient {
    log.debug("get MongoDB client")
    val mongoClient = MongoClients.create(connectionString)
    log.debug("got MongoDB client")
    return mongoClient
  }

  fun convertAndStore(jokeJson: String, mongoClient: MongoClient): String {
    log.debug("convert and store joke")
    val jokeValue = Document.parse(jokeJson)["value"] as Document
    val joke = jokeValue["joke"] as String
    val jokeDocument = Document()
        .append("at", LocalDateTime.now())
        .append("joke", joke)
        .append("categories", jokeValue["categories"])

    mongoClient
        .getDatabase(databaseName)
        .getCollection(collectionName)
        .insertOne(jokeDocument)
    log.debug("converted and stored joke")
    return joke
  }

  fun printAllJokes(mongoClient: MongoClient) {
    mongoClient
        .getDatabase(databaseName)
        .getCollection(collectionName)
        .find()
        .map { it["joke"] }
        .forEach { println(it) }
  }
}