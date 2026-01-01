package repl;

import repl.exceptions.GracefulExitException;

import java.util.Arrays;

public class ReplEvaluator {
	private static final String WHITESPACE = " ";
	private static final String EXIT_COMMAND = "exit";
	private static final String COMMAND_NOT_FOUND = "command not found";

	private final String originalInput;
	private String mainCommand;

	public ReplEvaluator (String originalInput) {
		this.originalInput = originalInput;
	}

	public String eval() {
		extractMainCommand();
		if(isExitCommand()) {
			throw new GracefulExitException();
		}
		return commandNotFound(originalInput);
	}

	private boolean isExitCommand() {
		return this.mainCommand != null && this.mainCommand.equals(EXIT_COMMAND);
	}

	private void extractMainCommand() {
		this.mainCommand = Arrays.stream(originalInput.split(WHITESPACE)).findFirst().orElse(null);
	}

	private String commandNotFound(String badCommand) {
		return badCommand + ": " + COMMAND_NOT_FOUND;
	}
}
