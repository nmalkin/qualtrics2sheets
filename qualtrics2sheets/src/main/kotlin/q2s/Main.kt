package q2s

import kotlinx.cli.ArgParser
import q2s.qualtrics.DownloadQualtricsCommand
import q2s.sheets.UploadCSVCommand

const val PROGRAM_NAME = "qualtrics2csv"

var subcommandFinished = false

// @OptIn(ExperimentalCli::class)
fun main(args: Array<String>) {
    val parser = ArgParser(PROGRAM_NAME)
    parser.subcommands(ExportAndUploadCommand(), UploadCSVCommand(), DownloadQualtricsCommand())
    parser.parse(args)

    if (!subcommandFinished) {
        System.err.println("--help or -h for usage")
    }
}
