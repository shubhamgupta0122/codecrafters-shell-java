package repl.commands;

import repl.exceptions.GracefulExitException;

import java.util.List;

public class ExitCommand implements Command {
	public String process(
			String originalInput,
			String mainCommand,
			List<String> args
	) throws GracefulExitException {
		throw new GracefulExitException();
	}
}
