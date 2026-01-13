package repl.commands;

import repl.ReplContext;
import repl.exceptions.ReplException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Command handler for external executable programs.
 *
 * <p>Executes external programs found in PATH using Java's ProcessBuilder API.
 * Captures and returns stdout/stderr output from the spawned process.
 */
public class ExecutableCommand implements Command {
	/**
	 * Executes the external command and captures its output.
	 *
	 * <p>Spawns a new process using ProcessBuilder, waits for completion, and
	 * returns a CommandResult containing stdout, stderr, and exit code.
	 *
	 * @param context the REPL context containing command and arguments
	 * @return the command result with stdout, stderr, and exit code
	 * @throws ReplException only for truly unexpected errors (IOException, InterruptedException)
	 */
	@Override
	public CommandResult execute(ReplContext context) throws ReplException {
		String mainCommandStr = context.getMainCommandStr();
		try {
			// Build command list
			List<String> command = new ArrayList<>();

			// Use cached executable path if available (avoids redundant PATH lookup)
			if (context.getExecutablePath() != null) {
				command.add(context.getExecutablePath().toString());
			} else {
				// Fallback to command name (ProcessBuilder will search PATH)
				command.add(mainCommandStr);
			}

			command.addAll(context.getArgs());

			// Create and configure process
			ProcessBuilder pb = new ProcessBuilder(command);

			// Start process and capture output
			Process process = pb.start();

			// Read streams concurrently to avoid deadlock on large output
			// Sequential reading can block if output exceeds pipe buffer size (~64KB)
			CompletableFuture<String> stdoutFuture = CompletableFuture.supplyAsync(() -> {
				try (InputStream stream = process.getInputStream()) {
					return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
				} catch (IOException e) {
					return "";
				}
			});

			CompletableFuture<String> stderrFuture = CompletableFuture.supplyAsync(() -> {
				try (InputStream stream = process.getErrorStream()) {
					return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
				} catch (IOException e) {
					return "";
				}
			});

			// Wait for process completion
			int exitCode = process.waitFor();

			// Retrieve captured output (blocks until streams are fully read)
			String stdout = stdoutFuture.join();
			String stderr = stderrFuture.join();

			// Strip trailing whitespace (REPL adds newlines)
			String cleanStdout = stdout.stripTrailing();
			String cleanStderr = stderr.stripTrailing();

			return new CommandResult(cleanStdout, cleanStderr, exitCode);
		} catch (IOException | InterruptedException e) {
			throw new ReplException(mainCommandStr + ": execution failed: " + e.getMessage(), e);
		}
	}

}
