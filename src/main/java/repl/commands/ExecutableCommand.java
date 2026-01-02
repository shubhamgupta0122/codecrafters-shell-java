package repl.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
	 * returns the captured output. Both stdout and stderr are merged together.
	 *
	 * @param originalInput the complete original input string
	 * @param mainCommandStr the executable name/path
	 * @param args the command arguments
	 * @return the captured output from the process, or error message on failure
	 */
	@Override
	public String execute(String originalInput, String mainCommandStr, List<String> args) {
		try {
			// Build command list
			List<String> command = new ArrayList<>();
			command.add(mainCommandStr);
			command.addAll(args);

			// Create and configure process
			ProcessBuilder pb = new ProcessBuilder(command);
			pb.redirectErrorStream(true); // Merge stderr into stdout

			// Start process and capture output
			Process process = pb.start();
			String output = new String(process.getInputStream().readAllBytes());

			// Wait for completion
			int exitCode = process.waitFor();

			// Strip trailing whitespace (REPL adds newline)
			return output.stripTrailing();

		} catch (IOException | InterruptedException e) {
			return mainCommandStr + ": execution failed: " + e.getMessage();
		}

		// Debug output (commented out - uncomment to see argument details)
		// StringBuilder sb = new StringBuilder();
		// appendArgs(mainCommandStr, args, sb);
		// return sb.toString();
	}

	/**
	 * Appends formatted argument list to the output.
	 *
	 * @param mainCommandStr the program name
	 * @param args the arguments
	 * @param sb the StringBuilder to append to
	 */
	private static void appendArgs(String mainCommandStr, List<String> args, StringBuilder sb) {
		appendArgCounts(args, sb);
		sb.append("Arg #0 (program name): ");
		sb.append(mainCommandStr);
		sb.append("\n");
		AtomicInteger count = new AtomicInteger(1);
		args.forEach(arg -> {
			sb.append("Arg #");
			sb.append(count.getAndIncrement());
			sb.append(": ");
			sb.append(arg);
		});
	}

	/**
	 * Appends argument count summary to the output.
	 *
	 * @param args the arguments (excluding program name)
	 * @param sb the StringBuilder to append to
	 */
	private static void appendArgCounts(List<String> args, StringBuilder sb) {
		sb.append("Program was passed ");
		sb.append(args.size() + 1);
		sb.append(" args (including program name).\n");
	}

}
