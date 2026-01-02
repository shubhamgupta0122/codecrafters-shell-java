package repl.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.stream.Stream;

public class ExecutableUtils {
	public static final String[] ENV_PATHS = System.getenv("PATH").split(":");

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
