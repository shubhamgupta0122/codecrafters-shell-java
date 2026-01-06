package repl.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DirUtils {
	private static final Path initialDir = Paths.get(System.getProperty("user.dir"));
	private static Path currentDir = initialDir;

	public static Path getCurrentDir() {
		return currentDir;
	}

	public static void setCurrentDir(String pathStr) throws IOException {
		currentDir = currentDir.resolve(pathStr).toRealPath();
	}
}
