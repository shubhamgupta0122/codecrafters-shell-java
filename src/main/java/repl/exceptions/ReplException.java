package repl.exceptions;

/**
 * Base exception for REPL-related errors.
 *
 * <p>Thrown for truly unexpected errors during command evaluation or execution
 * (e.g., I/O errors, parsing errors). Expected command failures (non-zero exit codes)
 * are returned as CommandResult, not thrown as exceptions.
 */
public class ReplException extends Exception {
	/**
	 * Creates a new ReplException wrapping the given cause.
	 *
	 * @param cause the underlying exception that caused this error
	 */
	public ReplException(Throwable cause) {
		super(cause != null ? cause.getMessage() : null, cause);
	}

	/**
	 * Creates a new ReplException with a message and underlying cause.
	 *
	 * @param message the error message
	 * @param cause the underlying exception that caused this error
	 */
	public ReplException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Creates a new ReplException with a message.
	 *
	 * @param message the error message
	 */
	public ReplException(String message) {
		super(message);
	}
}
