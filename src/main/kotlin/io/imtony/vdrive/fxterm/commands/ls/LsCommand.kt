package io.imtony.vdrive.fxterm.commands.ls

import io.imtony.vdrive.fxterm.commands.CommandCleanup
import io.imtony.vdrive.fxterm.commands.TerminalCommand
import io.imtony.vdrive.fxterm.commands.context.CommandContext
import javafx.scene.paint.Color
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.io.path.isDirectory
import kotlin.io.path.readAttributes
import kotlin.math.floor
import kotlin.random.Random

class LsCommand : TerminalCommand {
  private inner class LsState(val ctx: CommandContext) {
    public val showHidden: Boolean
    public val target: Path

    init {
      val argsSplit: MutableList<String> = ctx.args.split(" ").toMutableList()
      showHidden = if (argsSplit.contains("-a") || argsSplit.contains("--all")) {
        argsSplit.removeAll(listOf("-a", "--all"))
        true
      } else {
        false
      }

      argsSplit.removeAll { it.startsWith("--") || it.startsWith('-') }
      target = if (argsSplit.isNotEmpty()) {
        val t = ctx.terminalState.cwd.resolve(argsSplit.first()).toAbsolutePath()
        if (ctx.fileSystem.isValidPath(t)) {
          t
        } else {
          ctx.fileSystem.getRoot()
        }
      } else {
        ctx.fileSystem.getRoot()
      }
    }
  }

  override val commandText: String = "ls"
  override val helpText: String = """
    |   
    |   USAGE: 
    |     ls (Opts)               - List files in current directory.
    |     ls (Opts) <Directory>   - List files in target directory.
    | 
    |   options:
    |     -a - Show all (including hidden) files.
    |       
  """.trimMargin()

  private fun createState(ctx: CommandContext): LsState = LsState(ctx)

  private val timestampFormat = DateTimeFormatter
    .ofPattern("MM/dd/yyyy hh:mm a")
    .withLocale(Locale.US)
    .withZone(ZoneId.systemDefault())

  private fun printDir(dir: Path, ctx: CommandContext, state: LsState) {
    val attrs = dir.readAttributes<BasicFileAttributes>()

    ctx.output.write(
      "d----",
      Color.PALEGOLDENROD
    )
    ctx.output.write(
      "\t\t\t" +
        timestampFormat.format(attrs.lastModifiedTime().toInstant()),
      Color.WHITESMOKE
    )

    ctx.output.write(
      "\t\t" +
        "\t\t" +
        "\t",
    )

    ctx.output.writeLn(
      "${dir.fileName}",
      Color.CYAN
    )
  }

  private fun printFile(file: Path, ctx: CommandContext, state: LsState) {
    val attrs = file.readAttributes<BasicFileAttributes>()

    ctx.output.write(
      "d----",
      Color.PALEGOLDENROD
    )
    ctx.output.write(
      "\t\t\t" +
        timestampFormat.format(attrs.lastModifiedTime().toInstant()),
      Color.WHITESMOKE
    )
    ctx.output.write(
      "\t\t" +
        attrs.size().toString().padStart(8) +
        "\t",
      Color.PALEGOLDENROD
    )
    ctx.output.writeLn(
      "${file.fileName}",
      Color.CYAN
    )
  }

  override suspend fun execute(ctx: CommandContext): CommandCleanup? {
    val state = createState(ctx)
    val (dirs, files) = ctx.fileSystem
      .getDirContents(state.target.toString())
      .partition { it.isDirectory() }

    ctx.output.writeLn("\tDirectory /", Color.rgb(248, 199, 63))
    ctx.output.writeLn(" Mode\t\t\tLastWriteTime\t\t\t\tLength\t\tName", Color.LIGHTGRAY)
    ctx.output.writeLn("-----\t\t\t------------\t\t\t\t\t------\t\t----", Color.GRAY)

    for (dir in dirs) {
      printDir(dir, ctx, state)
    }


    for (file in files) {
      printFile(file, ctx, state)
    }

    ctx.output.writeLn("")

    return null
  }
}
