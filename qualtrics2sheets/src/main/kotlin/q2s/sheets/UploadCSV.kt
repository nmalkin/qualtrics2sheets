package q2s.sheets

import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange
import logging.KotlinLogging
import java.nio.file.Path

private val logger = KotlinLogging.logger { }

private fun getFirstSheetName(client: Sheets, sheetID: String): String {
    logger.debug { "determining the first sheet (tab) in $sheetID" }

    val sheets = client.spreadsheets().get(sheetID).setFields("sheets.properties").execute().sheets
    val firstSheet = sheets[0]
    return firstSheet.properties.title
}

fun uploadCSV(client: Sheets, sheetID: String, csvFilePath: Path, columnsToExclude: List<String>) {
    val csvContent = if (columnsToExclude.isEmpty())
        readCSV(csvFilePath)
    else
        readAndFilterCSV(csvFilePath, columnsToExclude.toSet())

    val newBody = ValueRange().setValues(csvContent)

    val range = getFirstSheetName(client, sheetID)

    logger.debug { "overwriting sheet $range in spreadsheet $sheetID with data from $csvFilePath" }

    val result =
        client.spreadsheets().values().update(sheetID, range, newBody)
            .setValueInputOption("USER_ENTERED") // https://developers.google.com/sheets/api/reference/rest/v4/ValueInputOption
            .execute()

    logger.debug { "updated ${result.updatedCells} cells in sheet $range of spreadsheet $sheetID" }
}
