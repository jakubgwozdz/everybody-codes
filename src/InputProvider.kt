import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

private val logger = LoggerFactory.getLogger("InputProvider")

@OptIn(ExperimentalStdlibApi::class)
fun provideInput(event: Int, quest: Int, part: Int, invalidate: Boolean = false): String =
    cached("local/everybody_codes_e${event}_q${quest.toString().padStart(2, '0')}_p${part}.txt", invalidate) {
        try {
            val cookie = File("local/ec-cookie").readText().trim()

            val seed = cachingDownloadGetKey("https://everybody.codes/api/user/me", "seed", invalidate, cookie)
                .also { logger.info("seed: $it") }
                .also { require(it != "0") { "Seed must not be 0" } }

            val encryptedInput = cachingDownloadGetKey(
                "https://everybody-codes.b-cdn.net/assets/$event/$quest/input/$seed.json",
                part.toString(),
                invalidate,
            )
                .hexToByteArray()

            val (key, iv) = cachingDownloadGetKey(
                "https://everybody.codes/api/event/$event/quest/$quest",
                "key$part",
                invalidate,
                cookie
            )
//                .let { it.take(20) + "~" + it.drop(21) }
                .let { it.toByteArray(Charsets.UTF_8) to it.take(16).toByteArray(Charsets.UTF_8) }

            val secretKeySpec = SecretKeySpec(key, "AES")
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, IvParameterSpec(iv))
            cipher.doFinal(encryptedInput).toString(Charsets.UTF_8)
        } catch (e: Exception) {
            if (!invalidate) {
                logger.error("Failed to provide input ($e), will reset cache and try again")
                provideInput(event, quest, part, invalidate = true)
            } else error("Failed to provide input")
        }
    }

fun String.asJson() = Json.decodeFromString<JsonObject>(this)

fun cachingDownload(uri: String, cookie: String? = null) =
    cached("local/${uri.asPath()}") { download(uri, cookie) }

fun invalidateAndRedownload(uri: String, cookie: String? = null) =
    cached("local/${uri.asPath()}", invalidate = true) { download(uri, cookie) }

fun cachingDownloadGetKey(uri: String, key: String, invalidate: Boolean, cookie: String? = null) =
    ((if (invalidate) null else cachingDownload(uri, cookie).asJson()[key])
        ?: invalidateAndRedownload(uri, cookie).asJson()[key]
        ?: error("No key $key found at $uri"))
        .jsonPrimitive.content

private fun String.asPath(): String = substringAfter("://").replace("?", "/").replace("=", "/")

fun download(uri: String, cookie: String? = null) = HttpClient.newHttpClient().send(
    HttpRequest.newBuilder()
        .uri(URI.create(uri))
        .let { if (!cookie.isNullOrBlank()) it.header("Cookie", cookie) else it }
        .build(),
    HttpResponse.BodyHandlers.ofString()
).body()!!
    .also { logger.info("Downloaded $uri") }

fun cached(path: String, invalidate: Boolean = false, block: () -> String): String {
    val file = File(path)
    if (file.canRead() && file.length() > 0) {
        if (invalidate) file.delete()
            .also { logger.info("Invalidated cached file $path") }
        else return file.readText()
            .also { logger.info("Loaded cached file $path") }
    }
    return block().also {
        file.parentFile.mkdirs()
        file.writeText(it)
            .also { logger.info("Cached file $path") }
    }
}

fun main() {
    println(provideInput(1, 1, 1))
}
