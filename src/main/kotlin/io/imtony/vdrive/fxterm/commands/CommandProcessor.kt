@file:Suppress("unused")

package io.imtony.vdrive.fxterm.commands

import io.imtony.vdrive.fxterm.commands.context.CommandContext
import io.imtony.vdrive.fxterm.commands.context.CommandOutput
import io.imtony.vdrive.fxterm.commands.context.createCommandContext
import io.imtony.vdrive.fxterm.fs.DriveFileSystem
import io.imtony.vdrive.fxterm.google.services.GoogleServiceCollection
import io.imtony.vdrive.fxterm.terminal.TerminalState
import io.imtony.vdrive.fxterm.utils.LockProperty
import io.imtony.vdrive.fxterm.utils.getLineEnding
import javafx.collections.ObservableList
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.Font
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.util.Duration
import tornadofx.FX
import tornadofx.runLater
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.logging.Logger
import kotlin.math.floor
import kotlin.random.Random

private val EOL by lazy { String.getLineEnding() }

/**
 *
 */
class CommandProcessor(
  private val google: GoogleServiceCollection,
  private val FS: DriveFileSystem
) {

  private val logger = Logger.getLogger("fxterm.commands.CommandProcessor")
  private val history = mutableListOf<String>()
  private val cwd: Path = Files.createTempDirectory("/temp/temp/temp")

  private var delayTime = 50.0
    set(value) {
      field = value
      delayDuration = Duration.millis(value)
    }

  private var delayDuration = Duration.millis(delayTime)

  private fun getTerminalState() = TerminalState(
    history,
    cwd
  )

  private fun createOutput(output: ObservableList<Text>) = object : CommandOutput {
    override fun write(text: String, fg: Paint, bold: Boolean, italic: Boolean, underline: Boolean, strike: Boolean) =
      this@CommandProcessor.write(
        output,
        text,
        fg,
        bold,
        italic,
        underline,
        strike
      )

    override fun writeLn(text: String, fg: Paint, bold: Boolean, italic: Boolean, underline: Boolean, strike: Boolean) =
      this@CommandProcessor.writeLn(
        output,
        text,
        fg,
        bold,
        italic,
        underline,
        strike
      )
  }


  private fun createContext(
    command: TerminalCommand,
    args: String,
    output: ObservableList<Text>
  ): CommandContext = createCommandContext(
    Logger.getLogger("fxterm.command.${command.commandText}"),
    getTerminalState(),
    google,
    command.commandText,
    args,
    createOutput(output)
  )

  private fun defaultText(
    text: String,
    fg: Paint = Color.WHITESMOKE,
    bold: Boolean = false,
    italic: Boolean = false,
    underline: Boolean = false,
    strike: Boolean = false,
  ) = Text(text).apply {
    this.fill = fg
    this.isUnderline = underline
    this.isStrikethrough = strike

    if (bold || italic) {
      this.font = Font.font(
        null,
        if (bold) FontWeight.BOLD else null,
        if (italic) FontPosture.ITALIC else FontPosture.REGULAR,
        -1.0
      )
    }
  }

  private fun write(
    list: ObservableList<Text>,
    text: String,
    fg: Paint = Color.WHITESMOKE,
    bold: Boolean = false,
    italic: Boolean = false,
    underline: Boolean = false,
    strike: Boolean = false,
  ): Unit {
    list.add(defaultText(text, fg, bold, italic, underline, strike))
  }

  private fun writeLn(
    list: ObservableList<Text>,
    text: String,
    fg: Paint = Color.WHITESMOKE,
    bold: Boolean = false,
    italic: Boolean = false,
    underline: Boolean = false,
    strike: Boolean = false,
  ): Unit {
    list.add(defaultText(text + "\n", fg, bold, italic, underline, strike))
  }

  private fun writeInput(input: String, output: ObservableList<Text>) {
    write(output, "> ", Color.rgb(80, 250, 120), bold = true)
    writeLn(output, input)
  }

  private val timestampFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a")

  fun runLs(output: ObservableList<Text>) {
    writeLn(output, "\tDirectory /", Color.rgb(248, 199, 63))
    writeLn(output, " Mode\t\t\tLastWriteTime\t\t\t\tLength\t\tName", Color.LIGHTGRAY)
    writeLn(output, "-----\t\t\t------------\t\t\t\t\t------\t\t----", Color.GRAY)
    val lsNum = Random.nextInt(3, 12)
    val dirNum = floor(lsNum * Random.nextFloat()).toInt()

    for (i in 0..dirNum) {
      write(
        output,
        "d----\t\t\t${LocalDateTime.now().format(timestampFormat)}\t\t\t\t\t",
        Color.WHITESMOKE
      )
      writeLn(
        output,
        "Some Directory",
        Color.CYAN
      )
    }

    for (i in 0..(lsNum - dirNum)) {
      writeLn(output, "FILE")
    }
  }

  suspend fun processCommand(input: String, lock: LockProperty, output: ObservableList<Text>) {
    writeInput(input, output)
    history.add(input)
    when {
      input.startsWith("ls ") -> {
        runLs(output)
        runLater { lock.unlock() }
      }
      input.toLowerCase() == "exit" -> {
        FX.primaryStage.close()
        runLater { lock.unlock() }
      }
      else -> {
        logger.warning("Unknown command: $input")
        writeLn(output, "\tUnknown command: $input", Color.rgb(247, 132, 91))
        runLater { lock.unlock() }
      }
    }
  }
}
