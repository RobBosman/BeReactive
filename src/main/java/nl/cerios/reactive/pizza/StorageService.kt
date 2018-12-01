package nl.cerios.reactive.pizza

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Projections
import org.bson.Document
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

object StorageService {

  private val log = LoggerFactory.getLogger(javaClass)
  private const val connectionString = "mongodb://localhost:27017"

  fun getMongoClient(): MongoClient {
    log.debug("get MongoDB client")
    val mongoClient = MongoClients.create(connectionString)
    log.debug("got MongoDB client")
    return mongoClient
  }

  fun getMongoCollection(mongoClient: MongoClient): MongoCollection<Document> {
    log.debug("get MongoDB collection")
    val mongoCollection = mongoClient
        .getDatabase("reactive-pizza")
        .getCollection("jokes")
    log.debug("got MongoDB collection")
    return mongoCollection
  }

  fun convertAndStore(jokeRaw: String, mongoCollection: MongoCollection<Document>) {
    log.debug("convert and store joke")
    val jokeValue = Document.parse(jokeRaw)["value"] as Document
    val jokeDocument = Document()
        .append("at", LocalDateTime.now())
        .append("joke", jokeValue["joke"])
        .append("categories", jokeValue["categories"])

    mongoCollection.insertOne(jokeDocument)
    log.debug("converted and stored joke")
  }

  fun printAllJokes(mongoCollection: MongoCollection<Document>) {
    mongoCollection
        .find()
        .projection(Projections.excludeId())
        .forEach { d -> println(d.toJson()) }
  }
}