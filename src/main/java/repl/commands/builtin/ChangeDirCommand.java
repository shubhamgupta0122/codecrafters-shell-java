package repl.commands.builtin;

import repl.BuiltinCommand;
import repl.ReplContext;
import repl.commands.Command;
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
	/** Error message suffix for non-existent paths. */
	private static final String NoSuchFileOrDirectory = ": No such file or directory";

	/**
	 * Changes the current working directory.
	 *
	 * <p>If no argument is provided, changes to the home directory. Supports
	 * tilde expansion for home directory paths.
	 *
	 * @param context the REPL context containing target path and DirUtils
	 * @return null on success
	 * @throws ReplException if the path doesn't exist (NoSuchFileException) or
	 *                        if an I/O error occurs
	 */
	@Override
	public String execute(ReplContext context) throws ReplException {
		List<String> args = context.getArgs();
		String requestedPath = args.isEmpty() ? DirUtils.HomeDirTilde : args.getFirst();
		try {
			context.getDirUtils().setCurrentDir(requestedPath);
			return null;
		} catch (NoSuchFileException e) {
			throw new ReplException(BuiltinCommand.cd + ": " + e.getMessage() + NoSuchFileOrDirectory, e);
		} catch (IOException e) {
			throw new ReplException(e);
		}
	}
}
