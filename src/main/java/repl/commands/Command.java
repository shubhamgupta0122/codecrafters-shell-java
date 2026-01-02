package repl.commands;

import repl.exceptions.ReplException;

import java.util.List;

public interface Command {
	String process(String originalInput, String mainCommand, List<String> args) throws ReplException;
}
