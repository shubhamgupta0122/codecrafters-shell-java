package repl.commands;

import repl.SupportedCommand;
import repl.exceptions.ReplException;

import java.util.List;

public class TypeCommand implements Command {
	private static final String COMMAND_FOUND = " is a shell builtin";
	private static final String COMMAND_NOT_FOUND = ": not found";
	@Override
	public String process(String originalInput, String mainCommandStr, List<String> args) {
		String commandToTest = args.getFirst();
		if(SupportedCommand.commandMap.containsKey(commandToTest))
			return commandToTest + COMMAND_FOUND;
		else
			return commandToTest + COMMAND_NOT_FOUND;
	}
}
