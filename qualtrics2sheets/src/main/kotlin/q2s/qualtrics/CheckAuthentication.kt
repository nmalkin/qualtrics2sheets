package q2s.qualtrics

import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.HttpMethod
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import logging.KotlinLogging

private val logger = KotlinLogging.logger { }

suspend fun checkApiToken(datacenter: QualtricsDatacenter, apiToken: String) {
    logger.debug { "checking validity of Qualtrics API token" }
    val client = HttpClient(Java)

    val url = QualtricsURL(datacenter).whoAmI
    logger.debug { "making request to $url" }
    val response: HttpResponse = client.request(url) {
        method = HttpMethod.Get
        headers {
            append("X-API-TOKEN", apiToken)
        }
    }
    val text = response.readText()
    val json = Json.parseToJsonElement(text)
    val username: String? = json.jsonObject["result"]?.jsonObject?.get("userName")?.jsonPrimitive?.content

    if (username == null) {
        throw QualtricsException("failed to start export: $text")
    } else {
        logger.debug { "running with valid API token for $username" }
    }
}
