package nl.cerios.reactive.pizza

import com.mongodb.client.MongoClients
import com.mongodb.client.model.Projections
import org.bson.Document
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime

class ImperativeStyle {

  fun run() {
    val jokeRaw = fetchJoke()
    val jokeDocument = convertRawToDocument(jokeRaw)
    storeInDatabase(jokeDocument)
  }

  private fun fetchJoke(): String {
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

  private fun storeInDatabase(jokeDocument: Document) {
    MongoClients
        .create("mongodb://localhost:27017")
        .use { mongoClient ->

          val mongoCollection = mongoClient
              .getDatabase("reactive-pizza")
              .getCollection("jokes")

          mongoCollection.insertOne(jokeDocument)

          // print all jokes
          mongoCollection
              .find()
              .projection(Projections.excludeId())
              .forEach { d -> println(d.toJson()) }
        }
  }
}
