package io.imtony.vdrive.fxterm.utils

import javafx.beans.binding.BooleanBinding
import javafx.beans.property.ObjectProperty
import tornadofx.objectProperty

/**
 * Simple [ObjectProperty] to hold a lock. Use [startLocked] to determine
 * if the lock will begin it's life locked or unlocked.
 */
class LockProperty(startLocked: Boolean = false) {
  private val obj: ObjectProperty<Any?> = objectProperty(if (startLocked) null else Any())

  /**
   * [Boolean] value of whether the lock is locked.
   */
  val isLocked: Boolean get() = obj.value == null

  /**
   * [Boolean] value of whether the lock is unlocked.
   */
  val isUnlocked: Boolean get() = obj.value != null

  /**
   * [BooleanBinding] bound to the [isLocked] state.
   */
  val whenLocked: BooleanBinding get() = obj.isNull

  /**
   * [BooleanBinding] bound to the [isUnlocked] state.
   */
  val whenUnlocked: BooleanBinding get() = obj.isNotNull

  /**
   * If [isUnlocked] set to locked.
   */
  fun lock() {
    if (isUnlocked) {
      obj.set(null)
    }
  }

  /**
   * If [isLocked] set to unlocked.
   */
  fun unlock() {
    if (isLocked) {
      obj.set(Any())
    }
  }

  /**
   * Toggle the lock state.
   */
  fun toggle(): Unit = if (isLocked) obj.set(Any()) else obj.set(null)
}

/**
 * Convenience property to create a new [LockProperty]
 */
val lockProperty: LockProperty get() = LockProperty()
