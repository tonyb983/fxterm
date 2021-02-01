package io.imtony.vdrive.fxterm.commands.context

import io.imtony.vdrive.fxterm.commands.TerminalCommand
import io.imtony.vdrive.fxterm.fs.DriveFileSystem
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
   * Gives the command access to the virtual [DriveFileSystem].
   */
  val fileSystem: DriveFileSystem

  /**
   * The current state of the terminal.
   */
  val terminalState: TerminalState

  /**
   * The google service container.
   */
  val google: GoogleServiceCollection

  /**
   * The command word the user typed to run the command.
   */
  val userInput: String

  /**
   * The args that were passed after the command.
   */
  val args: String

  /**
   * Interface for the command to write to the stdout.
   */
  val output: CommandOutput

  /**
   * Whether or not the command should print logs.
   */
  val logOutput: Boolean
}

private data class CommandContextImpl(
  override val fileSystem: DriveFileSystem,
  override val terminalState: TerminalState,
  override val google: GoogleServiceCollection,
  override val userInput: String,
  override val args: String,
  override val output: CommandOutput,
  override val logOutput: Boolean
) : CommandContext

/**
 *
 */
fun createCommandContext(
  driveFileSystem: DriveFileSystem,
  terminalState: TerminalState,
  google: GoogleServiceCollection,
  command: String,
  args: String,
  output: CommandOutput,
  logOutput: Boolean
): CommandContext = CommandContextImpl(
  driveFileSystem,
  terminalState,
  google,
  command,
  args,
  output,
  logOutput
)
