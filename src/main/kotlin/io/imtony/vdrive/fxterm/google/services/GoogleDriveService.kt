package io.imtony.vdrive.fxterm.google.services

import com.google.api.services.drive.Drive
import io.imtony.vdrive.fxterm.google.ext.drive.*
import io.imtony.vdrive.fxterm.utils.ifNotNull
import java.io.ByteArrayOutputStream
import com.google.api.services.drive.model.File as DriveFile
import com.google.api.services.drive.model.FileList as DriveFileList

/**
 * Wraps the query modifiers that can be applied to [Drive.Files.List].
 *
 * @property q The Query parameter.
 * @property corpora The corpora parameter.
 * @property space The spaces parameter.
 * @property fields The fields parameter.
 * @property maxResults The max results returned.
 */
data class FileSearchParameters(
  /**
   * The Query parameter.
   */
  val q: String? = null,
  /**
   * The corpora parameter.
   */
  val corpora: String? = null,
  /**
   * The spaces parameter.
   */
  val space: String? = null,
  /**
   * The fields parameter.
   */
  val fields: String? = null,
  /**
   * The max results returned. Note, setting this will automatically also set
   * **nextPageToken** and **files(id,name,mimeType)** on the fields parameter
   * if no field parameter is supplied.
   */
  val maxResults: Int? = null,
)

/**
 * Applies the given [any non-null members of fsp][FileSearchParameters] to this [Drive.Files.List] request.
 */
fun Drive.Files.List.applyParams(fsp: FileSearchParameters): Drive.Files.List = this.apply {
  fsp.corpora.ifNotNull {
    corpora = it
  }

  fsp.space.ifNotNull {
    spaces = it
  }

  fsp.q.ifNotNull {
    q = it
  }

  if (fsp.fields != null && fsp.maxResults != null) {
    fields = "maxResults=${fsp.maxResults},nextPageToken,${fsp.fields}"
  } else {
    fsp.fields.ifNotNull {
      fields = it
    }

    fsp.maxResults.ifNotNull {
      fields = "maxResults=${fsp.maxResults},nextPageToken,files(id,name,mimeType)"
    }
  }
}

/**
 * Creates and executes an [Drive.Files.Export] request and returns the [ByteArrayOutputStream] it
 * has been piped into.
 */
fun Drive.downloadFile(id: String, type: MimeType = MimeType.pdf): ByteArrayOutputStream = ByteArrayOutputStream()
  .apply {
    files()
      .export(id, type.mime)
      .executeMediaAndDownloadTo(this)
  }

private const val MIN_PAGE_SIZE = 1
private const val MAX_PAGE_SIZE = 1000

/**
 * Sets this [Drive.Files.List] request to use the maximum result page size.
 *
 * #### Google's page maximum is <u>1000</u>.
 */
fun Drive.Files.List.useMaxPageCount(): Drive.Files.List = this.setPageSize(MAX_PAGE_SIZE)

/**
 * Sets the fields to be returned from this request as id and name only.
 */
fun Drive.Files.List.onlyNameAndId(): Drive.Files.List = this.setFields("files(id,name)")

/**
 * Sets the fields to be returned from this request as id, name, and mimeType only.
 */
fun Drive.Files.List.onlyNameMimeAndId(): Drive.Files.List = this.setFields("files(id,name,mimeType)")

/**
 * Sets the fields to be returned from this request as id, name, and the nextPageToken only, and sets
 * max results at a time as [max].
 */
fun Drive.Files.List.nameIdAndMax(max: Int): Drive.Files.List =
  this.setFields("maxResults=$max,nextPageToken,files(id,name)")

/**
 * Sets the fields to be returned from this request as id, name, mimeType, and the nextPageToken only, and sets
 * max results at a time as [max].
 */
fun Drive.Files.List.nameMimeIdAndMax(max: Int): Drive.Files.List =
  this.setFields("maxResults=$max,nextPageToken,files(id,name,mimeType)")

/**
 * Sets the space to [defaultSpace], sets the corpora to [defaultCorpora], and sets Q to be "trashed = false".
 */
fun Drive.Files.List.setDefaults(): Drive.Files.List = this
  .setSpaces(defaultSpace)
  .setCorpora(defaultCorpora)
  .setQ("trashed = false")

/**
 * The main interface used to interact with the Google [Drive] service.
 */
interface GoogleDriveService : GoogleService<Drive> {
  /**
   * The actual service.
   */
  override val service: Drive

  companion object {
    /**
     * Constructor function for a [GoogleDriveService].
     */
    fun create(serviceInitializer: ServiceInitializer): GoogleDriveService = GoogleDriveServiceImpl(serviceInitializer)
  }
}

private class GoogleDriveServiceImpl(serviceCreator: ServiceInitializer) : GoogleDriveService,
  GenericService<Drive>(lazy { serviceCreator.createDrive() }) {
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
    q = "trashed = false" +
      " and " +
      MimeType.googleFolder.toGoogleRequestQuery() +
      " and " +
      "name ${if (exact) "=" else "contains"} '$name'"
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

  fun downloadAsPdf(fileId: String) = ByteArrayOutputStream()
    .apply {
      service
        .files()
        .export(fileId, "application/pdf")
        .executeMediaAndDownloadTo(this)
    }

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
