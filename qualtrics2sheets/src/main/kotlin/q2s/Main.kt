package q2s

import kotlinx.cli.ArgParser
import logging.KotlinLogging
import q2s.qualtrics.DownloadQualtricsCommand
import q2s.sheets.UploadCSVCommand

const val PROGRAM_NAME = "qualtrics2csv"

private val logger = KotlinLogging.logger { }

var subcommandFinished = false

// @OptIn(ExperimentalCli::class)
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
        System.err.println("--help or -h for usage")
    }
}
