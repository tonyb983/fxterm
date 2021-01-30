package io.imtony.vdrive.fxterm.utils

import kotlin.reflect.KFunction1
import kotlin.reflect.KProperty

/**
 * Guard statement that runs the given [block] if [this] is null.
 */
inline infix fun <T> T?.guard(block: () -> Nothing): T = this ?: block()

/**
 * Guards against [this] and [prop] being null.
 */
inline fun <T, TProp> T?.guardProperty(prop: KProperty<TProp?>?, block: () -> Nothing): T =
  if (this != null && prop?.call() != null) {
    this
  } else {
    block()
  }

/**
 * Guards against [this] and [prop] being null.
 */
inline fun <T, TProp> T?.guardProperty(prop: KFunction1<T?, TProp?>?, block: () -> Nothing) =
  if (this != null && prop?.invoke(this) != null) {
    this
  } else {
    block()
  }
