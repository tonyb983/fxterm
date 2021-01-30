package io.imtony.vdrive.fxterm.terminal

import java.nio.file.Path

data class TerminalState(
  val commandHistory: List<String>,
  val cwd: Path,
)
