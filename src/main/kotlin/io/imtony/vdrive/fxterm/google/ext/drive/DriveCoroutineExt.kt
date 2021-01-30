@file:Suppress("BlockingMethodInNonBlockingContext", "unused")

package io.imtony.vdrive.fxterm.google.ext.drive

import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveRequest
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import io.imtony.vdrive.fxterm.google.ext.coExecute
import kotlinx.coroutines.*
import org.apache.http.Consts
import org.apache.http.entity.ContentType
import kotlin.coroutines.CoroutineContext

/**
 * Creates a new drive file with [parentId] as it's parent, [mimeType] as its [File.mimeType], and [name] as
 * it's [File.name], and then executes the creation using coroutines. The [context] can be used to
 * provide a custom [CoroutineContext].
 */
@Suppress("HardCodedDispatcher")
suspend fun Drive.createFileWithCoroutines(
  parentId: String,
  mimeType: String,
  name: String,
  context: CoroutineContext = Dispatchers.IO,
): String {
  val metadata = File().apply {
    parents = listOf(parentId)
    setMimeType(mimeType)
    setName(name)
  }

  return files()
    .create(metadata)
    .coExecute(context)
    .id
}

/**
 * Creates a new drive file with [parentId] as it's parent, [mimeType] as its [File.mimeType], and [name] as
 * it's [File.name], and then executes the creation using coroutines. The [scope] can be used to
 * provide a custom [CoroutineScope].
 */
suspend fun Drive.createFileWithCoroutines(
  parentId: String,
  mimeType: String,
  name: String,
  scope: CoroutineScope,
): String = createFileWithCoroutines(parentId, mimeType, name, scope.coroutineContext)

/**
 * Fetch or create app folder with coroutines. The [context] can be used to provide a custom [CoroutineContext].
 */
@Suppress("HardCodedDispatcher")
suspend fun Drive.fetchOrCreateAppFolderWithCoroutines(
  context: CoroutineContext = Dispatchers.IO
): String {
  val folder = getAppFolder()

  return if (folder == null) {
    val metadata = File().apply {
      mimeType = MimeType.googleFolder.mime
      parents = listOf("appDataFolder")
    }

    files().create(metadata)
      .setFields("id")
      .coExecute(context)
      .id
  } else {
    folder.id
  }
}

/**
 * Fetch or create app folder with coroutines. The [scope] can be used to provide a custom [CoroutineScope].
 */
suspend fun Drive.fetchOrCreateAppFolderWithCoroutines(
  scope: CoroutineScope
): String = fetchOrCreateAppFolderWithCoroutines(scope.coroutineContext)

/**
 * Creates and executes a new [Drive.Files.List] with [setDefaultSpaces].
 */
@Suppress("HardCodedDispatcher")
suspend fun Drive.queryFilesWithCoroutines(
  context: CoroutineContext = Dispatchers.IO
): FileList = files()
  .list()
  .setDefaultSpaces()
  .coExecute(context)

/**
 * Gets the appDataFolder or returns null.
 */
@Suppress("HardCodedDispatcher")
suspend fun Drive.getAppFolder(
  context: CoroutineContext = Dispatchers.IO
): File? = files()
  .list()
  .setSpaces(DriveSpaces.AppDataFolder)
  .setQ("mimeType='${MimeType.googleFolder}' and 'appDataFolder' in parents")
  .coExecute(context)
  .files
  .firstOrNull()

/**
 * https://developers.google.com/drive/api/v3/mime-types.
 */
val APP_FOLDER: ContentType = ContentType.create(MimeType.googleFolder.mime, Consts.ISO_8859_1)
