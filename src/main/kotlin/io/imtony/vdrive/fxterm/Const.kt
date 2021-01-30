@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package io.imtony.vdrive.fxterm

import at.favre.lib.crypto.bcrypt.BCrypt
import com.google.api.services.docs.v1.DocsScopes
import com.google.api.services.drive.DriveScopes
import com.google.api.services.sheets.v4.SheetsScopes

object Const {
  object ApplicationName {
    const val Value = "FxTerminal"
    const val Name = "applicationName"
  }

  object TokenDirectory {
    const val Value = "S:/FastCode/Kotlin/fxterm/tokens/"
    const val Name = "tokenDir"
  }

  object DefaultPort {
    const val Value = -1
    const val StringValue = "-1"
    const val Name = "defaultPort"
  }

  object BCrypt {
    object Version {
      val Instance = at.favre.lib.crypto.bcrypt.BCrypt.Version.VERSION_2A
      const val StringValue = "2A"
      const val Name = "bcryptVersion"
    }

    object Cost {
      const val Value = 15
      const val StringValue = "15"
      const val Name = "bcryptCost"
    }
  }

  object GoogleScopes {
    val Instance = ApplicationScopes(
      DriveScopes.DRIVE, DriveScopes.DRIVE_APPDATA, DriveScopes.DRIVE_METADATA,
      DocsScopes.DOCUMENTS,
      SheetsScopes.SPREADSHEETS,
    )
    val Value: Array<String> = arrayOf(
      DriveScopes.DRIVE, DriveScopes.DRIVE_APPDATA, DriveScopes.DRIVE_METADATA,
      DocsScopes.DOCUMENTS,
      SheetsScopes.SPREADSHEETS,
    )
    const val Name = "applicationScopes"
  }
}

fun bcryptVersionFrom(input: String): BCrypt.Version = when (input) {
  "2A" -> BCrypt.Version.VERSION_2A
  "2B" -> BCrypt.Version.VERSION_2B
  "2X" -> BCrypt.Version.VERSION_2X
  "2Y" -> BCrypt.Version.VERSION_2Y
  else -> throw IllegalArgumentException("$input is not a valid (or usable) version of BCrypt.")
}

/**
 * Wrapper around google's permission scopes.
 *
 * @property scopes The scopes to set for the application.
 * @constructor Create the immutable google application scopes.
 */
data class ApplicationScopes(val scopes: MutableCollection<String>) {
  constructor(vararg scopes: String) : this(scopes.toMutableList())
}
