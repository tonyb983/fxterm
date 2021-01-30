package io.imtony.vdrive.fxterm.utils

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KFunction1
import kotlin.reflect.KProperty

/**
 * Guard statement that runs the given [block] if [this] is null.
 */
inline infix fun <T> T?.guard(block: () -> Nothing): T {
  contract {
    callsInPlace(block, InvocationKind.AT_MOST_ONCE)
  }

  return this ?: block()
}

/**
 * Guards against [this] and [prop] being null.
 */
inline fun <T, TProp> T?.guardProperty(prop: KProperty<TProp?>?, block: () -> Nothing): T {
  contract {
    callsInPlace(block, InvocationKind.AT_MOST_ONCE)
  }

  return if (this != null && prop?.call() != null) {
    this
  } else {
    block()
  }
}

/**
 * Guards against [this] and [prop] being null.
 */
inline fun <T, TProp> T?.guardProperty(prop: KFunction1<T?, TProp?>?, block: () -> Nothing): T =
  if (this != null && prop?.invoke(this) != null) {
    this
  } else {
    block()
  }

/**
 * Gets this value or [default] if it is null.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <T> T?.valueOrDefault(default: T): T = this ?: default

/**
 * Runs the given [block] if this value is not null.
 */
inline fun <T> T?.ifNotNull(block: (T) -> Unit): Unit {
  contract {
    callsInPlace(block, InvocationKind.AT_MOST_ONCE)
  }

  if (this != null) {
    block.invoke(this)
  }
}

/**
 * Runs the given [block] if this value is null.
 */
inline fun <T> T?.ifNull(block: () -> Unit): Unit {
  contract {
    callsInPlace(block, InvocationKind.AT_MOST_ONCE)
  }

  if (this == null) {
    block.invoke()
  }
}
