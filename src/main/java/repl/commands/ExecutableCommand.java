package repl.commands;

import repl.ReplContext;
import repl.exceptions.ReplException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	 * returns stdout if successful (exit code 0). If the process fails (non-zero
	 * exit code), throws ReplException with stderr content.
	 *
	 * @param context the REPL context containing command and arguments
	 * @return the captured stdout from the process (only if exit code is 0)
	 * @throws ReplException if the process exits with non-zero code (stderr as message)
	 *                        or if IOException/InterruptedException occurs
	 */
	@Override
	public String execute(ReplContext context) throws ReplException {
		String mainCommandStr = context.getMainCommandStr();
		try {
			// Build command list
			List<String> command = new ArrayList<>();
			command.add(mainCommandStr);
			command.addAll(context.getArgs());

			// Create and configure process
			ProcessBuilder pb = new ProcessBuilder(command);

			// Start process and capture output
			Process process = pb.start();
			String stdout = new String(process.getInputStream().readAllBytes());
			String stderr = new String(process.getErrorStream().readAllBytes());

			// Wait for completion
			int exitCode = process.waitFor();

			// Strip trailing whitespace from stdout (REPL adds newline)
			String cleanStdout = stdout.stripTrailing();

			if(exitCode != 0) {
				String errorMsg = stderr.isBlank()
					? context.getMainCommandStr() + ": command failed with exit code " + exitCode
					: stderr.stripTrailing();
				// Include stdout so it can be redirected even on failure
				throw new ReplException(errorMsg, cleanStdout);
			}

			return cleanStdout;
		} catch (IOException | InterruptedException e) {
			throw new ReplException(mainCommandStr + ": execution failed: " + e.getMessage(), e);
		}
	}

}
