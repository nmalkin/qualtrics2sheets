package q2s

import kotlinx.cli.ArgParser
import kotlinx.cli.ExperimentalCli
import logging.KotlinLogging
import q2s.qualtrics.DownloadQualtricsCommand
import q2s.sheets.UploadCSVCommand

const val PROGRAM_NAME = "qualtrics2csv"

private val logger = KotlinLogging.logger { }

var subcommandFinished = false

@ExperimentalCli
fun main(args: Array<String>) {
    val parser = ArgParser(PROGRAM_NAME)
    parser.subcommands(ExportAndUploadCommand(), UploadCSVCommand(), DownloadQualtricsCommand())

    try {
        parser.parse(args)
    } catch (err: Exception) {
        logger.error("execution failed: $err")
        err.printStackTrace()
        return
    }

    if (!subcommandFinished) {
        System.err.println("Welcome to $PROGRAM_NAME!\nThis program has several different subcommands.\nRe-run this command, appending --help or -h, to show usage information.")
    }
}
