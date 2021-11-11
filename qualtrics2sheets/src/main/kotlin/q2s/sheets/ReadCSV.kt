package q2s.sheets

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.nio.file.Path
import kotlin.io.path.bufferedReader

/**
 * Read CSV from path and return its entire contents
 * @return a list for each row, each containing a list of that row's values
 */
fun readCSV(csvFilePath: Path): List<List<String>> {
    val csvFile = CSVParser.parse(csvFilePath.bufferedReader(), CSVFormat.DEFAULT)
    return csvFile.map { row -> row.toList() }
}

/**
 * Read CSV from path and return its contents, except for values in columns included in the filter set
 * @return a list for each row, each containing a list of that row's values
 */
fun readAndFilterCSV(csvFilePath: Path, columnNamesToFilter: Set<String>): List<List<String>> {
    val csvFile = CSVParser.parse(
        csvFilePath.bufferedReader(),
        CSVFormat.DEFAULT.builder().setHeader().build() // read the first row and use its values as column names
    )

    val newHeader = listOf(csvFile.headerNames.filter { !columnNamesToFilter.contains(it) })

    return newHeader +
        csvFile.map { row ->
            row.toMap().filterKeys {
                !columnNamesToFilter.contains(it)
            }.values.toList()
        }
}
