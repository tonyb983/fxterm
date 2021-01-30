package io.imtony.vdrive.fxterm.google.ext.sheets

import com.google.api.services.sheets.v4.model.ValueRange

/**
 * Gets the content of this [ValueRange] as a [List] of [List] pf [Any]. Use [ignoreFirstRow] if
 * you expect the first row to have headers.
 */
fun ValueRange.asAnyAnyList(ignoreFirstRow: Boolean = true): List<List<Any?>> {
  val values = this.getValues()
  if (ignoreFirstRow) {
    values.removeFirst()
  }
  return values
}

/**
 * Gets the content of this [ValueRange] as a [MutableList] of [MutableList] pf [Any]. Use [ignoreFirstRow] if
 * you expect the first row to have headers.
 */
fun ValueRange.asAnyAnyMutableList(ignoreFirstRow: Boolean = true): MutableList<MutableList<Any?>> {
  val values = this.getValues()
  if (ignoreFirstRow) {
    values.removeFirst()
  }
  return values
}
