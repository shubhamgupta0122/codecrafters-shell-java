package repl.exceptions;

/**
 * Base exception for REPL-related errors.
 *
 * <p>Thrown for recoverable errors during command evaluation or execution
 * that should be reported to the user but shouldn't terminate the shell.
 */
public class ReplException extends Exception {
	/**
	 * Creates a new ReplException with the given error message.
	 *
	 * @param message the error message describing what went wrong
	 */
	public ReplException(String message) {
		super(message);
	}
}
