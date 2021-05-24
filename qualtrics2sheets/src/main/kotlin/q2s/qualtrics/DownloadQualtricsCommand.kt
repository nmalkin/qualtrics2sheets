package q2s.qualtrics

import kotlinx.cli.ArgType
import kotlinx.cli.Subcommand
import kotlinx.cli.required
import kotlinx.coroutines.runBlocking
import q2s.subcommandFinished
import q2s.util.toPath

class DownloadQualtricsCommand : Subcommand("download_qualtrics", "download a Qualtrics export") {
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
        shortName = "s",
        description = "Qualtrics survey ID"
    ).required()
    val targetDirectory by option(
        ArgType.String,
        fullName = "output",
        shortName = "o",
        description = "target directory where to put the downloaded CSV"
    ).required()

    override fun execute() {
        val targetPath = targetDirectory.toPath()

        runBlocking {
            checkApiToken(QualtricsDatacenter(datacenter), apiToken)
            downloadSurvey(QualtricsDatacenter(datacenter), apiToken, surveyID, targetPath)
        }

        subcommandFinished = true
    }
}
