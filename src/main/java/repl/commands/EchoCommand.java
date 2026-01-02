package repl.commands;

import repl.exceptions.ReplException;

import java.util.List;

import static repl.Constants.WHITESPACE;

public class EchoCommand implements Command {
	public String process(String originalInput, String mainCommand, List<String> args) throws ReplException {
		StringBuilder outputSB = new StringBuilder();
		args.forEach(i -> {
			outputSB.append(i);
			outputSB.append(WHITESPACE);
		});
		return outputSB.toString();
	}
}
