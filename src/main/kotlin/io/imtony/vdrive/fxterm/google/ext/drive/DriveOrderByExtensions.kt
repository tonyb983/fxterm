package io.imtony.vdrive.fxterm.google.ext.drive

import com.google.api.services.drive.Drive

enum class DriveOrderBy {
  CreatedTime,
  Folder,
  ModifiedByMeTime,
  ModifiedTime,
  Name,
  NameNatural,
  QuotaBytesUsed,
  Recency,
  SharedWithMeTime,
  Starred,
  ViewedByMeTime;

  override fun toString(): String = when (this) {
    CreatedTime -> "createdTime${if (descending) " desc" else ""}"
    Folder -> "folder${if (descending) " desc" else ""}"
    ModifiedByMeTime -> "modifiedByMeTime${if (descending) " desc" else ""}"
    ModifiedTime -> "modifiedTime${if (descending) " desc" else ""}"
    Name -> "name${if (descending) " desc" else ""}"
    NameNatural -> "name_natural${if (descending) " desc" else ""}"
    QuotaBytesUsed -> "quotaBytesUsed${if (descending) " desc" else ""}"
    Recency -> "recency${if (descending) " desc" else ""}"
    SharedWithMeTime -> "sharedWithMeTime${if (descending) " desc" else ""}"
    Starred -> "starred${if (descending) " desc" else ""}"
    ViewedByMeTime -> "viewedByMeTime${if (descending) " desc" else ""}"
  }

  fun asDesc(): DriveOrderBy = this.apply { descending = true }

  /**
   * Convenience function pointing to [plus]
   */
  fun add(other: DriveOrderBy): String = this + other

  /**
   * Convenience function pointing to [plus]
   */
  fun and(other: DriveOrderBy): String = this + other

  /**
   * Adds two [DriveOrderBy] and converts them to [String] separated by a comma.
   */
  operator fun plus(other: DriveOrderBy): String = "${this},$other"

  fun plus(vararg others: DriveOrderBy): String =
    this.toString() +
      (if (others.isNotEmpty()) "," else "") +
      others.joinToString(",")

  private var descending: Boolean = false
}

/**
 * Sets the order by which the result of this file list request will be sorted.
 */
fun Drive.Files.List.setOrderBy(vararg keys: DriveOrderBy): Drive.Files.List = this.setOrderBy(keys.joinToString(","))
