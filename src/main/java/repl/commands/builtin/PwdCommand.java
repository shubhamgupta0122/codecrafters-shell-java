package repl.commands.builtin;

import repl.ReplContext;
import repl.commands.Command;
import repl.commands.CommandResult;

/**
 * Builtin command that prints the current working directory.
 *
 * <p>Implements the {@code pwd} command, returning the absolute path
 * of the shell's current working directory.
 */
public class PwdCommand implements Command {
	/**
	 * Returns the current working directory path.
	 *
	 * @param context the REPL context containing DirUtils
	 * @return command result with the absolute path of the current working directory
	 */
	@Override
	public CommandResult execute(ReplContext context) {
		String path = context.getDirUtils().getCurrentDir()
				.toAbsolutePath()
				.toString();
		return CommandResult.success(path);
	}
}
