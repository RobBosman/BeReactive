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
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

class AsyncWithFutures {

  private val log = LoggerFactory.getLogger(javaClass)

  fun run(executor: ExecutorService): Future<Unit> {

    val jokeRawF = executor.submit<String>(::fetchJoke)

    val mongoClientF = executor.submit<MongoClient>(::getMongoClient)

    val mongoCollectionF = executor.submit<MongoCollection<Document>> {
      log.debug("wait for MongoDB client")
      val mongoClient = mongoClientF.get() // blocking wait
      getMongoCollection(mongoClient)
    }

    return executor.submit<Unit> {
      log.debug("wait for async tasks to complete")
      val jokeRaw = jokeRawF.get() // blocking wait
      val mongoCollection = mongoCollectionF.get() // blocking wait
      convertAndStore(jokeRaw, mongoCollection)
      log.debug("close MongoDB client")
      mongoClientF.get().close()
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
