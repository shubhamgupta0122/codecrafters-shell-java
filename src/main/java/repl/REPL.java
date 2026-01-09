package repl;

import repl.exceptions.GracefulExitException;
import repl.exceptions.ReplException;
import repl.utils.DirUtils;

import java.util.Scanner;

/**
 * The main Read-Eval-Print Loop that orchestrates the interactive shell.
 *
 * <p>Implements the classic REPL pattern: reads user input, evaluates commands,
 * prints results, and loops until exit. Uses tail recursion for the main loop.
 *
 * <p>Example usage:
 * <pre>{@code
 * REPL repl = new REPL();
 * repl.loop(); // Starts the interactive shell
 * }</pre>
 *
 * @see ReplEvaluator
 */
public class REPL {

	/** The context builder with shared services (reused across commands). */
	private final ReplContext.Builder contextBuilder;

	/** Scanner for reading user input (reused across all inputs). */
	private final Scanner scanner = new Scanner(System.in);

	/**
	 * Creates a new REPL instance with default shared services.
	 */
	public REPL() {
		this(new DirUtils());
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
	 * <p>Repeatedly reads user input, evaluates it, and prints output until the user
	 * exits. Uses tail recursion instead of an explicit while loop.
	 *
	 * <p>Flow: read → eval → print → loop (repeat)
	 *
	 * @throws RuntimeException if a non-exit ReplException occurs
	 */
	@SuppressWarnings("InfiniteRecursion")
	public void loop() {
		try {
			String input = read();
			ReplEvaluator evaluator = new ReplEvaluator(input, contextBuilder);
			String output = evaluator.eval();
			print(output);
			loop();
		} catch (GracefulExitException _) {
		} catch (ReplException e) {
			System.err.println(e.getMessage());
			loop();
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
	 * Prints command output to stdout.
	 *
	 * @param output the output string to print
	 */
	private void print(String output) {
		if(output != null)
			System.out.println(output);
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
