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
	private String mainCommand;
	private List<String> commandArgs;

	public ReplEvaluator (String input) {
		originalInput = input;
	}

	public String eval() throws ReplException {
		extractMainCommand();
		if(mainCommand != null) {
			return processCommand();
		}
		throw new RuntimeException("Null Input???");
	}

	private String processCommand() throws ReplException {
		Command command;
		switch (mainCommand) {
			case SupportedCommand.exit -> command = new ExitCommand();
			case SupportedCommand.echo -> command = new EchoCommand();
			default -> command = new BadCommand();
		}
		return command.process(originalInput, mainCommand, commandArgs);
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
}
