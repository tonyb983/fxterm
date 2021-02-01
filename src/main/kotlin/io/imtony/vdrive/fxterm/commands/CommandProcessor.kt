@file:Suppress("unused")

package io.imtony.vdrive.fxterm.commands

import io.imtony.vdrive.fxterm.commands.context.CommandContext
import io.imtony.vdrive.fxterm.commands.context.CommandOutput
import io.imtony.vdrive.fxterm.commands.context.createCommandContext
import io.imtony.vdrive.fxterm.commands.ls.LsCommand
import io.imtony.vdrive.fxterm.fs.DriveFileSystem
import io.imtony.vdrive.fxterm.google.services.GoogleServiceCollection
import io.imtony.vdrive.fxterm.terminal.TerminalState
import io.imtony.vdrive.fxterm.utils.LockProperty
import io.imtony.vdrive.fxterm.utils.empty
import io.imtony.vdrive.fxterm.utils.getLineEnding
import javafx.collections.ObservableList
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.Font
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.util.Duration
import mu.KLogging
import tornadofx.*
import java.nio.file.Path
import java.time.format.DateTimeFormatter

private val EOL by lazy { String.getLineEnding() }

/**
 *
 */
class CommandProcessor(
  private val google: GoogleServiceCollection,
  private val FS: DriveFileSystem
) {
  private inner class OutputImpl(private val output: ObservableList<Text>) : CommandOutput {
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

  private val history = mutableListOf<String>()
  private val cwd: Path = FS.getRoot()
  private var commandLogging: Boolean = false

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

  private fun createOutput(output: ObservableList<Text>) = OutputImpl(output)

  private fun createContext(
    command: TerminalCommand,
    args: String,
    output: ObservableList<Text>
  ): CommandContext = createCommandContext(
    FS,
    getTerminalState(),
    google,
    command.commandText,
    args,
    createOutput(output),
    commandLogging
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
    text: String = String.empty,
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
    text: String = String.empty,
    fg: Paint = Color.WHITESMOKE,
    bold: Boolean = false,
    italic: Boolean = false,
    underline: Boolean = false,
    strike: Boolean = false,
  ): Unit {
    list.add(defaultText(text + "\n", fg, bold, italic, underline, strike))
  }

  private fun writeError(
    list: ObservableList<Text>,
    text: String = "",
    ex: Throwable? = null
  ): Unit {
    writeLn(list)
    writeLn(list)
    writeLn(list, "Error during execution.", Color.ORANGERED)

    if (text.isBlank()) {
      writeLn(list, text, Color.ORANGERED)
    }

    if (ex?.message != null) {
      writeLn(list, ex.message ?: return, Color.ROSYBROWN)
    }
    writeLn(list)
  }

  private val errorColor = Color.ORANGERED

  private fun writeInput(input: String, output: ObservableList<Text>) {
    write(output, "> ", Color.rgb(80, 250, 120), bold = true)
    writeLn(output, input)
  }

  private val timestampFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a")

  suspend fun processCommand(input: String, lock: LockProperty, output: ObservableList<Text>) {
    writeInput(input, output)
    history.add(input)
    val lowered = input.toLowerCase()
    when {
      lowered == "help" -> """
        | Commands:
        |   * help  - Prints this help text.
        |   * ls    - Lists all files in either the given directory or the current directory.
        |   * exit  - Exits the program.
        |   
      """.trimMargin().also {
        writeLn(output, it, Color.INDIANRED)
      }
      input == "ls" || input.startsWith("ls ") -> runCatching {
        LsCommand().apply {
          this.execute(
            createContext(
              this,
              if (input.length == 2 || input.length == 3) String.empty else input.removePrefix("ls").trim(),
              output
            )
          )
        }
      }.onFailure {
        writeError(output, "Error running LS command:", it)
      }
      lowered == "exit" || lowered.startsWith("exit ") -> FX.primaryStage.close()
      else -> {
        logger.warn("Unknown command: $input")
        writeLn(output, "\tUnknown command: $input", Color.rgb(247, 132, 91))
      }
    }

    runLater { lock.unlock() }
  }

  companion object : KLogging()
}
