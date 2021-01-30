package io.imtony.vdrive.fxterm.utils

import javafx.util.StringConverter

/**
 * A [StringConverter] implementation which forces a [prefix] onto a [String].
 */

class ConstStringPrefixer(private val prefix: String) : StringConverter<String>() {
  /**
   * Modifies the [input] [String] to be prefixed by the store [prefix].
   */
  override fun toString(input: String?): String = "${prefix}${input.thisOrEmpty()}"

  /**
   * Converts back to normal string by removing the stored [prefix] if it exists
   * on the input string.
   */
  override fun fromString(string: String?): String = string?.removePrefix(prefix) ?: empty_
}
