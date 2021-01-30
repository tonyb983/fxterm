package io.imtony.vdrive.fxterm.google.ext.sheets

import com.google.api.services.sheets.v4.Sheets

enum class SheetsMajorDimension {
  Rows,
  Columns;

  override fun toString(): String = when (this) {
    Rows -> "ROWS"
    Columns -> "COLUMNS"
  }
}

fun Sheets.Spreadsheets.Values.Get.setMajorDimension(smd: SheetsMajorDimension) = this.setMajorDimension(smd.toString())
fun Sheets.Spreadsheets.Values.BatchGet.setMajorDimension(smd: SheetsMajorDimension) =
  this.setMajorDimension(smd.toString())
