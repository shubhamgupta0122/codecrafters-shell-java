package repl.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for managing the shell's current working directory.
 *
 * <p>Provides methods to get and set the current directory, with support
 * for both absolute and relative paths, as well as home directory expansion.
 */
public class DirUtils {
	/** The directory where the shell was started. */
	private static final Path initialDir = Paths.get(System.getProperty("user.dir"));

	/** The current working directory of the shell. */
	private static Path currentDir = initialDir;

	/** The tilde character used for home directory expansion. */
	public static final String HomeDirTilde = "~";

	/** The absolute path to the user's home directory. */
	public static final String HomeDirPath = System.getenv("HOME");

	/**
	 * Returns the current working directory.
	 *
	 * @return the current directory as a Path
	 */
	public static Path getCurrentDir() {
		return currentDir;
	}

	/**
	 * Sets the current working directory.
	 *
	 * <p>Supports tilde expansion for home directory paths. Relative paths
	 * are resolved against the current directory. The path is validated
	 * and converted to a real path (resolving symlinks).
	 *
	 * @param pathStr the path to change to (absolute, relative, or with ~)
	 * @throws IOException if the path doesn't exist or cannot be accessed
	 */
	public static void setCurrentDir(String pathStr) throws IOException {
		if(pathStr.startsWith(HomeDirTilde))
			setCurrentDir(pathStr.replace(HomeDirTilde, HomeDirPath));
		else
			currentDir = currentDir.resolve(pathStr).toRealPath();
	}
}
