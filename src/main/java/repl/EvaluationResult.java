package repl;

import repl.commands.CommandResult;

/**
 * Result of evaluating a command in the REPL, including the command output
 * and any redirection targets specified by the user.
 *
 * <p>This separates command evaluation (what the command produced) from
 * I/O handling (where the output should go). The REPL uses this information
 * to decide whether to print output to the terminal or redirect to files.
 *
 * @param commandResult the result of executing the command (stdout, stderr, exit code)
 * @param stdoutRedirectTo target file for stdout redirection, or null if printing to terminal
 * @param stderrRedirectTo target file for stderr redirection, or null if printing to terminal
 */
public record EvaluationResult(
	CommandResult commandResult,
	String stdoutRedirectTo,
	String stderrRedirectTo
) {
	/**
	 * Checks if stdout should be redirected to a file.
	 *
	 * @return true if stdout redirection is specified, false otherwise
	 */
	public boolean hasStdoutRedirect() {
		return stdoutRedirectTo != null;
	}

	/**
	 * Checks if stderr should be redirected to a file.
	 *
	 * @return true if stderr redirection is specified, false otherwise
	 */
	public boolean hasStderrRedirect() {
		return stderrRedirectTo != null;
	}
}
