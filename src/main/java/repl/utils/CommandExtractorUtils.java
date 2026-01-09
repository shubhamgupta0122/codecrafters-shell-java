package repl.utils;

import java.util.ArrayList;
import java.util.List;

import static repl.Constants.*;

/**
 * Utility class for extracting and parsing shell commands from input strings.
 *
 * <p>Provides functionality to split a raw command line into a main command
 * and its arguments, properly handling whitespace, quoted strings, and escape characters.
 *
 * <p>Handles quoting and escaping according to shell semantics:
 * <ul>
 *   <li>Characters inside single quotes are treated literally (no escape sequences)</li>
 *   <li>Characters inside double quotes are treated literally, except backslash escapes</li>
 *   <li>Whitespace inside single or double quotes is preserved</li>
 *   <li>Adjacent quoted strings are concatenated into a single argument</li>
 *   <li>Empty quotes are ignored</li>
 *   <li>Backslash ({@code \}) outside quotes or inside double quotes escapes the next character</li>
 * </ul>
 *
 * <p>Examples:
 * <pre>{@code
 * // Spaces preserved inside single quotes
 * get("echo 'hello     world'") → ExtractedCommand("echo", ["hello     world"])
 *
 * // Spaces preserved inside double quotes
 * get("echo "hello     world"") → ExtractedCommand("echo", ["hello     world"])
 *
 * // Spaces collapsed outside quotes
 * get("echo hello     world") → ExtractedCommand("echo", ["hello", "world"])
 *
 * // Adjacent quotes concatenate
 * get("echo 'hello''world'") → ExtractedCommand("echo", ["helloworld"])
 *
 * // Mixing single and double quotes
 * get("echo 'hello'"world"") → ExtractedCommand("echo", ["helloworld"])
 *
 * // Escaped space creates single argument
 * get("echo hello\ world") → ExtractedCommand("echo", ["hello world"])
 *
 * // Escaped backslash outside quotes
 * get("echo hello\\world") → ExtractedCommand("echo", ["hello\world"])
 *
 * // Backslash escapes inside double quotes
 * get("echo "hello\world"") → ExtractedCommand("echo", ["helloworld"])
 *
 * // Double backslash in double quotes produces single backslash
 * get("echo "test\\case"") → ExtractedCommand("echo", ["test\case"])
 * }</pre>
 */
public class CommandExtractorUtils {

	/**
	 * Represents a parsed command with its name and arguments.
	 *
	 * @param mainCommandStr the command name (first token)
	 * @param args the list of parsed arguments (may be empty, never null)
	 */
	public record ExtractedCommand(
		String mainCommandStr,
		List<String> args
	) { }

	/**
	 * Represents the parsing state while processing command arguments.
	 *
	 * <p>The parser operates as a state machine with the following states:
	 * <ul>
	 *   <li>{@link #NORMAL} - Default state, outside any quotes</li>
	 *   <li>{@link #SINGLE_QUOTED} - Inside single quotes, all characters are literal</li>
	 *   <li>{@link #DOUBLE_QUOTED} - Inside double quotes, backslash can escape characters</li>
	 *   <li>{@link #ESCAPING} - Next character will be escaped (outside quotes)</li>
	 *   <li>{@link #ESCAPING_IN_DOUBLE_QUOTES} - Next character will be escaped (inside double quotes)</li>
	 * </ul>
	 */
	private enum ParserState {
		/** Normal parsing mode, outside of any quotes. */
		NORMAL,

		/** Inside single quotes; all characters (including whitespace) are treated literally. */
		SINGLE_QUOTED,

		/** Inside double quotes; most characters are literal, but backslash escapes the next character. */
		DOUBLE_QUOTED,

		/** Next character will be escaped and treated literally (outside quotes). */
		ESCAPING,

		/** Next character will be escaped and treated literally (inside double quotes). */
		ESCAPING_IN_DOUBLE_QUOTES
	}

