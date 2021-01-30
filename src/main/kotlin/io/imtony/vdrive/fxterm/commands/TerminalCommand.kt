package io.imtony.vdrive.fxterm.commands

import io.imtony.vdrive.fxterm.commands.context.CommandContext

// TODO: Optional by default?
// (suspend (TerminalCommand) -> Unit)?
/**
 * Convenience alias for terminal command clean-up function (dispose)
 * returned from [TerminalCommand.execute].
 * (TerminalCommand) -> Unit
 */
typealias CommandCleanup = suspend (TerminalCommand) -> Unit

interface TerminalCommand {
  /**
   * The string used to invoke this [TerminalCommand].
   */
  val commandText: String

  /**
   * The Usage / Help text printed when --help or -h is called with the command.
   */
  val helpText: String
  // TODO: assume blocking for now.
  // val isBlocking: Boolean

  /**
   * Execute this [TerminalCommand]. A new [CommandContext] is passed to each command
   * that is executed, and an optional [CommandCleanup] function can be returned
   * from the command execution.
   *
   * TODO: Split args string?
   */
  suspend fun execute(ctx: CommandContext): CommandCleanup?
}
