package q2s.sheets

import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import kotlinx.cli.default
import kotlinx.cli.multiple
import kotlinx.cli.required
import q2s.subcommandFinished
import q2s.util.toPath

@ExperimentalCli
class UploadCSVCommand : Subcommand("upload_csv", "(only) upload a CSV file") {
    val credentials by option(
        ArgType.String,
        fullName = "credentials",
        shortName = "c",
        description = "Credentials JSON file (see https://developers.google.com/workspace/guides/create-credentials#desktop)"
    ).default("credentials.json")
    val spreadsheetID by option(
        ArgType.String,
        fullName = "spreadsheet",
        shortName = "s",
        description = "the ID of the target spreadsheet",
    ).required()
    val csvFile by option(
        ArgType.String,
        fullName = "csv-file",
        shortName = "f",
        description = "the path to the CSV file to upload"
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
        val tokensDirectoryPath = tokensDirectory.toPath().toFile()
        val client = SheetsClient(credentials.toPath(), tokensDirectoryPath).getClient()
        uploadCSV(client, spreadsheetID, csvFile.toPath(), columnsToExclude)

        subcommandFinished = true
    }
}
