package repl;

import repl.exceptions.GracefulExitException;
import repl.exceptions.ReplException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReplEvaluator {
	private static final String WHITESPACE = " ";
	private static final String COMMAND_NOT_FOUND = "command not found";

	private final String originalInput;
	private String mainCommand;
	private List<String> commandArgs;

	public ReplEvaluator (String input) {
		originalInput = input;
	}

	public String eval() {
		extractMainCommand();
		if(mainCommand != null) {
			return processCommand();
		}
		throw new ReplException("Null Input???");
	}

	private String processCommand() {
		String output;
		switch (mainCommand) {
			case SupportedCommand.exit -> throw new GracefulExitException();
			default -> output = commandNotFound(originalInput);
		}
		return output;
	}

	private void extractMainCommand() {
		List<String> splitCommand = Arrays.stream(originalInput.split(WHITESPACE)).toList();
		mainCommand = splitCommand.getFirst();
		if(mainCommand == null) {
			commandArgs = new ArrayList<>();
		} else {
			commandArgs = splitCommand.subList(1, splitCommand.size());
		}
	}

	private String commandNotFound(String badCommand) {
		return badCommand + ": " + COMMAND_NOT_FOUND;
	}
}
