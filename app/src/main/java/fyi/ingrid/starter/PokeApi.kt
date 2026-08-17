package fyi.ingrid.starter

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

// One public REST API, no key and no account: PokeAPI. Both calls run on the
// IO dispatcher (withContext(Dispatchers.IO)) so the network never touches
// the main thread, and both throw on a non-OK response so the UI can show its
// error state.
data class PokemonSummary(val name: String)

data class Pokemon(
    val name: String,
    val heightM: Double,
    val weightKg: Double,
    val types: List<String>,
)

object PokeApi {
    private const val BASE = "https://pokeapi.co/api/v2"

    suspend fun list(): List<PokemonSummary> = withContext(Dispatchers.IO) {
        val results = JSONObject(get("$BASE/pokemon?limit=151")).getJSONArray("results")
        (0 until results.length()).map {
            PokemonSummary(results.getJSONObject(it).getString("name"))
        }
    }

    suspend fun detail(name: String): Pokemon = withContext(Dispatchers.IO) {
        val obj = JSONObject(get("$BASE/pokemon/$name"))
        val typesArray = obj.getJSONArray("types")
        val types = (0 until typesArray.length()).map {
            typesArray.getJSONObject(it).getJSONObject("type").getString("name")
        }
        Pokemon(
            name = obj.getString("name"),
            heightM = obj.getInt("height") / 10.0,
            weightKg = obj.getInt("weight") / 10.0,
            types = types,
        )
    }

    private fun get(url: String): String {
        val conn = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10000
            readTimeout = 10000
        }
        try {
            if (conn.responseCode !in 200..299) {
                throw RuntimeException("The API answered ${conn.responseCode}")
            }
            return conn.inputStream.bufferedReader().use { it.readText() }
        } finally {
            conn.disconnect()
        }
    }
}