	/**
	 * Extracts the command name and arguments from an input string.
	 *
	 * <p>Leading and trailing whitespace is stripped before parsing.
	 * Splits on the first whitespace to get the command name, then parses
	 * the remaining string for arguments with single-quote handling.
	 *
	 * @param originalInput the complete input string to parse
	 * @return an ExtractedCommand containing the command name and parsed arguments
	 */
	public static ExtractedCommand get(String originalInput) {
		String strippedInput = originalInput.strip();
		String mainCommandStr;
		List<String> args;
		int firstWhiteSpaceIndex = strippedInput.indexOf(WHITESPACE);
		if(firstWhiteSpaceIndex == -1) {
			mainCommandStr = strippedInput;
			args = new ArrayList<>();
		} else {
			mainCommandStr = strippedInput.substring(0, firstWhiteSpaceIndex);
			String commandArgsStr = strippedInput.substring(firstWhiteSpaceIndex + 1);
			args = normalizeCommandArgs(commandArgsStr);
		}
		return new ExtractedCommand(mainCommandStr, args);
	}

	/**
	 * Parses a command arguments string into a list of individual arguments.
	 *
	 * <p>Uses a state machine to handle quoting and escaping:
	 * <ul>
	 *   <li>Outside quotes: whitespace delimits arguments, backslash escapes the next character</li>
	 *   <li>Inside single quotes: all characters (including whitespace) are literal, no escaping</li>
	 *   <li>Inside double quotes: most characters are literal, but backslash escapes the next character</li>
	 *   <li>Escape character ({@code \}): outside quotes or inside double quotes, causes the next character to be treated literally</li>
	 * </ul>
	 *
	 * <p>Uses StringBuilder for efficient string building during parsing.
	 *
	 * <p>Validates that all quotes are properly closed. Unclosed quotes will result
	 * in an IllegalArgumentException being thrown.
	 *
	 * @param commandArgsStr the arguments portion of the input (after command name)
	 * @return list of parsed arguments
	 * @throws IllegalArgumentException if the input contains unclosed quotes
	 */
	private static List<String> normalizeCommandArgs(String commandArgsStr) {
		List<StringBuilder> builders = new ArrayList<>();
		ParserState state = ParserState.NORMAL;

		for (char c : commandArgsStr.toCharArray()) {
			state = switch (state) {
				case ESCAPING -> {
					// Escaped character: add it literally and return to NORMAL
					addCharToLastArg(c, builders);
					yield ParserState.NORMAL;
				}

				case ESCAPING_IN_DOUBLE_QUOTES -> {
					addCharToLastArg(c, builders);
					yield ParserState.DOUBLE_QUOTED;
				}

				case NORMAL -> {
					if (c == BACKSLASH) {
						yield ParserState.ESCAPING;
					} else if (c == SINGLE_QUOTE) {
						yield ParserState.SINGLE_QUOTED;
					} else if (c == DOUBLE_QUOTE) {
						yield ParserState.DOUBLE_QUOTED;
					} else if (c == WHITESPACE) {
						if (!hasLastElementAsEmpty(builders)) {
							builders.add(new StringBuilder());
						}
						yield ParserState.NORMAL;
					} else {
						addCharToLastArg(c, builders);
						yield ParserState.NORMAL;
					}
				}

				case SINGLE_QUOTED -> {
					if (c == SINGLE_QUOTE) {
						// Close single quote
						yield ParserState.NORMAL;
					} else {
						// Inside single quotes: all chars are literal
						addCharToLastArg(c, builders);
						yield ParserState.SINGLE_QUOTED;
					}
				}

				case DOUBLE_QUOTED -> {
					if (c == DOUBLE_QUOTE) {
						// Close double quote
						yield ParserState.NORMAL;
					} else if (c == BACKSLASH) {
						yield ParserState.ESCAPING_IN_DOUBLE_QUOTES;
					} else {
						// Inside double quotes: all chars are literal
						addCharToLastArg(c, builders);
						yield ParserState.DOUBLE_QUOTED;
					}
				}
			};
		}

		// Validate that we ended in a valid state
		if (!(state == ParserState.NORMAL || state == ParserState.ESCAPING)) {
			throw new IllegalArgumentException("Unclosed quote in input");
		}

		if (hasLastElementAsEmpty(builders)) {
			builders.removeLast();
		}

		return builders.stream()
				.map(StringBuilder::toString)
				.toList();
	}

	private static void addCharToLastArg(char c, List<StringBuilder> builders) {
		if (builders.isEmpty()) {
			builders.add(new StringBuilder());
		}
		builders.getLast().append(c);
	}

	private static boolean hasLastElementAsEmpty(List<StringBuilder> builders) {
		return !builders.isEmpty() && builders.getLast().isEmpty();
	}
}
