package repl.commands;

import repl.ReplContext;
import repl.exceptions.ReplException;

/**
 * Core interface for all executable shell commands.
 *
 * <p>Implements the Command pattern - all command types (builtins, executables, invalid commands)
 * implement this interface for uniform execution.
 *
 * <p>Implementations: {@link repl.commands.builtin.EchoCommand}, {@link repl.commands.builtin.ExitCommand},
 * {@link repl.commands.builtin.TypeCommand}, {@link repl.commands.ExecutableCommand}, {@link repl.commands.BadCommand}
 *
 * @see repl.REPL
 * @see repl.ReplEvaluator
 */
public interface Command {
	/**
	 * Executes this command with the given context.
	 *
	 * <p>Returns output as a CommandResult containing stdout and stderr; REPL handles printing
	 * and redirection. Throw {@link ReplException} for errors or
	 * {@link repl.exceptions.GracefulExitException} to exit shell.
	 *
	 * @param context the REPL context containing command input, arguments, and shared services
	 * @return the command result containing stdout and stderr streams
	 * @throws ReplException if command execution fails or encounters an error
	 */
	CommandResult execute(ReplContext context) throws ReplException;
}
