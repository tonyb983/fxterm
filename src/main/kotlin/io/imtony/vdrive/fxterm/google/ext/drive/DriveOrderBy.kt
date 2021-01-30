package io.imtony.vdrive.fxterm.google.ext.drive

import com.google.api.services.drive.Drive

/**
 * Enum class that can be used to sort a [Drive] [Drive.Files.List] request in a type-safe way.
 */
enum class DriveOrderBy {
  /** The createdAt time of the files. */
  CreatedTime,

  /** Orders folders first, then regular files. */
  Folder,

  /** The modifiedAtByMe time of the files. */
  ModifiedByMeTime,

  /** The modifiedAt time of the files. */
  ModifiedTime,

  /** Order by filename. */
  Name,

  /** I have no clue what makes this different from [Name]. */
  NameNatural,

  /** Order by bytes used. */
  QuotaBytesUsed,

  /** Puts the most recent files first. */
  Recency,

  /** Orders most recently shared files first. */
  SharedWithMeTime,

  /** Orders the starred files first. */
  Starred,

  /** Orders by view by me time. */
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

  /**
   * Returns this [DriveOrderBy] with [descending] marked as true.
   */
  fun asDesc(): DriveOrderBy = this.apply { descending = true }

  /**
   * Convenience function pointing to [plus].
   */
  fun add(other: DriveOrderBy): String = this + other

  /**
   * Convenience function pointing to [plus].
   */
  fun and(other: DriveOrderBy): String = this + other

  /**
   * Adds two [DriveOrderBy] and converts them to [String] separated by a comma.
   */
  operator fun plus(other: DriveOrderBy): String = "$this,$other"

  /**
   * Adds this [DriveOrderBy] to all of the given [DriveOrderBy] converting them to a
   * comma separated [String].
   */
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
