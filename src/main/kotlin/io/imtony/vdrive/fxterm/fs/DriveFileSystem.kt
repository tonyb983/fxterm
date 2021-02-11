package io.imtony.vdrive.fxterm.fs

import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.FileList
import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import dispatch.core.IOCoroutineScope
import dispatch.core.withIO
import io.imtony.vdrive.fxterm.google.ext.drive.*
import io.imtony.vdrive.fxterm.google.ext.executeAsync
import io.imtony.vdrive.fxterm.google.services.GoogleDriveService
import io.imtony.vdrive.fxterm.utils.emptyImmutableList
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.*
import mu.KLogging
import tornadofx.*
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.*
import kotlin.io.path.*
import kotlin.time.measureTime
import com.google.api.services.drive.model.File as DriveFile

/**
 * A virtual [FileSystem] created by [Jimfs] representing the Google Drive folder heirarchy.
 */
class DriveFileSystem private constructor(
  private val scope: CoroutineScope,
  private val driveService: GoogleDriveService
) {
  private val requestPageSize = 200
  private val fsName = "vdrive"
  private val className by lazy { this::class.qualifiedName }
  private val fsConfigBuilder: Configuration.Builder
    get() = Configuration
      .unix()
      .toBuilder()
      .setAttributeViews("basic", "user") // , "owner", "posix")

  private var fs: FileSystem by singleAssign()
  private val mappedFiles: MutableMap<String, Pair<DriveFile, Path>> = mutableMapOf()
  private val unmappedFiles: MutableMap<String, Pair<DriveFile, Path>> = mutableMapOf()
  private val unknownFiles: MutableSet<String> = mutableSetOf()

  private lateinit var cwd: Path

  private var isInit = false

  private suspend fun init() {
    logger.entry()
    try {
      runInit()
      isInit = true
    } catch (cancel: CancellationException) {
      logger.warn("Coroutine cancelled while file system was initializing.")
      isInit = false
    } catch (logged: Exception) {
      logger.error(logged) { "Exception thrown during file system initialization." }
      isInit = false
      throw logged
    } finally {
      logger.info(
        "File system " +
          "${if (isInit) "was initialized correctly" else "was errored during initialization"}."
      )
    }
    logger.exit()
  }

  private fun getCwd(): Path = cwd

  private fun createDefaultListRequest(
    withQ: (GoogleDriveQueryBuilder.() -> Unit)? = null,
    withFields: (DriveFilesListRequestFieldsBuilder.() -> Unit)? = null,
    sortOrder: DriveOrderBy? = null,
  ) = driveService.service.files().list()
    .setCorpora(DriveCorporas.User)
    .setSpaces(DriveSpaces.Drive)
    .setPageSize(requestPageSize)
    .apply {
      if (withQ != null) {
        this.buildQ(withQ)
      } else {
        this.buildQ {
          notTrashed()
        }
      }
    }.apply {
      if (withFields != null) {
        this.buildFields(withFields)
      } else {
        this.buildFields {
          nextPageToken
          files { fileSystemDefaults() }
        }
      }
    }
    .setOrderBy(sortOrder?.toString() ?: DriveOrderBy.Folder.add(DriveOrderBy.Name))

  private suspend fun runInit() {
    logger.entry()

    fs = Jimfs.newFileSystem(fsName, fsConfigBuilder.build())
    val root = fs.getPath("/")
    cwd = root

    // measureTimeMillis {
    //   deepMapping()
    // }.also { logger.info { "Mapped entire drive in '$it' ms." } }

    // Get all root files and directories from Google Drive
    val dirReq = createDefaultListRequest()
      .buildQ {
        and {
          onlyType(MimeType.googleFolder)
          notTrashed()
          isRoot()
        }
      }

    val fileReq = createDefaultListRequest()
      .buildQ {
        and {
          notType(MimeType.googleFolder)
          notTrashed()
          isRoot()
        }
      }

    var token = null as String?
    do {
      val dirs = dirReq.setPageToken(token).executeAsync(this.scope).await()
      mapListRequest(dirs, root)
      token = dirs.nextPageToken
    } while (token != null)

    token = null
    do {
      val files = fileReq.setPageToken(token).executeAsync(this.scope).await()
      mapListRequest(files, root)
      token = files.nextPageToken
    } while (token != null)

    logger.exit()
  }

  fun getRoot(): Path = fs.getPath("/")

  fun isValidPath(path: Path): Boolean = path.isJimfs() && path.exists()

  fun getDirContents(path: String): ImmutableList<Path> {
    if (!isInit) {
      throw RuntimeException("DriveFileSystem is not yet initialized.")
    }

    val p = fs.getPath(path)

    if (!p.isDirectory()) {
      logger.warn { "Cannot get the contents of a non-directory path. Path requested: '$path' '$p'" }
      return emptyImmutableList()
    }

    if (isValidPath(p)) {
      return p.listDirectoryEntries().toImmutableList().also {
        logger.info { "Found ${it.size} files in path '$path'" }
      }
    }

    val up = mutableListOf<String>()
    for (names in (p.nameCount - 1) downTo 0) {
      val sub = p.subpath(0, names)

      val mappedPath = mappedFiles.values.firstOrNull { it.second == sub }
      if (mappedPath != null) {

      }
    }

    logger.warn { "Requested path '$path' does not exist!" }
    return emptyImmutableList()
  }

  fun getFile(pathString: String): Path? {
    val path = runCatching {
      if (pathString.startsWith("/")) {
        fs.getPath(pathString)
      } else {
        cwd.resolve(pathString)
      }
    }.getOrElse {
      if (it is InvalidPathException) {
        logger.warn { "PathString represents invalid path: CWD: $cwd | PathString: $pathString" }
      } else {
        logger.error { "Unexpected exception trying to get path '$pathString'." }
      }

      return null
    }

    if (isValidPath(path)) {
      return path
    }

    return null
  }

  private suspend fun mapListRequestTimed(
    fileList: FileList,
    parentPath: Path? = null,
    isRoot: Boolean = false,
    createParents: Boolean = false,
    fetchAllPages: Boolean = false,
    scope: IOCoroutineScope = IOCoroutineScope(this@DriveFileSystem.scope.coroutineContext)
  ) = measureTime {
    mapListRequest(fileList, parentPath, isRoot, createParents, fetchAllPages, scope)
  }.also { logger.info { "Mapped ${fileList.files.size} Drive Files in ${it.inMilliseconds} ms." } }

  private suspend fun mapListRequest(
    fileList: FileList,
    parentPath: Path? = null,
    isRoot: Boolean = false,
    createParents: Boolean = false,
    fetchAllPages: Boolean = false,
    scope: IOCoroutineScope = IOCoroutineScope(this@DriveFileSystem.scope.coroutineContext)
  ) = mapFileList(fileList.files, parentPath, isRoot, createParents, fetchAllPages, scope)

  private suspend fun mapFileListTimed(
    files: List<DriveFile>,
    parentPath: Path? = null,
    isRoot: Boolean = false,
    createParents: Boolean = false,
    fetchAllPages: Boolean = false,
    scope: IOCoroutineScope = IOCoroutineScope(this@DriveFileSystem.scope.coroutineContext)
  ) = measureTime {
    mapFileList(files, parentPath, isRoot, createParents, fetchAllPages, scope)
  }.also { logger.info { "Mapped ${files.size} Drive Files in ${it.inMilliseconds} ms." } }

  private suspend fun mapFileList(
    files: List<DriveFile>,
    parentPath: Path? = null,
    isRoot: Boolean = false,
    createParents: Boolean = false,
    fetchAllPages: Boolean = false,
    scope: IOCoroutineScope = IOCoroutineScope(this@DriveFileSystem.scope.coroutineContext)
  ) = withIO(scope.coroutineContext) {
    for (file in files) {
      if (!this.isActive) {
        throw CancellationException()
      }

      runCatching {
        mapDriveFileJob(
          file,
          parentPath,
          isRoot,
          createParents,
        )
      }.onFailure {
        logger.warn("Caught exception making file ${file.name}:\n$it")
      }
    }
  }

  private fun createDirectoryMapper(
    @Suppress("UNUSED_PARAMETER")
    df: DriveFile,
    targetPath: Path,
    createParents: Boolean
  ): Pair<String, () -> Unit> = "Directory" to {
    if (createParents) targetPath.createDirectories() else targetPath.createDirectory()
    targetPath.writeDriveMetadata(df)
    mappedFiles.putIfAbsent(df.id, df to targetPath)
  }

  private fun createFileMapper(
    @Suppress("UNUSED_PARAMETER")
    df: DriveFile,
    targetPath: Path,
    createParents: Boolean
  ): Pair<String, () -> Unit> = "File" to {
    if (createParents) {
      targetPath.parent?.createDirectories()
    }

    targetPath.createFile()
    targetPath.writeDriveMetadata(df)
    mappedFiles.putIfAbsent(df.id, df to targetPath)
  }

  private fun createShortcutMapper(
    @Suppress("UNUSED_PARAMETER")
    df: DriveFile,
    shortcutPath: Path,
    targetPath: Path,
  ): Pair<String, () -> Unit> = "Shortcut" to {
    // TODO Check for targetPath existence?

    shortcutPath.createSymbolicLinkPointingTo(targetPath)
    shortcutPath.writeDriveMetadata(df)
    mappedFiles.putIfAbsent(df.id, df to shortcutPath)
  }

  private fun mapDriveFileJob(
    file: DriveFile,
    parentPath: Path? = null,
    isRoot: Boolean = false,
    createParents: Boolean = false,
  ): Job = this.scope.launch {
    mapDriveFile(file, parentPath, isRoot, createParents)
  }

  private fun mapDriveFile(
    file: DriveFile,
    parentPath: Path? = null,
    isRoot: Boolean = false,
    createParents: Boolean = false,
  ) {
    logger.info("Mapping DriveFile ${file.name} (ID: ${file.id})")
    val path = when {
      parentPath != null -> parentPath / file.name
      isRoot -> fs.getPath("/", file.name)
      else -> {
        val parent = file.parents.firstOrNull() ?: let {
          logger.error { "Unable to get parent for '${file.name}'." }
          unknownFiles.add(file.id)
          throw IllegalArgumentException("Unable to map ${file.name}.")
        }

        if (mappedFiles.containsKey(parent)) {
          val (pFile, pPath) = mappedFiles[parent] ?: let {
            logger.error { "mappedFiles.containsKey($parent) returned true but unable to pull from map." }
            unknownFiles.add(file.id)
            throw IllegalArgumentException("Unable to map ${file.name}.")
          }

          pPath
        } else {
          unknownFiles.add(file.id)
          throw IllegalArgumentException("Unable to map ${file.name}.")
        }
      }
    }

    val (typeName, createFunction) = when (file.mimeType) {
      MimeType.googleFolder.mime -> createDirectoryMapper(file, path, createParents)
      MimeType.googleShortcut.mime -> {
        if (file.shortcutDetails == null || !mappedFiles.containsKey(file.shortcutDetails.targetId)) {
          return
        }

        val targetPath = mappedFiles[file.shortcutDetails.targetId]?.second ?: return
        createShortcutMapper(file, path, targetPath)
      }
      else -> createFileMapper(file, path, createParents)
    }

    runCatching { createFunction() }.onFailure { ex ->
      when (ex) {
        is FileAlreadyExistsException -> logger.warn(
          "Warning, cannot create $typeName for ${file.name}, file with path '$path' already exists!"
        )
        is IOException -> logger.error(
          "Unexpected IO error while creating $typeName ${file.name}:\n$ex"
        )
        else -> logger.error("Unknown error creating $typeName ${file.name}:\n$ex")
      }
    }
  }

  private suspend fun deepMapping() {

    val dirStack: MutableList<Pair<DriveFile, Path>> = mutableListOf()
    val fileStack: MutableList<Pair<DriveFile, Path>> = mutableListOf()
    val fsRoot = fs.getPath("/")
    var dirsMapped = 0
    var filesMapped = 0

    fun checkCancel(stage: String? = null) {
      if (!this.scope.isActive) {
        val msg = "Cancellation requested during deep map. " +
          "${if (stage != null) "(Stage: $stage)" else ""} " +
          "dirsMapped: $dirsMapped filesMapped: $filesMapped"
        logger.warn { msg }
        throw CancellationException(msg)
      }
    }

    val rootDirReq = createDefaultListRequest(
      withQ = {
        and {
          onlyType(MimeType.googleFolder)
          notTrashed()
          isRoot()
        }
      }
    )

    var token = null as String?
    do {
      checkCancel("Mapping root folders")

      val res = rootDirReq.executeAsync(this.scope).await()
      for (dir in res.files) {
        mapDriveFileJob(dir, fsRoot)
        dirsMapped += 1
        val (contentDirs, contentFiles) = createDefaultListRequest(
          withQ = {
            and {
              notTrashed()
              parents has value(dir.id)
            }
          },
          withFields = {
            files {
              id
              mimeType
            }
          }
        ).setPageSize(1000)
          .executeAsync(this.scope)
          .await()
          .files
          .partition { it.mimeType == MimeType.googleFolder.mime }

        dirStack.addAll(contentDirs.map { it to fsRoot / dir.name })
        fileStack.addAll(contentFiles.map { it to fsRoot / dir.name })
      }

      token = res.nextPageToken
      rootDirReq.pageToken = token
    } while (token != null)

    while (dirStack.isNotEmpty()) {
      checkCancel("Mapping dirStack")

      val (df, path) = dirStack.removeFirst()

      mapDriveFileJob(df, path)
      dirsMapped += 1

      val children = createDefaultListRequest(
        withQ = {
          parents has value(df.id)
        }
      ).setPageSize(1000)
        .executeAsync(this.scope)
        .await()
        .files

      val childPath = path / df.name
      children
        .partition { it.mimeType equals MimeType.googleFolder }
        .let { (childFolders, childFiles) ->
          dirStack.addAll(childFolders.map { it to childPath })
          fileStack.addAll(childFiles.map { it to childPath })
        }
    }

    while (fileStack.isNotEmpty()) {
      checkCancel("Mapping fileStack")
      val (df, path) = fileStack.removeFirst()

      mapDriveFileJob(df, path)
      filesMapped += 1
    }
  }

  companion object : KLogging() {
    /**
     * Coroutine constructor function for [DriveFileSystem], use this instead of creating one manually.
     * #### [new] uses [withContext] ([scope][CoroutineScope.coroutineContext]) while [newAsync] uses [scope.async][CoroutineScope.async]
     */
    suspend fun newAsync(
      scope: CoroutineScope,
      driveService: GoogleDriveService
    ): Deferred<DriveFileSystem> = scope.async { DriveFileSystem(scope, driveService).also { it.init() } }

    /**
     * Coroutine constructor function for [DriveFileSystem], use this instead of creating one manually.
     * #### [new] uses [withContext] ([scope][CoroutineScope.coroutineContext]) while [newAsync] uses [scope.async][CoroutineScope.async]
     */
    suspend fun new(
      scope: CoroutineScope,
      driveService: GoogleDriveService
    ): DriveFileSystem = withContext(scope.coroutineContext) {
      DriveFileSystem(scope, driveService).also { it.init() }
    }

    suspend fun iterateListRequest(
      req: Drive.Files.List,
      scope: CoroutineScope,
      checkCancellation: Boolean,
      beforeEach: () -> Unit = {},
      additionalFields: DriveFilesListRequestFieldsBuilder.() -> Unit = {},
      block: suspend FileList.() -> Unit
    ): Unit {
      req.buildFields {
        additionalFields.invoke(this)
        nextPageToken
      }

      var token = null as String?
      do {
        if (checkCancellation && !scope.isActive) {
          throw CancellationException()
        }

        beforeEach()

        val res = req.setPageToken(token).executeAsync(scope).await()

        block.invoke(res)

        token = res.nextPageToken
        req.pageToken = token
      } while (token != null)
    }
  }
}
