package nl.cerios.reactive.pizza

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Projections
import org.bson.Document
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

object StorageService {

  private val log = LoggerFactory.getLogger(javaClass)

  fun getMongoClient(): MongoClient {
    log.debug("get MongoDB client")
    val mongoClient = MongoClients.create("mongodb://localhost:27017")
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
    val joke = JSONObject(jokeRaw)
        .getJSONObject("value")
        .getString("joke")
    val jokeDocument = Document()
        .append("at", LocalDateTime.now())
        .append("joke", joke)
    mongoCollection.insertOne(jokeDocument)
    log.debug("converted and stored joke")
  }

  fun printAllJokes(mongoCollection: MongoCollection<Document>) {
    mongoCollection
        .find()
        .projection(Projections.excludeId())
        .forEach { d -> println("\t\t${d.toJson()}") }
  }
}