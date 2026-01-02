package repl.commands;

import repl.exceptions.ReplException;

import java.util.List;

public interface Command {
	String execute(String originalInput, String mainCommandStr, List<String> args) throws ReplException;
}
