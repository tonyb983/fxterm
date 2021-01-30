package io.imtony.vdrive.fxterm.google.services

import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.Spreadsheet
import io.imtony.vdrive.fxterm.google.ext.coExecute
import io.imtony.vdrive.fxterm.google.ext.executeAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred

/**
 * The main [Sheets] service.
 */
interface GoogleSheetsService : GoogleService<Sheets> {
  companion object {
    /**
     * Constructor function for a [GoogleSheetsService].
     */
    fun create(serviceInitializer: ServiceInitializer): GoogleSheetsService =
      GoogleSheetsServiceImpl(serviceInitializer)
  }
}

/**
 * Loads the spreadsheet with the given [id]. Use [fetchData] to control performance, when false
 * it will only load [Spreadsheet] "meta" data, not the data contained by each cell.
 */
fun GoogleSheetsService.loadEntireSpreadsheet(
  id: String,
  fetchData: Boolean = false
): Spreadsheet? = service
  .spreadsheets()
  .get(id)
  .setIncludeGridData(fetchData)
  .execute()

/**
 * Loads the spreadsheet with the given [id]. Use [fetchData] to control performance, when false
 * it will only load [Spreadsheet] "meta" data, not the data contained by each cell.
 */
suspend fun GoogleSheetsService.loadEntireSpreadsheetCoroutine(
  id: String,
  fetchData: Boolean = false,
  scope: CoroutineScope? = null,
): Spreadsheet? = service
  .spreadsheets()
  .get(id)
  .setIncludeGridData(fetchData)
  .let {
    return if (scope == null) {
      it.coExecute()
    } else {
      it.coExecute(scope)
    }
  }

/**
 * Loads the spreadsheet with the given [id]. Use [fetchData] to control performance, when false
 * it will only load [Spreadsheet] "meta" data, not the data contained by each cell.
 */
fun GoogleSheetsService.loadEntireSpreadsheetAsync(
  id: String,
  fetchData: Boolean = false,
  scope: CoroutineScope? = null
): Deferred<Spreadsheet?> = service
  .spreadsheets()
  .get(id)
  .setIncludeGridData(fetchData)
  .let {
    return if (scope == null) {
      it.executeAsync()
    } else {
      it.executeAsync(scope)
    }
  }

private class GoogleSheetsServiceImpl(serviceCreator: ServiceInitializer) : GoogleSheetsService,
  GenericService<Sheets>(lazy { serviceCreator.createSheets() })
