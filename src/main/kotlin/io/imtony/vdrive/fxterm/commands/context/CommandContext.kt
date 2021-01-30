package io.imtony.vdrive.fxterm.commands.context

import io.imtony.vdrive.fxterm.commands.TerminalCommand
import io.imtony.vdrive.fxterm.google.services.GoogleServiceCollection
import io.imtony.vdrive.fxterm.terminal.TerminalState
import java.util.logging.Logger

/**
 *  A [CommandContext] represents the state of the terminal and terminal controller
 *  at the time of command execution. It gives the command access to a [Logger], the
 *  [TerminalState], the [GoogleServiceCollection], the main command [String] typed
 *  by the user, the args that followed the main command, and a [CommandOutput] object
 *  allowing the [TerminalCommand] to print output.
 */
interface CommandContext {
  /**
   *
   */
  val logger: Logger

  /**
   *
   */
  val terminalState: TerminalState

  /**
   *
   */
  val google: GoogleServiceCollection

  /**
   *
   */
  val userInput: String

  /**
   *
   */
  val args: String

  /**
   *
   */
  val output: CommandOutput
}

private data class CommandContextImpl(
  override val logger: Logger,
  override val terminalState: TerminalState,
  override val google: GoogleServiceCollection,
  override val userInput: String,
  override val args: String,
  override val output: CommandOutput
) : CommandContext

/**
 *
 */
fun createCommandContext(
  logger: Logger,
  terminalState: TerminalState,
  google: GoogleServiceCollection,
  command: String,
  args: String,
  output: CommandOutput
): CommandContext = CommandContextImpl(
  logger,
  terminalState,
  google,
  command,
  args,
  output,
)
