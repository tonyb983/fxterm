package io.imtony.vdrive.fxterm.fs

import com.google.api.services.drive.model.FileList
import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import dispatch.core.IOCoroutineScope
import dispatch.core.withIO
import io.imtony.vdrive.fxterm.google.ext.drive.*
import io.imtony.vdrive.fxterm.google.services.GoogleDriveService
import kotlinx.coroutines.*
import tornadofx.singleAssign
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.*
import java.util.logging.Logger
import kotlin.io.path.*
import com.google.api.services.drive.model.File as DriveFile

private const val RUN_INIT = "runInit"

/**
 * A virtual [FileSystem] created by [Jimfs] representing the Google Drive folder heirarchy.
 */
class DriveFileSystem private constructor(
  private val scope: CoroutineScope,
  private val driveService: GoogleDriveService
) {
  private val fsName = "vdrive"
  private val className by lazy { this::class.qualifiedName }
  private val logger = Logger.getLogger("fxterm.fx.DriveFileSystem")
  private val fsConfigBuilder: Configuration.Builder
    get() = Configuration
      .unix()
      .toBuilder()
      .setAttributeViews("basic", "user") // , "owner", "posix")

  private var fs: FileSystem by singleAssign()
  private val mappedFiles: MutableMap<String, Pair<DriveFile, Path>> = mutableMapOf()
  private val unmappedFiles: MutableMap<String, Pair<DriveFile, Path>> = mutableMapOf()

  private var cwd: Path = Path.of("/")

  private var isInit = false

  private suspend fun init() {
    logger.entering(className, "init")
    try {
      runInit()
      isInit = true
    } catch (cancel: CancellationException) {
      logger.warning("Coroutine cancelled while file system was initializing.")
      isInit = false
    } catch (logged: Exception) {
      logger.severe(
        "Exception thrown during file system initialization:\n" +
          "${logged.message}\n" +
          "${logged.cause}\n" +
          "${logged.printStackTrace()}"
      )
      isInit = false
      throw logged
    } finally {
      logger.info(
        "File system " +
          "${if (isInit) "was initialized correctly" else "was errored during initialization"}."
      )
    }
    logger.exiting(className, "init")
  }

  private fun getCwd(): Path = cwd

  private suspend fun runInit() {
    logger.entering(className, RUN_INIT)
    // Get all root files and directories from Google Drive
    val (dirs, files) = driveService.service.files().list()
      .setCorpora(DriveCorporas.User)
      .setSpaces(DriveSpaces.Drive)
      .buildQ {
        and {
          isRoot()
          notTrashed()
        }
      }
      .setFields(
        "files(" +
          "id,name,mimeType," +
          "properties," +
          "createdTime,modifiedTime," +
          "shortcutDetails," +
          "originalFilename,fullFileExtension,fileExtension" +
          ")"
      )
      .setOrderBy(DriveOrderBy.Folder.add(DriveOrderBy.Name))
      .executeWithCoroutines()
      .files
      .partition { it.mimeType == MimeType.googleFolder.mime }

    // Create Jimfs file system
    fs = Jimfs.newFileSystem(fsName, fsConfigBuilder.build())
    val root = fs.getPath("/")
    // Map root directories first
    mapFileList(dirs, root)

    // Map rest
    mapFileList(files, root)

    logger.exiting(className, RUN_INIT)
  }

  private suspend fun mapNewRequest(
    fileList: FileList,
    parentPath: Path? = null,
    isRoot: Boolean = false,
    createParents: Boolean = false,
    scope: IOCoroutineScope = IOCoroutineScope(this@DriveFileSystem.scope.coroutineContext)
  ) = mapFileList(fileList.files, parentPath, isRoot, createParents, scope)

  private suspend fun mapFileList(
    files: List<DriveFile>,
    parentPath: Path? = null,
    isRoot: Boolean = false,
    createParents: Boolean = false,
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
        logger.warning("Caught exception making file ${file.name}:\n$it")
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
      else -> return
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
        is FileAlreadyExistsException -> logger.warning(
          "Warning, cannot create $typeName for ${file.name}, file with path '$path' already exists!"
        )
        is IOException -> logger.severe(
          "Unexpected IO error while creating $typeName ${file.name}:\n$ex"
        )
        else -> logger.severe("Unknown error creating $typeName ${file.name}:\n$ex")
      }
    }
  }

  companion object {
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
  }
}
