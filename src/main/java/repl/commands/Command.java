package repl.commands;

import repl.exceptions.ReplException;

import java.util.List;

public interface Command {
	String process(String originalInput, String mainCommandStr, List<String> args) throws ReplException;
}
