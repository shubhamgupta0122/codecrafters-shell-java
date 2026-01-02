package repl.commands;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Command handler for external executable programs.
 *
 * <p>Wraps external programs found in PATH. Currently outputs debug information
 * about the command and arguments; actual process execution is not yet implemented.
 */
public class ExecutableCommand implements Command {
	/**
	 * Executes the external command.
	 *
	 * <p>Currently a stub implementation that outputs debug information about
	 * the command and its arguments. TODO: Implement actual process spawning.
	 *
	 * @param originalInput the complete original input string
	 * @param mainCommandStr the executable name/path
	 * @param args the command arguments
	 * @return debug output showing arguments passed
	 */
	@Override
	public String execute(String originalInput, String mainCommandStr, List<String> args) {
		StringBuilder sb = new StringBuilder();
		appendArgs(mainCommandStr, args, sb);
		// @TODO: execute actual command and append it's output
		return sb.toString();
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
