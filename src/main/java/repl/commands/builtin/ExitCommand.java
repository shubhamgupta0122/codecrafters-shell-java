package repl.commands.builtin;

import repl.commands.Command;
import repl.exceptions.GracefulExitException;

import java.util.List;

public class ExitCommand implements Command {
	public String process(
			String originalInput,
			String mainCommandStr,
			List<String> args
	) throws GracefulExitException {
		throw new GracefulExitException();
	}
}
