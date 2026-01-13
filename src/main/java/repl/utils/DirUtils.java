package repl.utils;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Manages the shell's current working directory.
 *
 * <p>Provides methods to get and set the current directory, with support
 * for both absolute and relative paths, as well as home directory expansion.
 *
 * <p>Instance-based design allows for proper test isolation.
 */
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DirUtils {
	/** The tilde character used for home directory expansion. */
	public static final String HomeDirTilde = "~";

	/**
	 * The absolute path to the user's home directory.
	 * Falls back to user.home system property if HOME environment variable is not set (Windows).
	 */
	public static final String HomeDirPath = getHomeDirectory();

	/**
	 * Determines the user's home directory path.
	 * Tries HOME environment variable first (Unix/Linux/Mac), then user.home property (Windows).
	 *
	 * @return the absolute path to the user's home directory
	 * @throws IllegalStateException if home directory cannot be determined
	 */
	private static String getHomeDirectory() {
		String home = System.getenv("HOME");
		if (home == null || home.isEmpty()) {
			home = System.getProperty("user.home");
		}
		if (home == null || home.isEmpty()) {
			throw new IllegalStateException("Cannot determine home directory: both HOME env and user.home property are unset");
		}
		return home;
	}

	/** The directory where the shell was started. */
	final Path initialDir;

	/** The current working directory of the shell. */
	@Getter
	Path currentDir;

	/**
	 * Creates a new DirUtils instance with the current working directory
	 * set to the JVM's user.dir property.
	 */
	public DirUtils() {
		this.initialDir = Paths.get(System.getProperty("user.dir"));
		this.currentDir = this.initialDir;
	}

	/**
	 * Creates a new DirUtils instance with a custom initial directory.
	 *
	 * <p>Useful for testing with controlled directory state.
	 *
	 * @param initialDir the initial working directory
	 */
	public DirUtils(Path initialDir) {
		this.initialDir = initialDir;
		this.currentDir = initialDir;
	}

	/**
	 * Sets the current working directory.
	 *
	 * <p>Supports tilde expansion for home directory paths. Relative paths
	 * are resolved against the current directory. The path is validated
	 * and converted to a real path (resolving symlinks).
	 *
	 * <p>Tilde expansion only occurs when '~' appears at the start of the path.
	 * For example, "~/documents" expands to "/home/user/documents", but
	 * "foo~bar" is treated literally.
	 *
	 * @param pathStr the path to change to (absolute, relative, or with ~)
	 * @throws IOException if the path doesn't exist or cannot be accessed
	 */
	public void setCurrentDir(String pathStr) throws IOException {
		if(pathStr.startsWith(HomeDirTilde)) {
			String expandedPath = HomeDirPath + pathStr.substring(1);
			setCurrentDir(expandedPath);
		} else {
			currentDir = currentDir.resolve(pathStr).toRealPath();
		}
	}
}
