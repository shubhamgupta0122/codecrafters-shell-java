package repl;

import repl.commands.Command;
import repl.commands.BadCommand;
import repl.commands.ExecutableCommand;
import repl.exceptions.ReplException;
import repl.utils.ExecutableUtils;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;

/**
 * Parses and evaluates user input to create the appropriate Command object.
 *
 * <p>Determines whether input is a builtin command, external executable, or invalid
 * command, then instantiates the corresponding Command implementation.
 *
 * <p>Resolution order: builtin → executable in PATH → bad command
 *
 * @see Command
 * @see BuiltinCommand
 */
public class ReplEvaluator {

	private final ReplContext context;

	/**
	 * Creates a new evaluator for the given input.
	 *
	 * @param input the user input string to evaluate
	 * @param contextBuilder the context builder with shared services
	 */
	public ReplEvaluator(String input, ReplContext.Builder contextBuilder) {
		// Build context with per-request data
		context = contextBuilder
				.originalInput(input)
				.build();
	}

	/**
	 * Evaluates the input and executes the appropriate command.
	 *
	 * <p>Parses the input, determines command type, creates Command object,
	 * and executes it.
	 *
	 * @return the command execution result
	 * @throws ReplException if command execution fails
	 */
	public String eval() throws ReplException {
		if(context.getMainCommandStr() == null) {
			throw new RuntimeException("Null Input???");
		}

		return processCommand();
	}

	/**
	 * Processes the parsed command and executes it.
	 *
	 * <p>Checks if command is builtin, then searches PATH for executable,
	 * falling back to BadCommand if not found. Uses reflection to instantiate
	 * builtin commands.
	 *
	 * @return the command execution result
	 * @throws ReplException if command execution fails
	 */
	private String processCommand() throws ReplException {
		Class<? extends Command> commandClass = BuiltinCommand.allCommandMap.get(context.getMainCommandStr());
		Command command;
		if(commandClass != null) {
			try {
				command = commandClass.getDeclaredConstructor().newInstance();
			} catch (
					InstantiationException |
					IllegalAccessException |
					InvocationTargetException |
					NoSuchMethodException e
			) {
				throw new RuntimeException(e);
			}
		} else {
			Path executablePath = ExecutableUtils.findExecutablePath(context.getMainCommandStr());
			if(executablePath != null)
				command = new ExecutableCommand();
			else
				command = new BadCommand();
		}

		return command.execute(context);
	}

}
