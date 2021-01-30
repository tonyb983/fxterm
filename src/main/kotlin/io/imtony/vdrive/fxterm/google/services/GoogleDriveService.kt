package io.imtony.vdrive.fxterm.google.services

import com.google.api.services.drive.Drive
import io.imtony.vdrive.fxterm.google.ext.drive.*
import java.io.ByteArrayOutputStream
import com.google.api.services.drive.model.File as DriveFile
import com.google.api.services.drive.model.FileList as DriveFileList

data class FileSearchParameters(
  val query: String? = null,
  val spaces: String? = null,
  val spacesType: DriveSpaces? = null,
  val corpora: String? = null,
  val corporaType: DriveCorporas? = null,
)

private fun Drive.Files.List.applyParams(fsp: FileSearchParameters) = this.apply {
  if (fsp.corpora != null) {
    corpora = fsp.corpora
  } else if (fsp.corporaType != null) {
    setCorpora(fsp.corporaType)
  }

  if (fsp.spaces != null) {
    spaces = fsp.spaces
  } else if (fsp.spacesType != null) {
    setSpaces(fsp.spacesType)
  }

  if (fsp.query != null) {
    q = fsp.query
  }
}

val Drive.Files.List.defaultSpace get() = DriveSpaces.Drive
val Drive.Files.List.defaultCorpora get() = DriveCorporas.User
fun Drive.Files.List.onlyNameAndId(): Drive.Files.List = this.setFields("files(id, name)")
fun Drive.Files.List.nameIdAndMax(max: Int): Drive.Files.List =
  this.setFields("maxResults=$max,nextPageToken,files(id, name)")

fun Drive.Files.List.setDefaults(): Drive.Files.List = this
  .setSpaces(defaultSpace)
  .setCorpora(defaultCorpora)
  .setQ("trashed = false")

interface GoogleDriveService : GoogleService<Drive> {
  override val service: Drive

  companion object {
    fun create(serviceInitializer: ServiceInitializer): GoogleDriveService = GoogleDriveServiceImpl(serviceInitializer)
  }
}


private class GoogleDriveServiceImpl(serviceCreator: ServiceInitializer) : GoogleDriveService,
  GenericInjectedService<Drive>(lazy { serviceCreator.createDrive() }) {
  var defaultCorporas: DriveCorporas = DriveCorporas.User
  var defaultSpaces: DriveSpaces = DriveSpaces.Drive

  val fileList: DriveFileList by lazy {
    service
      .files()
      .list()
      .setDefaults()
      .execute()
  }

  val allFiles: List<DriveFile> by lazy {
    service
      .files()
      .list()
      .setDefaults()
      .onlyNameAndId()
      .execute()
      .files
  }

  fun fetchFiles(config: Drive.Files.List.() -> Unit): DriveFileList? = service
    .files()
    .list()
    .setDefaults()
    .apply(config)
    .execute()

  fun fetchFolders(name: String, exact: Boolean): DriveFileList? = fetchFiles {
    this.setDefaults()
    fields = "files(id, name, mimeType)"
    q =
      "trashed = false and ${MimeType.googleFolder.toGoogleRequestQuery()} and name ${if (exact) "=" else "contains"} '${name}'"
  }

  fun createFile(name: String, mimeType: MimeType?, parentId: String?): DriveFile = createFile(name) {
    if (mimeType != null) this.mimeType = mimeType.toString()
    if (parentId != null) this.parents = listOf(parentId)
  }

  fun createFile(
    name: String?,
    setCreateOptions: DriveFile.() -> Unit
  ): DriveFile {
    val file = DriveFile()

    if (name != null) file.name = name
    setCreateOptions.invoke(file)

    return service.files().create(file).execute()
  }

  fun copyFile(originalId: String, newName: String, modification: (DriveFile.() -> Unit)?): DriveFile = service
    .files()
    .copy(originalId, DriveFile().setName(newName).apply { modification?.invoke(this) })
    .execute()

  fun downloadAsPdf(fileId: String) =
    ByteArrayOutputStream().apply { service.files().export(fileId, "application/pdf").executeMediaAndDownloadTo(this) }

  /**
   * Fetches all files of the given *[mimeType]*. The function will by default only fetch the
   * id, name, and mimType fields of the files, add any other file properties you'd like to
   * gather to the *[otherFileProps]* variable, they will be joined by commas and appended.
   * #### Returns a *[DriveFileList]* containing any files which match the given mimeType.
   */
  fun fetchFilesOfType(mimeType: MimeType, vararg otherFileProps: String): DriveFileList? = fetchFiles {
    fields = "files(id,name,mimeType${if (otherFileProps.isNotEmpty()) ",${otherFileProps.joinToString(",")}" else ""})"
    q = mimeType.toGoogleRequestQuery()
  }
}
