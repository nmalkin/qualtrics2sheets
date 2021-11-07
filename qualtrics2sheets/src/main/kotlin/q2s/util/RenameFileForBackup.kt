package q2s.util

import java.nio.file.Path
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.extension
import kotlin.io.path.moveTo
import kotlin.io.path.nameWithoutExtension

private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")

/**
 * Rename the given file to include the current time
 *
 * @return the path to the new file
 */
fun renameFileForBackup(fileToRename: Path): Path {
    val now: ZonedDateTime = ZonedDateTime.now()
    val formattedDateTime = now.format(dateTimeFormatter)
    val newFilename = "${fileToRename.nameWithoutExtension}_$formattedDateTime.${fileToRename.extension}"
    val newPath = fileToRename.resolveSibling(newFilename)

    fileToRename.moveTo(newPath)

    return newPath
}
