@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package io.imtony.vdrive.fxterm

object Resources {
  private const val root = "/"

  object Creds {
    private const val base = "${root}creds"

    object Credentials {
      const val Path = "$base/credentials.json"
      const val Name = "credsFile"
    }
  }
}
