package io.imtony.vdrive.fxterm.utils

import javafx.beans.binding.StringBinding
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import tornadofx.stringBinding

/**
 *
 */
class PrefixedString(
  prefix: String,
  value: String,
) {
  private val prefixProperty by lazy { SimpleStringProperty(prefix) }

  /**
   * The [prefix] [StringProperty] to apply to the inner [String].
   */
  fun prefixProperty(): StringProperty = SimpleStringProperty(prefix)

  /**
   * The prefix to apply to this [String].
   */
  var prefix: String
    get() = prefixProperty.get()
    set(value) = prefixProperty.set(value)

  private val innerValueProperty by lazy { SimpleStringProperty(value) }

  /**
   * The [innerValue] [StringProperty], representing the value of the [String]
   * minus the [prefix].
   */
  fun innerValueProperty(): StringProperty = innerValueProperty

  /**
   * The inner value of the [String] minus the [prefix].
   */
  var innerValue: String
    get() = innerValueProperty.get()
    set(value) = innerValueProperty.set(value)

  private val boundValue =
    stringBinding(prefixProperty.concat(innerValueProperty), prefixProperty, innerValueProperty) { this.get() }
  val valueBinding: StringBinding get() = boundValue

  /**
   * The "full" value of the [String]. Getting it returns the [prefix] plus
   * the [innerValue], setting it sets only the [innerValue].
   */
  var value: String
    get() = boundValue.get()
    set(value) = innerValueProperty.set(value)
}
