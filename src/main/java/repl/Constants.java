package repl;

import java.util.List;

/**
 * Shared constants used throughout the REPL.
 */
public class Constants {
	/** Single space character used for string splitting and joining. */
	public static final Character WHITESPACE = ' ';

	/** Single quote character used for literal string parsing in shell commands. */
	public static final Character SINGLE_QUOTE = '\'';

	/** Double quote character used for literal string parsing in shell commands. */
	public static final Character DOUBLE_QUOTE = '"';

	/** Backslash character used for escaping special characters outside quotes. */
	public static final Character BACKSLASH = '\\';

//	public static final Character DOLLAR = '$';
//	public static final Character STAR = '*';
//	public static final Character QUESTION_MARK = '?';
//
//	public static final List<Character> SPECIAL_CHARACTERS = List.of(
//		WHITESPACE,
//		SINGLE_QUOTE,
//		DOUBLE_QUOTE,
//		BACKSLASH,
//		DOLLAR,
//		STAR,
//		QUESTION_MARK
//	);
}
