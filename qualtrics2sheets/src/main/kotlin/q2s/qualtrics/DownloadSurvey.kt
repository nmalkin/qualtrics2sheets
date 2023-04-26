package q2s.qualtrics

import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.engine.java.Java
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import logging.KotlinLogging
import q2s.util.FileExistsStrategy
import q2s.util.unzip
import q2s.util.unzipAndRename
import java.io.InputStream
import java.nio.file.Path

private const val SLEEP_TIME_MILLISECONDS = 3 * 1000L

private val logger = KotlinLogging.logger { }

suspend fun startExport(client: HttpClient, urls: QualtricsURL, apiToken: String, surveyID: String): String {
    logger.debug { "requesting export of $surveyID" }

    val response: HttpResponse = client.request(urls.startExport(surveyID)) {
        method = HttpMethod.Post
        headers {
            append("X-API-TOKEN", apiToken)
        }
        contentType(ContentType.Application.Json)
        val payload = mapOf(
            "format" to "csv",
            /* Include display order information, to see when randomized items were shown */
            "includeDisplayOrder" to "true",
            /*
            For columns that have answer labels, export two columns: one that uses recode values and one that uses labels. The label column has an IsLabelsColumn field in the 3rd header row. Note that this cannot be used with useLabels. Use this setting if you wish to export both column labels and numberic values in the same export.
            - from https://api.qualtrics.com/guides/reference/responseImportsExports.json/paths/~1surveys~1%7BsurveyId%7D~1export-responses/post
             */
            "includeLabelColumns" to "true",
        )
        body = Json.encodeToString(payload)
    }
    val text = response.readText()
    val json = Json.parseToJsonElement(text)
    val progressID: String = json.jsonObject["result"]?.jsonObject?.get("progressId")?.jsonPrimitive?.content
        ?: throw QualtricsException("failed to start export: $text")

    logger.debug { "export started, progressId $progressID" }

    return progressID
}

suspend fun downloadSurvey(
    datacenter: QualtricsDatacenter,
    apiToken: String,
    surveyID: String,
    targetDirectory: Path,
    outputFilename: String?,
    fileExistsStrategy: FileExistsStrategy
) {
    val client = HttpClient(Java)

    val urls = QualtricsURL(datacenter)

    val progressID = startExport(client, urls, apiToken, surveyID)

    var fileID: String? = null
    var progressStatus = "inProgress"
    while (progressStatus != "complete") {
        delay(SLEEP_TIME_MILLISECONDS)

        logger.debug { "checking on export progress at ${urls.getExportProgress(surveyID, progressID)}" }

        val progressResponse: HttpResponse = client.request(urls.getExportProgress(surveyID, progressID)) {
            method = HttpMethod.Get
            headers {
                append("X-API-TOKEN", apiToken)
            }
        }
        val progressText = progressResponse.readText()
        val progressJson = Json.parseToJsonElement(progressText)
        progressStatus = progressJson.jsonObject["result"]?.jsonObject?.get("status")?.jsonPrimitive?.content
            ?: throw QualtricsException("failed to determine export status: $progressText")

        if (progressStatus == "failed") {
            throw QualtricsException("Qualtrics export failed: $progressText")
        }

        fileID = progressJson.jsonObject["result"]?.jsonObject?.get("fileId")?.jsonPrimitive?.content
    }

    if (fileID == null) {
        throw QualtricsException("Qualtrics export complete but fileId is null")
    }

    downloadExport(client, urls, apiToken, surveyID, fileID, targetDirectory, outputFilename, fileExistsStrategy)
}

suspend fun downloadExport(
    client: HttpClient,
    urls: QualtricsURL,
    apiToken: String,
    surveyID: String,
    fileID: String,
    targetDirectory: Path,
    outputFilename: String?,
    fileExistsStrategy: FileExistsStrategy
) {
    logger.debug { "downloading result from ${urls.getExportFile(surveyID, fileID)}" }

    val downloadResponse: HttpResponse = client.request(urls.getExportFile(surveyID, fileID)) {
        method = HttpMethod.Get
        headers {
            append("X-API-TOKEN", apiToken)
        }
    }
    val content = downloadResponse.receive<InputStream>()

    if (outputFilename == null) {
        logger.debug { "unzipping $surveyID export to $targetDirectory" }
        unzip(content, targetDirectory, fileExistsStrategy)
    } else {
        logger.debug { "unzipping $surveyID export as $outputFilename to $targetDirectory" }
        unzipAndRename(content, targetDirectory, outputFilename, fileExistsStrategy)
    }
}
