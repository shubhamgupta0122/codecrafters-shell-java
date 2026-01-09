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
 *   <li>Characters inside single or double quotes are treated literally</li>
 *   <li>Whitespace inside single or double quotes is preserved</li>
 *   <li>Adjacent quoted strings are concatenated into a single argument</li>
 *   <li>Empty quotes are ignored</li>
 *   <li>Backslash ({@code \}) outside quotes escapes the next character, treating it literally</li>
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
 * // Escaped backslash
 * get("echo hello\\world") → ExtractedCommand("echo", ["hello\world"])
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
	 *   <li>Inside single quotes: all characters (including whitespace) are literal</li>
	 *   <li>Inside double quotes: all characters (including whitespace) are literal</li>
	 *   <li>Escape character ({@code \}): outside quotes, causes the next character to be treated literally</li>
	 * </ul>
	 *
	 * <p>Uses StringBuilder for efficient string building during parsing.
	 *
	 * @param commandArgsStr the arguments portion of the input (after command name)
	 * @return list of parsed arguments
	 */
	private static List<String> normalizeCommandArgs(String commandArgsStr) {
		List<StringBuilder> builders = new ArrayList<>();
		boolean sQuoting = false;
		boolean dQuoting = false;
		boolean escaping = false;

		for (char c : commandArgsStr.toCharArray()) {
			if(escaping) {
				escaping = false;
				addCharToLastArg(c, builders);
				continue;
			} else if(!sQuoting && !dQuoting && c == BACKSLASH) {
				escaping = true;
				continue;
			}

			if(!sQuoting && c == DOUBLE_QUOTE) {
				dQuoting = !dQuoting;
				continue;
			}

			if(!dQuoting && c == SINGLE_QUOTE) {
				sQuoting = !sQuoting;
				continue;
			}

			if(dQuoting || sQuoting) {
				addCharToLastArg(c, builders);
			} else {
				if(c == WHITESPACE) {
					if(!hasLastElementAsEmpty(builders))
						builders.add(new StringBuilder());
				} else {
					addCharToLastArg(c, builders);
				}
			}
		}
		if(hasLastElementAsEmpty(builders))
			builders.removeLast();

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
