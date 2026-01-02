package repl;

import repl.commands.Command;
import repl.commands.EchoCommand;
import repl.commands.ExitCommand;

import java.util.Map;

public class SupportedCommand {
	public static final String exit = "exit";
	public static final String echo = "echo";
	public static final Map<String, Class<? extends Command>> commandMap = Map.of(
			exit, ExitCommand.class,
			echo, EchoCommand.class
	);
}
