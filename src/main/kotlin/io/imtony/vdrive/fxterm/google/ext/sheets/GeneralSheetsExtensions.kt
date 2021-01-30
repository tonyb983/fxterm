package io.imtony.vdrive.fxterm.google.ext.sheets

import com.google.api.services.sheets.v4.model.ValueRange

fun ValueRange.asAnyAnyList(ignoreFirstRow: Boolean = true): List<List<Any?>> {
  val values = this.getValues()
  if (ignoreFirstRow) {
    values.removeFirst()
  }
  return values
}

fun ValueRange.asAnyAnyMutableList(ignoreFirstRow: Boolean = true): MutableList<MutableList<Any?>> {
  val values = this.getValues()
  if (ignoreFirstRow) {
    values.removeFirst()
  }
  return values
}
