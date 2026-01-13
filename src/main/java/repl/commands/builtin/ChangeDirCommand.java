package repl.commands.builtin;

import repl.BuiltinCommand;
import repl.Messages;
import repl.ReplContext;
import repl.commands.Command;
import repl.commands.CommandResult;
import repl.exceptions.ReplException;
import repl.utils.DirUtils;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;

/**
 * Builtin command that changes the current working directory.
 *
 * <p>Implements the {@code cd} command. Supports absolute paths, relative paths,
 * and home directory expansion using {@code ~}.
 */
public class ChangeDirCommand implements Command {

	/**
	 * Changes the current working directory.
	 *
	 * <p>If no argument is provided, changes to the home directory. Supports
	 * tilde expansion for home directory paths.
	 *
	 * @param context the REPL context containing target path and DirUtils
	 * @return command result (empty on success, error on failure)
	 * @throws ReplException if an unexpected I/O error occurs
	 */
	@Override
	public CommandResult execute(ReplContext context) throws ReplException {
		List<String> args = context.getArgs();
		String requestedPath = args.isEmpty() ? DirUtils.HomeDirTilde : args.getFirst();
		try {
			context.getDirUtils().setCurrentDir(requestedPath);
			return CommandResult.empty();
		} catch (NoSuchFileException e) {
			return CommandResult.error(BuiltinCommand.cd + ": " + e.getMessage() + Messages.NO_SUCH_FILE_OR_DIRECTORY);
		} catch (IOException e) {
			throw new ReplException(e);
		}
	}
}
