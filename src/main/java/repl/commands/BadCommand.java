package repl.commands;

import repl.Messages;
import repl.ReplContext;

/**
 * Command handler for invalid or unrecognized commands.
 *
 * <p>Used when the user enters a command that is neither a builtin nor
 * an executable found in PATH. Returns a "command not found" error message.
 */
public class BadCommand implements Command {
	/**
	 * Executes the bad command handler by returning an error result.
	 *
	 * @param context the REPL context
	 * @return command result with "command not found" error in stderr
	 */
	@Override
	public CommandResult execute(ReplContext context) {
		return CommandResult.error(context.getMainCommandStr() + ": " + Messages.COMMAND_NOT_FOUND);
	}
}
