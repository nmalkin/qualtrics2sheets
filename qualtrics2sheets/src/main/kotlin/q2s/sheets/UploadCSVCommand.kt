package q2s.sheets

import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import kotlinx.cli.default
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

    override fun execute() {
        val client = SheetsClient(credentials.toPath()).getClient()
        uploadCSV(client, spreadsheetID, csvFile.toPath())

        subcommandFinished = true
    }
}
