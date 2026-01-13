package repl;

/**
 * Centralized message constants for the shell.
 *
 * <p>Contains all user-facing error messages, status messages, and other
 * string constants used throughout the application. Centralizing messages
 * improves consistency and makes future internationalization easier.
 */
public final class Messages {

	// Prevent instantiation
	private Messages() {
		throw new UnsupportedOperationException("Messages class should not be instantiated");
	}

	// === Type command messages ===

	/** Message suffix indicating a command is a shell builtin. */
	public static final String TYPE_IS_SHELL_BUILTIN = " is a shell builtin";

	/** Message suffix indicating a command was not found. */
	public static final String TYPE_NOT_FOUND = ": not found";

	/** Error message when type command is missing its operand. */
	public static final String TYPE_MISSING_OPERAND = "type: missing operand";

	// === Bad command messages ===

	/** Message indicating a command was not found. */
	public static final String COMMAND_NOT_FOUND = "command not found";

	// === Change directory messages ===

	/** Error suffix for file/directory not found errors. */
	public static final String NO_SUCH_FILE_OR_DIRECTORY = ": No such file or directory";
}
