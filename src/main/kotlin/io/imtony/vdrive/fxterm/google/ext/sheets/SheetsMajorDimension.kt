package io.imtony.vdrive.fxterm.google.ext.sheets

import com.google.api.services.sheets.v4.Sheets

/**
 * The Major Dimensions for a [Sheets.Spreadsheets.Values] request.
 */
enum class SheetsMajorDimension {
  /** Get the values as Rows. */
  Rows,

  /** Get the values as Columns. */
  Columns;

  override fun toString(): String = when (this) {
    Rows -> "ROWS"
    Columns -> "COLUMNS"
  }
}

/**
 * Set the major dimension for this Get request.
 */
fun Sheets.Spreadsheets.Values.Get.setMajorDimension(
  smd: SheetsMajorDimension
): Sheets.Spreadsheets.Values.Get = this.setMajorDimension(smd.toString())

/**
 * Set the major dimension for this BatchGet request.
 */
fun Sheets.Spreadsheets.Values.BatchGet.setMajorDimension(
  smd: SheetsMajorDimension
): Sheets.Spreadsheets.Values.BatchGet =
  this.setMajorDimension(smd.toString())
