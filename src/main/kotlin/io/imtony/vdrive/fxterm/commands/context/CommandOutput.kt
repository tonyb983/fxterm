package io.imtony.vdrive.fxterm.commands.context

import javafx.scene.paint.Color
import javafx.scene.paint.Paint

interface CommandOutput {
  fun write(
    text: String,
    fg: Paint = Color.WHITESMOKE,
    bold: Boolean = false,
    italic: Boolean = false,
    underline: Boolean = false,
    strike: Boolean = false,
  ): Unit

  fun writeLn(
    text: String,
    fg: Paint = Color.WHITESMOKE,
    bold: Boolean = false,
    italic: Boolean = false,
    underline: Boolean = false,
    strike: Boolean = false,
  ): Unit
}
