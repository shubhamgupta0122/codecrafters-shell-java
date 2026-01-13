package repl.commands;

/**
 * Result of executing a command, containing stdout, stderr streams, and exit code.
 *
 * @param stdout the standard output produced by the command (empty string if none)
 * @param stderr the standard error produced by the command (empty string if none)
 * @param exitCode the exit code (0 for success, non-zero for failure)
 */
public record CommandResult(String stdout, String stderr, int exitCode) {
	/**
	 * Creates a successful result with stdout only (stderr empty, exit code 0).
	 */
	public static CommandResult success(String stdout) {
		return new CommandResult(stdout, "", 0);
	}

	/**
	 * Creates a successful result with no output (exit code 0).
	 */
	public static CommandResult empty() {
		return new CommandResult("", "", 0);
	}

	/**
	 * Creates a failed result with stderr only (stdout empty, exit code 1).
	 */
	public static CommandResult error(String stderr) {
		return new CommandResult("", stderr, 1);
	}

	/**
	 * Returns whether the command executed successfully (exit code 0).
	 */
	public boolean isSuccess() {
		return exitCode == 0;
	}
}
