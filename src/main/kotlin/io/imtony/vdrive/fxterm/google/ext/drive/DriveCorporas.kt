@file:Suppress("unused")

package io.imtony.vdrive.fxterm.google.ext.drive

import com.google.api.services.drive.Drive as DriveService

/**
 * The corporas that can be used to narrow (or widen) [DriveService.Files.List] Requests.
 *
 * #### Default corpora is [User].
 */
enum class DriveCorporas {
  /** Files created by, opened by, or shared directly with the user. */
  User,

  /** Files in the specified shared drive as indicated by the 'driveId'. */
  Drive,

  /** Files shared to the user's domain. */
  Domain,

  /** A combination of **[User]** and **[Drive]** for all drives where the user is a member. */
  AllDrives;

  override fun toString(): String = when (this) {
    User -> "user"
    Drive -> "drive"
    Domain -> "domain"
    AllDrives -> "allDrives"
  }
}

/**
 * Groupings of files to which the query applies. Supported groupings are: [DriveCorporas.User] (files created by,
 * opened by, or shared directly with the user), [DriveCorporas.Drive] (files in the specified shared drive as
 * indicated by the 'driveId'), [DriveCorporas.Domain] (files shared to the user's domain),
 * and [DriveCorporas.AllDrives] (A combination of 'user' and 'drive' for all drives where the user is a member).
 *
 * #### When able, use *[User][DriveCorporas.User]* or *[Drive][DriveCorporas.Drive]*, instead of *[AllDrives][DriveCorporas.AllDrives]*, for efficiency.
 */
@Suppress("LongLine")
fun DriveService.Files.List.setCorpora(corpora: DriveCorporas): DriveService.Files.List =
  this.setCorpora(corpora.toString())

/**
 * The value to use as the default corpora.
 */
val DriveService.Files.List.defaultCorpora: DriveCorporas get() = DriveCorporas.User

/**
 * Sets the corpora for this request to the [defaultCorpora].
 */
fun DriveService.Files.List.setDefaultCorpora(): DriveService.Files.List =
  this.setCorpora(defaultCorpora.toString())
