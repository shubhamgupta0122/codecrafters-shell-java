package repl.commands;

import repl.exceptions.ReplException;

import java.util.List;

public class BadCommand implements Command {
	public static final String COMMAND_NOT_FOUND = "command not found";
	@Override
	public String process(String originalInput, String mainCommandStr, List<String> args) throws ReplException {
		return mainCommandStr + ": " + COMMAND_NOT_FOUND;
	}
}
