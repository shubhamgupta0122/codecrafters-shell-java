package repl.commands.builtin;

import repl.ReplContext;
import repl.commands.Command;
import repl.commands.CommandResult;

import static repl.Constants.WHITESPACE;

/**
 * Builtin command that prints arguments to stdout.
 *
 * <p>Implements the standard {@code echo} command, which prints its arguments
 * separated by spaces.
 */
public class EchoCommand implements Command {
	/**
	 * Executes the echo command, printing all arguments.
	 *
	 * @param context the REPL context containing arguments to print
	 * @return command result with arguments joined by spaces
	 */
	public CommandResult execute(ReplContext context) {
		String output = String.join(Character.toString(WHITESPACE), context.getArgs());
		return CommandResult.success(output);
	}
}
