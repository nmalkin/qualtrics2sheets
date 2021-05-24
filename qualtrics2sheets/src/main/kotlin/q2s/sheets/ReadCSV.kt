package q2s.sheets

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.nio.file.Path
import kotlin.io.path.bufferedReader

fun readCSV(csvFilePath: Path): List<List<String>> {
    val csvFile = CSVParser.parse(csvFilePath.bufferedReader(), CSVFormat.DEFAULT)
    return csvFile.map { row -> row.toList() }.toList()
}
