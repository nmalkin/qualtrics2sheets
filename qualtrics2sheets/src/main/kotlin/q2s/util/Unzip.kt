package q2s.util

import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 * Unzip stream to given directory
 * via https://stackoverflow.com/a/59581898
 */
fun unzip(inputStream: InputStream, targetDirectory: Path) {
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
                Files.copy(zipStream, entryPath)
            }
        }
    }
}
