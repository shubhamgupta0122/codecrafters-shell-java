package repl.exceptions;

/**
 * Base exception for REPL-related errors.
 *
 * <p>Thrown for recoverable errors during command evaluation or execution
 * that should be reported to the user but shouldn't terminate the shell.
 */
public class ReplException extends Exception {
	/**
	 * Optional stdout that was captured before the exception was thrown.
	 * Used for redirection when a command fails but still produced output.
	 */
	private final String capturedStdout;

	/**
	 * Creates a new ReplException wrapping the given cause.
	 *
	 * @param cause the underlying exception that caused this error
	 */
	public ReplException(Throwable cause) {
		super(cause.getMessage(), cause);
		this.capturedStdout = null;
	}

	public ReplException(String message, Throwable cause) {
		super(message, cause);
		this.capturedStdout = null;
	}

	public ReplException(String message) {
		super(message);
		this.capturedStdout = null;
	}

	/**
	 * Creates a new ReplException with a message and captured stdout.
	 * Used when a command fails but produced output that should be redirected.
	 *
	 * @param message the error message
	 * @param capturedStdout the stdout that was captured before failure
	 */
	public ReplException(String message, String capturedStdout) {
		super(message);
		this.capturedStdout = capturedStdout;
	}

	/**
	 * Returns the stdout that was captured before the exception was thrown.
	 *
	 * @return captured stdout, or null if none
	 */
	public String getCapturedStdout() {
		return capturedStdout;
	}
}
