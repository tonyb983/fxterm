@file:Suppress("BlockingMethodInNonBlockingContext", "unused")

package io.imtony.vdrive.fxterm.google.ext.drive

import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveRequest
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import kotlinx.coroutines.*
import org.apache.http.Consts
import org.apache.http.entity.ContentType
import kotlin.coroutines.CoroutineContext

suspend fun <T> DriveRequest<T>.executeWithCoroutines(
  context: CoroutineContext = Dispatchers.IO
): T = withContext(context) { execute() }

suspend fun <T> DriveRequest<T>.executeWithCoroutines(
  scope: CoroutineScope
): T = withContext(scope.coroutineContext) { execute() }

suspend fun <T> DriveRequest<T>.executeAsync(
  scope: CoroutineScope
): Deferred<T> = scope.async {
  execute()
}

suspend fun Drive.createFile(
  folderId: String,
  mimeType: String,
  name: String
): String {
  val metadata = File().apply {
    parents = listOf(folderId)
    setMimeType(mimeType)
    setName(name)
  }

  return files()
    .create(metadata)
    .executeWithCoroutines()
    .id
}

suspend fun Drive.fetchOrCreateAppFolder(folderName: String): String {
  val folder = getAppFolder()

  return if (folder.isEmpty()) {
    val metadata = File().apply {
      name = folderName
      mimeType = APP_FOLDER.mimeType
    }

    files().create(metadata)
      .setFields("id")
      .executeWithCoroutines()
      .id
  } else {
    folder.files.first().id
  }
}

suspend fun Drive.queryFiles(): FileList = files().list().setSpaces("drive").executeWithCoroutines()

suspend fun Drive.getAppFolder(): FileList =
  files().list().setSpaces("drive").setQ("mimeType='${APP_FOLDER.mimeType}'").executeWithCoroutines()

/**
 * https://developers.google.com/drive/api/v3/mime-types
 */
val APP_FOLDER: ContentType = ContentType.create("application/vnd.google-apps.folder", Consts.ISO_8859_1)
