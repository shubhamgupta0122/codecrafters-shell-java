package repl.commands;

import repl.ReplContext;
import repl.exceptions.ReplException;

/**
 * Command handler for invalid or unrecognized commands.
 *
 * <p>Used when the user enters a command that is neither a builtin nor
 * an executable found in PATH. Returns a "command not found" error message.
 */
public class BadCommand implements Command {
	/** Error message for commands that don't exist. */
	public static final String COMMAND_NOT_FOUND = "command not found";

	/**
	 * Executes the bad command handler by throwing an exception.
	 *
	 * @param context the REPL context
	 * @return never returns (always throws exception)
	 * @throws ReplException always thrown with "command not found" message
	 */
	@Override
	public String execute(ReplContext context) throws ReplException {
		throw new ReplException(context.getMainCommandStr() + ": " + COMMAND_NOT_FOUND);
	}
}
