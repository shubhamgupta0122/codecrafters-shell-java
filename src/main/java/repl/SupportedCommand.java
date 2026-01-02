package repl;

import repl.commands.*;

import java.util.Map;

public class SupportedCommand {
	public static final String exit = "exit";
	public static final String echo = "echo";
	public static final String type = "type";
	public static final Map<String, Class<? extends Command>> commandMap = Map.of(
			exit, ExitCommand.class,
			echo, EchoCommand.class,
			type, TypeCommand.class
	);
}
