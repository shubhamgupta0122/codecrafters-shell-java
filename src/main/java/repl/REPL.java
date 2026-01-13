package repl;

import repl.exceptions.GracefulExitException;
import repl.exceptions.ReplException;
import repl.utils.DirUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * The main Read-Eval-Print Loop that orchestrates the interactive shell.
 *
 * <p>Implements the classic REPL pattern: reads user input, evaluates commands,
 * handles I/O redirection, prints results, and loops until exit. Uses tail recursion
 * for the main loop.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Reading user input from stdin</li>
 *   <li>Delegating command evaluation to {@link ReplEvaluator}</li>
 *   <li>Handling stdout and stderr redirection to files</li>
 *   <li>Printing output to the terminal</li>
 *   <li>Error handling and recovery</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>{@code
 * REPL repl = new REPL();
 * repl.loop(); // Starts the interactive shell
 * }</pre>
 *
 * @see ReplEvaluator
 * @see EvaluationResult
 */
public class REPL {

	/** The context builder with shared services (reused across commands). */
	private final ReplContext.Builder contextBuilder;

	/**
	 * Scanner for reading user input (reused across all inputs).
	 *
	 * <p><strong>Resource Management:</strong> This Scanner is intentionally NOT closed
	 * because closing it would close System.in, preventing any further input.
	 * The Scanner lifecycle is tied to the REPL lifecycle and terminates when
	 * the JVM exits. This is the correct pattern for interactive CLI applications.
	 */
	private final Scanner scanner = new Scanner(System.in);

	/**
	 * Creates a new REPL instance with default shared services.
	 *
	 * <p>Redirects stderr to stdout for all error output (including command errors
	 * printed by {@link #handleIO}). This ensures consistent output ordering with prompts.
	 * Commands that specify stderr redirection via {@code 2>} write to files before
	 * this global redirection affects them.
	 */
	public REPL() {
		this(new DirUtils());
		// Redirect all stderr to stdout for proper ordering
		// This affects both REPL errors and command stderr (if not redirected to file)
		System.setErr(System.out);
	}

	/**
	 * Creates a new REPL instance with the given DirUtils.
	 *
	 * <p>Useful for testing with custom directory state.
	 *
	 * @param dirUtils the directory utilities instance
	 */
	public REPL(DirUtils dirUtils) {
		this.contextBuilder = ReplContext.builder(dirUtils);
	}

	/**
	 * Starts the main REPL loop.
	 *
	 * <p>Repeatedly reads user input, evaluates it, handles I/O redirection,
	 * and prints output until the user exits. Uses tail recursion instead of
	 * an explicit while loop.
	 *
	 * <p>Flow: read → eval → handle I/O → loop (repeat)
	 *
	 * <p>Errors are caught and printed to stderr. The shell continues
	 * running after errors.
	 */
	@SuppressWarnings("InfiniteRecursion")
	public void loop() {
		try {
			String input = read();
			ReplEvaluator evaluator = new ReplEvaluator(input, contextBuilder);
			EvaluationResult result = evaluator.eval();
			handleIO(result);
			loop();
		} catch (GracefulExitException _) {
		} catch (ReplException e) {
			System.err.println(e.getMessage());
			loop();
		} catch (RuntimeException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			loop();
		}
	}

	/**
	 * Handles command output based on evaluation result.
	 *
	 * <p>For each output stream (stdout, stderr):
	 * <ul>
	 *   <li>If redirection is specified, writes to file</li>
	 *   <li>Otherwise, prints to the corresponding terminal stream</li>
	 * </ul>
	 *
	 * <p>Package-private for testing.
	 *
	 * @param result the evaluation result containing output and redirect targets
	 * @throws ReplException if file I/O fails during redirection
	 */
	void handleIO(EvaluationResult result) throws ReplException {
		// Handle stdout
		if (result.hasStdoutRedirect()) {
			redirectOutput(result.commandResult().stdout(), result.stdoutRedirectTo());
		} else if (!result.commandResult().stdout().isEmpty()) {
			System.out.println(result.commandResult().stdout());
		}

		// Handle stderr
		if (result.hasStderrRedirect()) {
			redirectOutput(result.commandResult().stderr(), result.stderrRedirectTo());
		} else if (!result.commandResult().stderr().isEmpty()) {
			System.err.println(result.commandResult().stderr());
		}
	}

	/**
	 * Redirects output to a file.
	 *
	 * <p>Creates parent directories if they don't exist. Overwrites the file
	 * if it already exists.
	 *
	 * @param output the output string to write
	 * @param redirectTo the target file path (relative to current working directory)
	 * @throws ReplException if file I/O fails
	 */
	private void redirectOutput(String output, String redirectTo) throws ReplException {
		try {
			// Resolve relative paths against current working directory
			Path outputPath = contextBuilder.getDirUtils().getCurrentDir().resolve(redirectTo);

			// Ensure parent directories exist
			Path parentDir = outputPath.getParent();
			if (parentDir != null && !Files.exists(parentDir)) {
				Files.createDirectories(parentDir);
			}

			Files.writeString(outputPath, output, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new ReplException(e);
		}
	}

	/**
	 * Reads user input from stdin.
	 *
	 * <p>Displays the prompt and waits for a complete line of input.
	 *
	 * @return the user input as a string
	 */
	private String read() {
		showPrompt();
		return readPrompt();
	}

	/**
	 * Displays the shell prompt to the user.
	 */
	private void showPrompt() {
		System.out.print("$ ");
	}

	/**
	 * Reads a line of input from the user.
	 *
	 * @return the input line as a string
	 */
	private String readPrompt() {
		return scanner.nextLine();
	}

}
