package repl.commands.builtin;

import repl.commands.Command;
import repl.exceptions.ReplException;
import repl.utils.DirUtils;

import java.util.List;

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
	 * @param originalInput the complete original input string
	 * @param mainCommandStr the command name ("pwd")
	 * @param args ignored (pwd takes no arguments)
	 * @return the absolute path of the current working directory
	 */
	@Override
	public String execute(String originalInput, String mainCommandStr, List<String> args) throws ReplException {
		return DirUtils.getCurrentDir()
				.toAbsolutePath()
				.toString();
	}
}
