package repl;

import repl.commands.Command;
import repl.commands.BadCommand;
import repl.commands.CommandResult;
import repl.commands.ExecutableCommand;
import repl.exceptions.ReplException;
import repl.utils.ExecutableUtils;

import java.nio.file.Path;
import java.util.function.Supplier;

/**
 * Evaluates user input to determine and execute the appropriate command.
 *
 * <p>Determines whether input is a builtin command, external executable, or invalid
 * command, then instantiates and executes the corresponding Command implementation.
 *
 * <p>Resolution order: builtin → executable in PATH → bad command
 *
 * <p>Returns an {@link EvaluationResult} containing the command output and any
 * redirection targets. The REPL is responsible for handling I/O redirection.
 *
 * @see Command
 * @see BuiltinCommand
 * @see EvaluationResult
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
	 * executes it, and returns the result along with any redirection targets.
	 *
	 * @return the evaluation result containing command output and redirect targets
	 * @throws ReplException if command execution fails unexpectedly
	 */
	public EvaluationResult eval() throws ReplException {
		return processCommand();
	}

	/**
	 * Processes the parsed command and executes it.
	 *
	 * <p>Checks if command is builtin, then searches PATH for executable,
	 * falling back to BadCommand if not found.
	 *
	 * @return the evaluation result with command output and redirect targets
	 * @throws ReplException if command execution fails unexpectedly
	 */
	private EvaluationResult processCommand() throws ReplException {
		Supplier<Command> factory = BuiltinCommand.allCommandMap.get(context.getMainCommandStr());
		Command command;
		if(factory != null) {
			command = factory.get();
		} else {
			Path executablePath = ExecutableUtils.findExecutablePath(context.getMainCommandStr());
			if(executablePath != null) {
				// Cache resolved path to avoid redundant PATH lookups in ExecutableCommand
				context.setExecutablePath(executablePath);
				command = new ExecutableCommand();
			} else {
				command = new BadCommand();
			}
		}

		CommandResult result = command.execute(context);

		return new EvaluationResult(
			result,
			context.getStdoutRedirectTo(),
			context.getStderrRedirectTo()
		);
	}

}
