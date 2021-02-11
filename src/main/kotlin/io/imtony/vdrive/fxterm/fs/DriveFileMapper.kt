package io.imtony.vdrive.fxterm.fs

import com.google.api.services.drive.model.FileList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import mu.KLogging
import java.nio.file.FileSystem
import java.nio.file.Path
import kotlin.reflect.KClass
import kotlin.reflect.safeCast
import com.google.api.services.drive.model.File as DriveFile
import java.io.File as JavaFile

interface FileSystemInitializer {
  suspend fun initialize()
}

interface DriveFileMapper {
  /**
   * Map the given [list] of [File] to the [fs].
   */
  suspend fun mapFiles(
    scope: CoroutineScope,
    fs: FileSystem,
    list: List<DriveFile>?
  )

  /**
   * Map the given [FileList], usually from the response to a [com.google.api.services.drive.Drive.Files.List]
   * request, the default implementation simple forwards the call to [mapFiles].
   */
  suspend fun mapRequestToFileSystemAtPath(
    scope: CoroutineScope,
    fileList: FileList,
    parent: Path,
    fs: FileSystem = parent.fileSystem,
  ): Job = scope.launch {
    mapFiles(scope, fs, fileList.files)
  }

  /**
   * Map the given [FileList], usually from the response to a [com.google.api.services.drive.Drive.Files.List]
   * request, the default implementation simple forwards the call to [mapFiles].
   */
  suspend fun mapRequestToFileSystemAtRoot(
    scope: CoroutineScope,
    fileList: FileList,
    fs: FileSystem,
  ): Job = scope.launch {
    mapFiles(scope, fs, fileList.files)
  }

  companion object : KLogging()
}

interface DriveFileStorage {
  fun getFileAtPath(path: Path): JavaFile
  fun getFileAtPathAsync(path: Path): Deferred<JavaFile>

  fun storeDriveFileMetadata(file: DriveFile, path: Path)
}

typealias DefaultConstructor<T> = () -> T
typealias Constructor<T, TArgs> = (TArgs) -> T

/**
 * Inherit from this class to create a factory.
 *
 * Example:
 * ```kotlin
 * interface Interface {
 *  fun sayType(): String = "Interface"
 *    companion object : KFactory<Interface>() {
 *      override val constructors: Map<KClass<*>, DefaultConstructor<*>> = mapOf(
 *        ClassA::class to ::ClassA,
 *        ClassB::class to ::ClassB,
 *        ClassC::class to ::ClassC,
 *        ClassD::class to ClassD::new,
 *        )
 *      }
 * }
 * abstract class Abstract : Interface {
 *   override fun sayType(): String = "Abstract"
 * }
 * open class ClassA : Interface {
 *   override fun sayType(): String = "ClassA"
 * }
 * open class ClassB : ClassA() {
 *   override fun sayType(): String = "ClassB"
 * }
 * open class ClassC : Abstract() {
 *   override fun sayType(): String = "ClassC"
 * }
 * class ClassD private constructor() : Interface {
 *   override fun sayType(): String = "ClassD"
 *   companion object {
 *     fun new(): ClassD = ClassD()
 *     }
 *   }
 * fun someFunction() {
 *   val a: ClassA = Interface.newOrThrow()
 *   val b: ClassB = Interface.newOrThrow()
 *   val c: ClassC = Interface.new() ?: throw RuntimeException("Error creating ClassC")
 *   val d: ClassD = Interface.new(ClassD::class) ?: throw RuntimeException("Error creating ClassD")
 *
 *   println("A: " + a.sayType())
 *   println("B: " + b.sayType())
 *   println("C: " + c.sayType())
 *   println("D: " + d.sayType())
 * }
 * ```
 */
abstract class KFactory<TSuper : Any> {
  /**
   * Map of objects this factory knows how to create.
   */
  abstract val constructors: Map<KClass<out TSuper>, DefaultConstructor<out TSuper>>

  open fun <T : TSuper> new(cls: KClass<T>): T? = constructors[cls]?.invoke()?.let { cls.safeCast(it) }
}

inline fun <
  reified TRequested : TSuper,
  reified TSuper : Any,
  > KFactory<TSuper>.new(): TRequested? = this.new(TRequested::class)

inline fun <
  reified TSuper : Any,
  reified TRequested : TSuper
  > KFactory<TSuper>.newOrThrow(): TRequested = runCatching {
  this.new(TRequested::class) ?: throw FactoryFailException(KFactory::class, TRequested::class)
}.getOrElse {
  throw FactoryFailException(KFactory::class, TRequested::class, it)
}

data class FactoryFailException(
  override val message: String?,
  override val cause: Throwable?,
) : Exception() {
  constructor(message: String?) : this(message, null)
  constructor(cause: Throwable?) : this(null, cause)
  constructor(
    factory: KClass<*>,
    requested: KClass<*>
  ) : this(
    "Unable to create requested type ${requested.simpleName} with factory ${factory.simpleName}.\n" +
      "Factory: $factory\n" +
      "Requested: $requested"
  )

  constructor(
    factory: KClass<*>,
    requested: KClass<*>,
    inner: Throwable?,
  ) : this(
    "Unable to create requested type ${requested.simpleName} with factory ${factory.simpleName}.\n" +
      "Factory: $factory\n" +
      "Requested: $requested\nInner Exception: $inner",
    inner
  )
}
