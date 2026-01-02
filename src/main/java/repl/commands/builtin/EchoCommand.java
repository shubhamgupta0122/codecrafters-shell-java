package repl.commands.builtin;

import repl.commands.Command;

import java.util.List;

import static repl.Constants.WHITESPACE;

public class EchoCommand implements Command {
	public String execute(String originalInput, String mainCommandStr, List<String> args) {
		StringBuilder outputSB = new StringBuilder();
		args.forEach(i -> {
			outputSB.append(i);
			outputSB.append(WHITESPACE);
		});
		return outputSB.toString();
	}
}
