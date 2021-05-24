package q2s.sheets

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import q2s.PROGRAM_NAME
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Path
import kotlin.io.path.bufferedReader
import kotlin.io.path.exists

const val APPLICATION_NAME = PROGRAM_NAME
const val TOKENS_DIRECTORY_PATH = "tokens"

class SheetsClient(
    private val credentialsFilePath: Path
) {
    private val applicationName: String = APPLICATION_NAME
    private val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()
    private val tokensDirectoryPath = TOKENS_DIRECTORY_PATH
    private val userId = "user"

    /**
     * Global instance of the scopes
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private val scopes = listOf(SheetsScopes.SPREADSHEETS)

    /**
     * Creates an authorized Credential object.
     * @return An authorized Credential object.
     */
    private fun getCredentials(httpTransport: NetHttpTransport): Credential {
        if (!credentialsFilePath.exists()) throw FileNotFoundException("Resource not found: $credentialsFilePath")

        val clientSecrets = GoogleClientSecrets.load(jsonFactory, credentialsFilePath.bufferedReader())

        // Build flow and trigger user authorization request
        val flow = GoogleAuthorizationCodeFlow.Builder(
            httpTransport, jsonFactory, clientSecrets, scopes
        )
            .setDataStoreFactory(FileDataStoreFactory(File(tokensDirectoryPath)))
            .setAccessType("offline")
            .build()
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize(userId)
    }

    fun getClient(): Sheets {
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        return Sheets.Builder(httpTransport, jsonFactory, getCredentials(httpTransport))
            .setApplicationName(applicationName)
            .build() ?: throw RuntimeException("failed to build a Sheets client")
    }
}
