package repl;

import repl.commands.BadCommand;
import repl.commands.Command;
import repl.exceptions.ReplException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static repl.Constants.WHITESPACE;

public class ReplEvaluator {

	private final String originalInput;
	private String mainCommandStr;
	private List<String> commandArgs;

	public ReplEvaluator (String input) {
		originalInput = input;
	}

	public String eval() throws ReplException {
		extractMainCommand();
		if(mainCommandStr != null) {
			return processCommand();
		}
		throw new RuntimeException("Null Input???");
	}

	private String processCommand() throws ReplException {
		Class<? extends Command> commandClass = SupportedCommand.commandMap.get(mainCommandStr);
		Command command;
		if(commandClass != null) {
			try {
				command = commandClass.getDeclaredConstructor().newInstance();
			} catch (
					InstantiationException |
					IllegalAccessException |
					InvocationTargetException |
					NoSuchMethodException e
			) {
				throw new RuntimeException(e);
			}
		} else {
			command = new BadCommand();
		}
		return command.process(originalInput, mainCommandStr, commandArgs);
	}

	private void extractMainCommand() {
		List<String> splitCommand = Arrays.stream(originalInput.split(WHITESPACE)).toList();
		mainCommandStr = splitCommand.getFirst();
		if(mainCommandStr == null) {
			commandArgs = new ArrayList<>();
		} else {
			commandArgs = splitCommand.subList(1, splitCommand.size());
		}
	}
}
