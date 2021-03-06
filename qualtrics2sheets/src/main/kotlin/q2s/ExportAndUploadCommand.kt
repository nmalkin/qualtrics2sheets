package q2s

import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import kotlinx.cli.default
import kotlinx.cli.multiple
import kotlinx.cli.required
import kotlinx.coroutines.runBlocking
import logging.DEFAULT_LOG_LEVEL
import logging.KotlinLogging
import logging.LogLevel
import q2s.qualtrics.QualtricsDatacenter
import q2s.qualtrics.checkApiToken
import q2s.qualtrics.downloadSurvey
import q2s.sheets.SheetsClient
import q2s.sheets.TOKENS_DIRECTORY_PATH
import q2s.sheets.uploadCSV
import q2s.util.FileExistsStrategy
import q2s.util.makeCopyOfCSV
import q2s.util.toPath
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createTempDirectory
import kotlin.io.path.extension

private val logger = KotlinLogging.logger { }

@ExperimentalCli
class ExportAndUploadCommand : Subcommand("run", "download a Qualtrics export and immediately upload it to Sheets") {
    val debug by option(
        ArgType.Boolean,
        fullName = "debug"
    )

    /* Qualtrics parameters */
    val datacenter by option(
        ArgType.String,
        fullName = "datacenter",
        shortName = "d",
        description = "the Qualtrics datacenter ID (see https://api.qualtrics.com/guides/docs/Instructions/Quick%20Start/qualtrics-api-quick-start.md#getting-your-api-url)",
    ).required()
    val apiToken by option(
        ArgType.String,
        fullName = "token",
        shortName = "t",
        description = "Qualtrics API token"
    ).required()
    val surveyID by option(
        ArgType.String,
        fullName = "survey",
        description = "Qualtrics survey ID"
    ).required()
    val saveExportDirectory by option(
        ArgType.String,
        fullName = "export-path",
        description = "if specified, the Qualtrics CSV will be extracted to the given directory. Otherwise, a temporary file system directory is used."
    )
    val outputFilename by option(
        ArgType.String,
        fullName = "filename",
        description = "filename to use for the extracted CSV"
    )
    val ifExists by option(
        ArgType.Choice<FileExistsStrategy>(),
        fullName = "if-exists",
        description = "what to do if the target file exists (only applicable if --export-path is specified)"
    ).default(FileExistsStrategy.ABORT)

    /* Sheets parameters */
    val credentials by option(
        ArgType.String,
        fullName = "credentials",
        shortName = "c",
        description = "Credentials JSON file (see https://developers.google.com/workspace/guides/create-credentials#desktop)"
    ).default("credentials.json")
    val spreadsheetID by option(
        ArgType.String,
        fullName = "spreadsheet",
        description = "the ID of the target spreadsheet",
    ).required()
    val tokensDirectory by option(
        ArgType.String,
        fullName = "tokens-directory",
        description = "directory for storing (and subsequently reading) the Sheets tokens",
    ).default(TOKENS_DIRECTORY_PATH)
    val columnsToExclude by option(
        ArgType.String,
        fullName = "exclude-column",
        shortName = "e",
        description = "name of column to exclude from upload (repeat for each column)",
    ).multiple()

    override fun execute() {
        DEFAULT_LOG_LEVEL = if (debug == true) LogLevel.DEBUG else LogLevel.INFO

        val tmpDirectory = createTempDirectory()
        logger.debug { "will save export to temporary directory $tmpDirectory" }

        runBlocking {
            checkApiToken(QualtricsDatacenter(datacenter), apiToken)
            downloadSurvey(QualtricsDatacenter(datacenter), apiToken, surveyID, tmpDirectory, outputFilename, FileExistsStrategy.ABORT)
        }

        var csvFile: Path? = null
        for (file in Files.list(tmpDirectory)) {
            if (file.extension == "csv") {
                csvFile = file
                break
            }
        }

        if (csvFile == null) {
            throw RuntimeException("failed to find CSV file in $tmpDirectory")
        }

        logger.debug { "determined the exported CSV file to be $csvFile" }

        if (saveExportDirectory != null) {
            makeCopyOfCSV(csvFile, saveExportDirectory!!, ifExists)
        }

        val tokensDirectoryPath = tokensDirectory.toPath().toFile()
        val client = SheetsClient(credentials.toPath(), tokensDirectoryPath).getClient()
        uploadCSV(client, spreadsheetID, csvFile, columnsToExclude)

        logger.debug { "*not* cleaning up temporary directory $tmpDirectory" }

        logger.info { "updated https://docs.google.com/spreadsheets/d/$spreadsheetID/edit" }

        subcommandFinished = true
    }
}
