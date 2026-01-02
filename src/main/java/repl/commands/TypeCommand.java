package repl.commands;

import repl.BuiltinCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class TypeCommand implements Command {

	private static final String COMMAND_FOUND = " is a shell builtin";
	private static final String COMMAND_NOT_FOUND = ": not found";
	private static final String[] ENV_PATHS = System.getenv("PATH").split(":");

	@Override
	public String process(String originalInput, String mainCommandStr, List<String> args) {
		String commandToTest = args.getFirst();
		if(BuiltinCommand.allCommandMap.containsKey(commandToTest)) {
			return commandToTest + COMMAND_FOUND;
		} else {
			Path executablePath = findExecutablePath(commandToTest);
			if (executablePath != null)
				return commandToTest + " is " + executablePath;
			else
				return commandToTest + COMMAND_NOT_FOUND;
		}
	}

	private Path findExecutablePath(String commandToTest) {
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
