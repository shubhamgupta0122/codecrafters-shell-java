package repl.commands.builtin;

import repl.ReplContext;
import repl.commands.Command;

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
	 * @return the arguments joined by spaces
	 */
	public String execute(ReplContext context) {
		StringBuilder outputSB = new StringBuilder();
		context.getArgs().forEach(i -> {
			outputSB.append(i);
			outputSB.append(WHITESPACE);
		});
		return outputSB.toString();
	}
}
