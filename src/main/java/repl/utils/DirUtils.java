package repl.utils;

import repl.exceptions.NoSuchFileOrDirectoryException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DirUtils {
	private static final String initialDir = System.getProperty("user.dir");
	private static String currentDir = initialDir;

	public static Path getCurrentDir() {
		return Paths.get(currentDir);
	}

	public static void setCurrentDir(String newCurrentDir) throws NoSuchFileOrDirectoryException {
		if(!dirExists(newCurrentDir))
			throw new NoSuchFileOrDirectoryException(newCurrentDir);

		currentDir = newCurrentDir;
	}

	private static boolean dirExists(String pathStr) {
		return Files.exists(Paths.get(pathStr));
	}
}
