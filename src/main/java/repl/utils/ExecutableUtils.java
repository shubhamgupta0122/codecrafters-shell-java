package repl.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Utility class for finding executables in system PATH.
 *
 * <p>Provides functionality to search PATH directories for executable files
 * by name, used to resolve external commands.
 */
public class ExecutableUtils {
	/**
	 * Array of directory paths from the PATH environment variable.
	 *
	 * <p>Initialized by splitting PATH using platform-specific separator.
	 * Handles missing PATH gracefully by returning empty array.
	 */
	public static final String[] ENV_PATHS = initEnvPaths();

	/**
	 * Initializes the PATH environment variable array.
	 *
	 * <p>Uses platform-specific path separator (colon on Unix, semicolon on Windows).
	 * Returns empty array if PATH is not set or is empty.
	 *
	 * @return array of PATH directory strings
	 */
	private static String[] initEnvPaths() {
		String pathEnv = System.getenv("PATH");
		if (pathEnv == null || pathEnv.isEmpty()) {
			return new String[0];
		}
		return pathEnv.split(File.pathSeparator);
	}

	/**
	 * Searches PATH directories for an executable with the given name.
	 *
	 * <p>Iterates through each PATH directory, looking for a file matching the
	 * command name that has executable permissions. Returns the first match found.
	 *
	 * @param commandToTest the name of the executable to find
	 * @return the Path to the executable if found, null otherwise
	 */
	public static Path findExecutablePath(String commandToTest) {
		for (String envPath : ENV_PATHS) {
			try (Stream<Path> directoryFiles = Files.list(Path.of(envPath))){
				for (Path dirFile : directoryFiles.toList()) {
					if(dirFile.getFileName().toString().equals(commandToTest)) {
						if (Files.isExecutable(dirFile)) {
							return dirFile;
						}
					}
				}
			} catch (NoSuchFileException _) {
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}
}
