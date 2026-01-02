package repl;

import repl.commands.BadCommand;
import repl.commands.Command;
import repl.commands.EchoCommand;
import repl.commands.ExitCommand;
import repl.exceptions.ReplException;

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
		Command command;
		switch (mainCommandStr) {
			case SupportedCommand.exit -> command = new ExitCommand();
			case SupportedCommand.echo -> command = new EchoCommand();
			default -> command = new BadCommand();
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
