@file:Suppress("unused")

package io.imtony.vdrive.fxterm.fs

import com.google.api.client.util.DateTime
import io.imtony.vdrive.fxterm.google.ext.drive.MimeType
import io.imtony.vdrive.fxterm.utils.empty
import java.nio.ByteBuffer
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributeView
import java.nio.file.attribute.FileTime
import java.nio.file.attribute.UserDefinedFileAttributeView
import kotlin.io.path.fileAttributesView
import kotlin.io.path.fileAttributesViewOrNull
import com.google.api.services.drive.model.File as DriveFile

private fun DateTime.toFileTime(): FileTime = FileTime.fromMillis(this.value)

private const val DRIVE_ID_ATTR = "user.driveId"
private const val MIME_TYPE_ATTR = "user.mimeType"

/**
 * Writes a [DriveFile]'s metadata to this [Path] object.
 */
fun Path.writeDriveMetadata(file: DriveFile): Boolean {
  this.fileAttributesViewOrNull<BasicFileAttributeView>()
    ?.setTimes(file.modifiedTime?.toFileTime(), null, file.createdTime?.toFileTime())
  return writeUserFileAttribute(this, DRIVE_ID_ATTR, file.id) &&
    writeUserFileAttribute(this, MIME_TYPE_ATTR, file.mimeType)
}

/**
 * Write the given Google Drive [id] to this [Path] object.
 */
fun Path.writeDriveId(id: String?): Boolean = if (id == null) {
  false
} else {
  writeUserFileAttribute(this, DRIVE_ID_ATTR, id)
}

/**
 * Write the given Google Drive [DriveFile]'s [id][DriveFile.id] to this [Path] object.
 */
fun Path.writeDriveId(file: DriveFile?): Boolean =
  if (file == null || file.id == null) {
    false
  } else {
    writeUserFileAttribute(this, DRIVE_ID_ATTR, file.id)
  }

/**
 * Attempts to read a [UserDefinedFileAttributeView] containing a previously saved Google Drive ID from
 * this [Path] object and returns it. If the ID has not been written or cannot be retrieved the
 * [empty][String.Companion.empty] [String] will be returned instead.
 */
fun Path.readDriveId(): String = readUserFileAttribute(
  this,
  DRIVE_ID_ATTR,
  checkExists = true,
  throwIfNotFound = false
)

/**
 * Write the given [DriveFile.mimeType] [String] to this [Path] object.
 */
fun Path.writeMimeType(driveFile: DriveFile?): Boolean = if (driveFile == null || driveFile.mimeType != null) {
  false
} else {
  writeUserFileAttribute(
    this,
    MIME_TYPE_ATTR,
    driveFile.mimeType,
  )
}

/**
 * Write the given [mimeType] [String] to this [Path] object.
 */
fun Path.writeMimeType(mimeType: MimeType?): Boolean = if (mimeType == null) false else {
  writeUserFileAttribute(
    this,
    MIME_TYPE_ATTR,
    mimeType.mime,
  )
}

/**
 * Write the given [mimeType] [String] to this [Path] object.
 */
fun Path.writeMimeType(mimeType: String?): Boolean = writeUserFileAttribute(
  this,
  MIME_TYPE_ATTR,
  mimeType,
)

/**
 *
 */
fun writeUserFileAttribute(path: Path, name: String?, value: String?): Boolean {
  if (name.isNullOrBlank()) {
    throw IllegalArgumentException(
      "Null or blank attribute name passed to writeUserFileAttribute." +
        " Path: $path Name: $name Value: $value"
    )
  }

  // Allow for blank attribute values for overwriting current values
  if (value == null) {
    throw IllegalArgumentException(
      "Null or blank attribute name passed to writeUserFileAttribute. " +
        "Path: $path Name: $name Value: $value"
    )
  }

  val view: UserDefinedFileAttributeView = path.fileAttributesView()
  val bytes = value.encodeToByteArray()
  val writeBuffer = ByteBuffer.allocate(bytes.size)
  writeBuffer.put(bytes)
  writeBuffer.flip()
  return view.write(name, writeBuffer) > 0
}

/**
 *
 */
fun readUserFileAttribute(path: Path, name: String?, checkExists: Boolean, throwIfNotFound: Boolean): String {
  if (name.isNullOrBlank()) {
    throw IllegalArgumentException(
      "Null or blank attribute name passed to readUserFileAttribute. " +
        "Path: $path Name: $name"
    )
  }

  val view: UserDefinedFileAttributeView = path.fileAttributesView()

  if (checkExists && !view.list().contains(name)) {
    return if (!throwIfNotFound) {
      String.empty
    } else {
      throw IllegalArgumentException("Attribute $name not found on Path '$path'.")
    }
  }

  val readBuffer = ByteBuffer.allocate(view.size(name))
  if (view.read(name, readBuffer) < 1) {
    return String.empty
  }

  return readBuffer.flip().array().decodeToString()
}
