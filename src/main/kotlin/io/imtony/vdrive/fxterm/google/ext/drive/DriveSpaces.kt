package io.imtony.vdrive.fxterm.google.ext.drive

import com.google.api.services.drive.Drive

/**
 * Convenience [Enum] for setting the [Drive.Files.List.spaces][com.google.api.services.drive.Drive.Files.List.spaces]
 * with my [setSpace() extension function][setSpaces].
 */
enum class DriveSpaces {
  /** The main drive container. */
  Drive,

  /** The hidden appDataFolder. */
  AppDataFolder,

  /** The photos folder. */
  Photos,

  /** Combines [Drive], [AppDataFolder], and [Photos]. */
  All;

  /**
   * Combines this [DriveSpaces] and the given [space] into a comma separated pair.
   */
  operator fun plus(space: DriveSpaces): String {
    if (this == space) return this.toString()
    if (this == All || space == All) return All.toString()

    return "$this,$space"
  }

  override fun toString(): String = when (this) {
    Drive -> "drive"
    AppDataFolder -> "appDataFolder"
    Photos -> "photos"
    All -> "drive,appDataFolder,photos"
  }
}

/**
 * Sets the spaces that this query targets to the given [space].
 */
fun Drive.Files.List.setSpaces(space: DriveSpaces): Drive.Files.List = this.setSpaces(space.toString())

/**
 * Sets the spaces that this query targets to the given [spaces].
 */
fun Drive.Files.List.setSpaces(spaces: List<DriveSpaces>): Drive.Files.List = this.also {
  when (spaces.distinct().size) {
    1 -> spaces[0].toString()
    2 -> spaces[0].plus(spaces[1])
    3 -> DriveSpaces.All.toString()
  }
}

/**
 * The value to use as the default space.
 */
@Suppress("unused")
val Drive.Files.List.defaultSpace: DriveSpaces
  get() = DriveSpaces.Drive

/**
 * Sets the space for this request to the [defaultSpace].
 */
@Suppress("unused")
fun Drive.Files.List.setDefaultSpaces(): Drive.Files.List = this.setSpaces(defaultSpace)
