@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package io.imtony.vdrive.fxterm

import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.docs.v1.DocsScopes
import com.google.api.services.drive.DriveScopes
import com.google.api.services.sheets.v4.SheetsScopes
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import at.favre.lib.crypto.bcrypt.BCrypt.Version as BCryptVersion

/**
 * Application wide const values. Should obviously only be used for very general very global things,
 * or perhaps fallback values for expected environmental variables.
 */
object Const {
  /**
   * The application name that is fed to the google api.
   */
  object ApplicationName {
    /**
     * The value of this key-value pair.
     */
    const val Value: String = "FxTerminal"

    /**
     * The key of this key-value pair.
     */
    const val Name: String = "applicationName"
  }

  /**
   * The directory that the stored credential is saved to.
   */
  object TokenDirectory {
    /**
     * The value of this key-value pair.
     */
    const val Value: String = "S:/FastCode/Kotlin/fxterm/tokens/"

    /**
     * The key of this key-value pair.
     */
    const val Name: String = "tokenDir"
  }

  /**
   * The default port for the [com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver]
   * used during credential verification.
   */
  object DefaultPort {
    /**
     * The value of this key-value pair.
     */
    const val Value: Int = -1

    /**
     * The value of this key-value pair, represented as a [String] instead of [Int].
     * #### This is mainly used for DI systems that are more strict in their property values.
     */
    const val StringValue: String = "-1"

    /**
     * The key of this key-value pair.
     */
    const val Name: String = "defaultPort"
  }

  /**
   * Const values used by the [at.favre.lib.crypto.bcrypt.BCrypt] library.
   */
  object BCrypt {
    /**
     * The version of the [at.favre.lib.crypto.bcrypt.BCrypt] algorithm.
     */
    object Version {
      /**
       * A "const" instance of the [BCryptVersion] enum.
       */
      val Instance: BCryptVersion = BCryptVersion.VERSION_2A

      /**
       * The value of this key-value pair, represented as a [String] instead of [BCryptVersion].
       * #### This is mainly used for DI systems that are more strict in their property values.
       *
       * @see bcryptVersionFrom
       */
      const val StringValue: String = "2A"

      /**
       * The key of this key-value pair.
       */
      const val Name: String = "bcryptVersion"
    }

    /**
     * The "cost" used by the [at.favre.lib.crypto.bcrypt.BCrypt] encryption algorithms.
     */
    object Cost {
      /**
       * The value of this key-value pair.
       */
      const val Value: Int = 15

      /**
       * The value of this key-value pair, represented as a [String] instead of [Int].
       * #### This is mainly used for DI systems that are more strict in their property values.
       */
      const val StringValue: String = "15"

      /**
       * The key of this key-value pair.
       */
      const val Name: String = "bcryptCost"
    }
  }

  /**
   * The Google service scopes that are used by this application.
   *
   * @see DriveScopes
   * @see DocsScopes
   * @see SheetsScopes
   */
  object GoogleScopes {
    /**
     * A raw [Array] holding the application scopes used by this application.
     */
    val Value: ImmutableSet<String> = persistentSetOf(
      DriveScopes.DRIVE, DriveScopes.DRIVE_APPDATA, DriveScopes.DRIVE_METADATA,
      DocsScopes.DOCUMENTS,
      SheetsScopes.SPREADSHEETS,
      CalendarScopes.CALENDAR,
    )

    /**
     * The "const" instance of the [ApplicationScopes] object holding the scopes.
     */
    val Instance: ApplicationScopes = ApplicationScopes(Value)

    /**
     * The key of this key-value pair.
     */
    const val Name: String = "applicationScopes"
  }
}

/**
 * Convenience function to parse a [String] into a [BCryptVersion].
 */
fun bcryptVersionFrom(input: String): BCryptVersion = when (input) {
  "2A" -> BCryptVersion.VERSION_2A
  "2B" -> BCryptVersion.VERSION_2B
  "2X" -> BCryptVersion.VERSION_2X
  "2Y" -> BCryptVersion.VERSION_2Y
  else -> throw IllegalArgumentException("$input is not a valid (or usable) version of BCrypt.")
}

/**
 * Wrapper around google's permission scopes.
 *
 * @property scopes The scopes to set for the application.
 * @constructor Create the immutable google application scopes.
 */
data class ApplicationScopes(val scopes: ImmutableSet<String>)
