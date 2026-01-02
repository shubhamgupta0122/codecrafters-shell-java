package repl.commands;

import repl.BuiltinCommand;
import repl.utils.ExecutableUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class TypeCommand implements Command {

	private static final String COMMAND_FOUND = " is a shell builtin";
	private static final String COMMAND_NOT_FOUND = ": not found";

	@Override
	public String process(String originalInput, String mainCommandStr, List<String> args) {
		String commandToTest = args.getFirst();
		if(BuiltinCommand.allCommandMap.containsKey(commandToTest)) {
			return commandToTest + COMMAND_FOUND;
		} else {
			Path executablePath = ExecutableUtils.findExecutablePath(commandToTest);
			if (executablePath != null)
				return commandToTest + " is " + executablePath;
			else
				return commandToTest + COMMAND_NOT_FOUND;
		}
	}
}
