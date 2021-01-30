@file:Suppress("NOTHING_TO_INLINE")

package io.imtony.vdrive.fxterm.utils

internal const val empty_: String = ""

/**
 *  Constant empty [String] value.
 */
internal inline val String.Companion.empty get() = empty_

/**
 * Returns the value of this [String?] or an empty string.
 */
internal inline fun String?.thisOrEmpty(): String = this ?: empty_

internal const val windowsEnding = "\r\n"
internal const val nixEnding = "\n"

internal inline fun String.Companion.getLineEnding(): String = if (IS_NIX) nixEnding else windowsEnding

fun String.ensureLineBreak() = if (!this.endsWith('\n')) this else this + '\n'
