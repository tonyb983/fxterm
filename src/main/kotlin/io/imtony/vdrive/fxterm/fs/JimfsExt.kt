package io.imtony.vdrive.fxterm.fs

import com.google.common.jimfs.Jimfs
import java.io.File
import java.nio.file.FileSystem
import java.nio.file.Path
import java.nio.file.spi.FileSystemProvider


/**
 * Returns true if this [FileSystemProvider] is [Jimfs].
 */
fun FileSystemProvider.isJimfs(): Boolean = this.scheme == Jimfs.URI_SCHEME

/**
 * Returns true if this [FileSystem] is [Jimfs].
 */
fun FileSystem.isJimfs(): Boolean = this.provider().isJimfs()

/**
 * Returns true if this [File] is [Jimfs].
 */
fun File.isJimfs(): Boolean = this.toURI().scheme == Jimfs.URI_SCHEME

/**
 * Returns true if this [Path] is [Jimfs].
 */
fun Path.isJimfs(): Boolean = this.toUri().scheme == Jimfs.URI_SCHEME
