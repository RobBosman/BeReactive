package nl.cerios.reactive.pizza

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import org.bson.Document
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

class AsyncWithCompletableFutures {

  private val log = LoggerFactory.getLogger(javaClass)

  fun run(): CompletableFuture<Void> {

    val jokeRawCF = CompletableFuture.supplyAsync(::fetchJoke)

    val mongoClientCF = CompletableFuture.supplyAsync(::getMongoClient)

    return mongoClientCF
        .thenApplyAsync(::getMongoCollection)
        .thenCombine(jokeRawCF) { mongoCollection, jokeRaw -> convertAndStore(jokeRaw, mongoCollection) }
        .thenAccept {
          log.debug("close MongoDB client")
          mongoClientCF.get().close()
        }
  }

  private fun fetchJoke(): String {
    log.debug("fetch joke")
    val url = URL("http://api.icndb.com/jokes/random?limitTo=[nerdy,explicit]") // Chuck Norris jokes
    val httpConnection = url.openConnection() as HttpURLConnection
    httpConnection.connectTimeout = 5000
    httpConnection.readTimeout = 5000
    try {
      httpConnection.inputStream.reader()
          .use { httpReader ->
            return httpReader.readText()
          }
    } finally {
      httpConnection.disconnect()
      log.debug("fetched joke")
    }
  }

  private fun convertRawToDocument(jokeRaw: String): Document {
    val joke = JSONObject(jokeRaw)
        .getJSONObject("value")
        .getString("joke")
    return Document()
        .append("at", LocalDateTime.now())
        .append("joke", joke)
  }

  private fun getMongoClient(): MongoClient {
    log.debug("get MongoDB client")
    val mongoClient = MongoClients
        .create("mongodb://localhost:27017")
    log.debug("got MongoDB client")
    return mongoClient
  }

  private fun getMongoCollection(mongoClient: MongoClient): MongoCollection<Document> {
    log.debug("get MongoDB collection")
    val mongoCollection = mongoClient
        .getDatabase("reactive-pizza")
        .getCollection("jokes")
    log.debug("got MongoDB collection")
    return mongoCollection
  }

  private fun convertAndStore(jokeRaw: String, mongoCollection: MongoCollection<Document>) {
    log.debug("convert and store joke")
    val jokeDocument = convertRawToDocument(jokeRaw)
    mongoCollection.insertOne(jokeDocument)
    log.debug("converted and stored joke")
  }
}
