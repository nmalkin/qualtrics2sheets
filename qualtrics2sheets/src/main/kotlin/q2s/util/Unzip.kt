package q2s.util

import logging.KotlinLogging
import java.io.InputStream
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

private val logger = KotlinLogging.logger { }

/**
 * Unzip stream to given directory
 * via https://stackoverflow.com/a/59581898
 */
fun unzip(
    inputStream: InputStream,
    targetDirectory: Path,
    fileExistsStrategy: FileExistsStrategy = FileExistsStrategy.ABORT
) {
    val resolvedTarget: Path = targetDirectory.toAbsolutePath()
    ZipInputStream(inputStream).use { zipStream ->
        while (true) {
            val entry: ZipEntry = zipStream.nextEntry ?: break

            val entryPath: Path = resolvedTarget.resolve(entry.name).normalize()

            // Check for zip slip: https://snyk.io/research/zip-slip-vulnerability
            if (!entryPath.startsWith(resolvedTarget)) {
                throw RuntimeException("Entry with an illegal path: ${entry.name}")
            }

            if (entry.isDirectory) {
                Files.createDirectories(entryPath)
            } else {
                Files.createDirectories(entryPath.parent)
                try {
                    Files.copy(zipStream, entryPath)
                } catch (e: FileAlreadyExistsException) {
                    when (fileExistsStrategy) {
                        FileExistsStrategy.ABORT -> {
                            logger.warning { "file already exists, aborting: $entryPath" }
                            return
                        }
                        FileExistsStrategy.OVERWRITE -> {
                            logger.info { "file already exists, overwriting: $entryPath" }
                            Files.copy(zipStream, entryPath, StandardCopyOption.REPLACE_EXISTING)
                        }

                        FileExistsStrategy.BACKUP -> {
                            val backedUpFile = renameFileForBackup(entryPath)
                            logger.info { "file already exists, moved for backup to: $backedUpFile" }
                            Files.copy(zipStream, entryPath)
                        }
                    }
                }
            }
        }
    }
}
