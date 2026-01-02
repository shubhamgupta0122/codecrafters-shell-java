package repl.commands.builtin;

import repl.BuiltinCommand;
import repl.commands.Command;
import repl.utils.ExecutableUtils;

import java.nio.file.Path;
import java.util.List;

public class TypeCommand implements Command {

	private static final String COMMAND_FOUND = " is a shell builtin";
	private static final String COMMAND_NOT_FOUND = ": not found";

	@Override
	public String execute(String originalInput, String mainCommandStr, List<String> args) {
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
