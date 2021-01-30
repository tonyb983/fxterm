package io.imtony.vdrive.fxterm.google.ext.drive

import com.google.api.services.drive.Drive

/**
 * Convenience [Enum] for setting the [Drive.Files.List.spaces][com.google.api.services.drive.Drive.Files.List.spaces]
 * with my [setSpace() extension function][im.tony.google.extensions.setSpaces].
 */
enum class DriveSpaces {
  Drive,
  AppDataFolder,
  Photos,
  All;

  operator fun plus(space: DriveSpaces): String {
    if (this == space) return this.toString()
    if (this == All || space == All) return All.toString()

    return "${this},$space"
  }

  override fun toString(): String = when (this) {
    Drive -> "drive"
    AppDataFolder -> "appDataFolder"
    Photos -> "photos"
    All -> "drive,appDataFolder,photos"
  }
}

/**
 * ### A comma-separated list of spaces to query within the corpus.
 * Supported values are [DriveSpaces.Drive], [DriveSpaces.AppDataFolder], and [DriveSpaces.Photos].
 */
fun Drive.Files.List.setSpaces(space: DriveSpaces): Drive.Files.List = this.setSpaces(space.toString())
fun Drive.Files.List.setSpaces(spaces: List<DriveSpaces>): Drive.Files.List = this.setSpaces(
  when (spaces.size) {
    0 -> DriveSpaces.Drive.toString()
    1 -> spaces[0].toString()
    2 -> if (spaces[0] == spaces[1]) spaces[0].toString() else spaces[0].plus(spaces[1])
    else -> DriveSpaces.All.toString()
  }
)
