package io.imtony.vdrive.fxterm.google.services

import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.Spreadsheet

interface GoogleSheetsService : GoogleService<Sheets> {
  companion object {
    fun create(serviceInitializer: ServiceInitializer): GoogleSheetsService =
      GoogleSheetsServiceImpl(serviceInitializer)
  }
}

private class GoogleSheetsServiceImpl(serviceCreator: ServiceInitializer) : GoogleSheetsService,
  GenericInjectedService<Sheets>(lazy { serviceCreator.createSheets() }) {
  //override val mainSheet: Spreadsheet by lazy { loadEntireSpreadsheet(Const.InputDataSheetId) }

//    val inspectionData: List<List<Any>> by lazy {
//      service
//        .Spreadsheets()
//        .Values()
//        .get(Const.InputDataSheetId, Const.NamedRanges.InspectionData)
//        .setMajorDimension(SheetsMajorDimension.Rows)
//        .execute()
//        .let {
//          val values = it.getValues()
//          require(values.size > 0) { "InspectionData returned less than 1 row." }
//          values.removeFirst()
//          return@let values
//        }
//    }
//
//    val topsData: List<List<Any>> by lazy {
//      service
//        .Spreadsheets()
//        .Values()
//        .get(Const.InputDataSheetId, Const.NamedRanges.TopsData)
//        .setMajorDimension(SheetsMajorDimension.Rows)
//        .execute()
//        .let {
//          val values = it.getValues()
//          require(values.size > 0) { "OwnerData returned less than 1 row." }
//          values.removeFirst()
//          return@let values
//        }
//    }

  fun loadEntireSpreadsheet(id: String): Spreadsheet = service.spreadsheets().get(id).setIncludeGridData(true).execute()

  fun loadSpreadsheetMetadata(id: String): Spreadsheet =
    service.spreadsheets().get(id).setIncludeGridData(false).execute()
}

