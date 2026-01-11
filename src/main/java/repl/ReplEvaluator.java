package repl;

import repl.commands.Command;
import repl.commands.BadCommand;
import repl.commands.ExecutableCommand;
import repl.exceptions.ReplException;
import repl.utils.ExecutableUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

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
	 * falling back to BadCommand if not found. Uses Supplier pattern to instantiate
	 * builtin commands efficiently.
	 *
	 * @return the command execution result
	 * @throws ReplException if command execution fails
	 */
	private String processCommand() throws ReplException {
		Supplier<Command> factory = BuiltinCommand.allCommandMap.get(context.getMainCommandStr());
		Command command;
		if(factory != null) {
			command = factory.get();
		} else {
			Path executablePath = ExecutableUtils.findExecutablePath(context.getMainCommandStr());
			if(executablePath != null)
				command = new ExecutableCommand();
			else
				command = new BadCommand();
		}

		try {
			String output = command.execute(context);

			if(context.getStdoutRedirectTo() == null) {
				return output;
			} else {
				redirectOutput(output, context.getStdoutRedirectTo());
				return null;
			}
		} catch (ReplException e) {
			// If command failed but produced stdout, redirect it before re-throwing
			if (context.getStdoutRedirectTo() != null && e.getCapturedStdout() != null) {
				redirectOutput(e.getCapturedStdout(), context.getStdoutRedirectTo());
			}
			throw e;
		}
	}

	private void redirectOutput(String output, String redirectTo) throws ReplException {
		try {
			// Resolve relative paths against current working directory
			Path outputPath = context.getDirUtils().getCurrentDir().resolve(redirectTo);

			// Ensure parent directories exist
			Path parentDir = outputPath.getParent();
			if (parentDir != null && !Files.exists(parentDir)) {
				Files.createDirectories(parentDir);
			}

			Files.writeString(outputPath, output, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new ReplException(e);
		}
	}

}
