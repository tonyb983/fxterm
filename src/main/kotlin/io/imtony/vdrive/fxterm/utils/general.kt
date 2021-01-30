package io.imtony.vdrive.fxterm.utils

val OS by lazy { System.getProperty("os.name").toLowerCase() }
val IS_WINDOWS by lazy { OS.indexOf("win") >= 0 }
val IS_MAC by lazy { OS.indexOf("mac") >= 0 }
val IS_UNIX by lazy {
  OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0
}
val IS_NIX by lazy { IS_MAC || IS_UNIX }

