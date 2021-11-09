package q2s.util

import logging.KotlinLogging
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.notExists

private val logger = KotlinLogging.logger { }

/**
 * Make a copy of the given file to the target directory, following given strategy if it exists
 */
fun makeCopyOfCSV(sourceFile: Path, targetDirectory: String, fileExistsStrategy: FileExistsStrategy) {
    val localCopyDirectory = targetDirectory.toPath()
    if (localCopyDirectory.notExists()) {
        logger.debug { "creating $localCopyDirectory to put copy of file there" }
        localCopyDirectory.createDirectories()
    } else if (!localCopyDirectory.isDirectory()) {
        logger.error { "$localCopyDirectory is not a directory; skipping saving a local copy" }
        return
    }

    val copyPath = localCopyDirectory.resolve(sourceFile.fileName)
    logger.debug { "will copy the CSV file to $copyPath" }

    if (copyPath.exists()) {
        when (fileExistsStrategy) {
            FileExistsStrategy.ABORT -> {
                logger.warning { "file already exists, aborting: $copyPath" }
                return
            }
            FileExistsStrategy.OVERWRITE -> {
                logger.debug { "file already exists, overwriting: $copyPath" }
                Files.copy(sourceFile, copyPath, StandardCopyOption.REPLACE_EXISTING)
            }

            FileExistsStrategy.BACKUP -> {
                val backedUpFile = renameFileForBackup(copyPath)
                logger.debug { "file already exists, moved for backup to: $backedUpFile" }
                Files.copy(sourceFile, copyPath)
            }
        }
    } else {
        Files.copy(sourceFile, copyPath)
    }
}
