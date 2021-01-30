package io.imtony.vdrive.fxterm.google.ext.drive

import com.google.api.services.drive.Drive

//<editor-fold desc="Drive Corpora Extensions">
enum class DriveCorporas {
  User,
  Drive,
  Domain,
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
 * indicated by the 'driveId'), [DriveCorporas.Domain] (files shared to the user's domain), and [DriveCorporas.AllDrives] (A
 * combination of 'user' and 'drive' for all drives where the user is a member).
 *
 * #### When able, use *[User][DriveCorporas.User]* or *[Drive][DriveCorporas.Drive]*, instead of *[AllDrives][DriveCorporas.AllDrives]*, for efficiency.
 */
fun Drive.Files.List.setCorpora(corpora: DriveCorporas): Drive.Files.List = this.setCorpora(corpora.toString())
