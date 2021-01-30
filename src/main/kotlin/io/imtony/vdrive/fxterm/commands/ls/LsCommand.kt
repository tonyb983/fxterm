package io.imtony.vdrive.fxterm.commands.ls

import io.imtony.vdrive.fxterm.commands.CommandCleanup
import io.imtony.vdrive.fxterm.commands.TerminalCommand
import io.imtony.vdrive.fxterm.commands.context.CommandContext

class LsCommand : TerminalCommand {
  inner class LsState(val ctx: CommandContext)

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

  private fun parseArgs(args: String) {

  }

  override suspend fun execute(ctx: CommandContext): CommandCleanup? {


    return null
  }
}
