package repl.utils;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandExtractorUtilsTest {

	@Test
	void get_commandOnly_returnsCommandWithEmptyArgs() {
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("pwd");

		assertEquals("pwd", result.mainCommandStr());
		assertTrue(result.args().isEmpty());
	}

	@Test
	void get_commandWithSingleArg_parsesCorrectly() {
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo hello");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("hello"), result.args());
	}

	@Test
	void get_commandWithMultipleArgs_parsesCorrectly() {
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo hello world");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("hello", "world"), result.args());
	}

	// === Stage #NI6: Quoting - Single quotes ===

	@Test
	@Tag("NI6")
	void get_singleQuotedArg_preservesSpaces() {
		// echo 'example script' → example script
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo 'hello     world'");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("hello     world"), result.args());
	}

	@Test
	@Tag("NI6")
	void get_multipleSpacesOutsideQuotes_collapsesToSingleDelimiter() {
		// echo script     world → script world
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo hello     world");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("hello", "world"), result.args());
	}

	@Test
	@Tag("NI6")
	void get_adjacentSingleQuotedStrings_concatenatesIntoSingleArg() {
		// 'world''example' → worldexample
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo 'hello''world'");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("helloworld"), result.args());
	}

	@Test
	@Tag("NI6")
	void get_emptySingleQuotes_ignoredAndConcatenates() {
		// script''test → scripttest
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo hello''world");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("helloworld"), result.args());
	}

	@Test
	@Tag("NI6")
	void get_singleQuotedAndUnquotedMixed_concatenatesCorrectly() {
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo hello'world'");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("helloworld"), result.args());
	}

	@Test
	@Tag("NI6")
	void get_multipleSingleQuotedArgs_parsesAsSeparateArgs() {
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo 'hello' 'world'");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("hello", "world"), result.args());
	}

	@Test
	@Tag("NI6")
	void get_complexMixedSingleQuotingAndConcatenation() {
		// echo 'shell     hello' 'world''example' script''test → shell     hello worldexample scripttest
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo 'shell     hello' 'world''example' script''test");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("shell     hello", "worldexample", "scripttest"), result.args());
	}

	@Test
	@Tag("NI6")
	void get_specialCharsInsideSingleQuotes_treatedLiterally() {
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo '$HOME ~/*.txt'");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("$HOME ~/*.txt"), result.args());
	}

	@Test
	@Tag("NI6")
	void get_doubleQuoteInsideSingleQuotes_treatedLiterally() {
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo '\"hello\"'");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("\"hello\""), result.args());
	}

	// === Stage #TG6: Quoting - Double quotes ===

	@Test
	@Tag("TG6")
	void get_doubleQuotedArg_preservesSpaces() {
		// echo "example test" → example test
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo \"hello     world\"");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("hello     world"), result.args());
	}

	@Test
	@Tag("TG6")
	void get_adjacentDoubleQuotedStrings_concatenatesIntoSingleArg() {
		// "hello""example" → helloexample
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo \"hello\"\"world\"");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("helloworld"), result.args());
	}

	@Test
	@Tag("TG6")
	void get_emptyDoubleQuotes_ignoredAndConcatenates() {
		// test""shell → testshell
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo hello\"\"world");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("helloworld"), result.args());
	}

	@Test
	@Tag("TG6")
	void get_doubleQuotedAndUnquotedMixed_concatenatesCorrectly() {
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo hello\"world\"");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("helloworld"), result.args());
	}

	@Test
	@Tag("TG6")
	void get_multipleDoubleQuotedArgs_parsesAsSeparateArgs() {
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo \"hello\" \"world\"");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("hello", "world"), result.args());
	}

	@Test
	@Tag("TG6")
	void get_doubleQuotedWithSpacesBetweenAndAdjacentQuotes() {
		// echo "test  shell"  "hello""example" → test  shell helloexample
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo \"test  shell\"  \"hello\"\"example\"");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("test  shell", "helloexample"), result.args());
	}

	@Test
	@Tag("TG6")
	void get_singleQuoteInsideDoubleQuotes() {
		// echo "script's" → script's
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo \"script's\"");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("script's"), result.args());
	}

	@Test
	@Tag("TG6")
	void get_complexMixedDoubleQuotesAndUnquoted() {
		// echo "hello"  "script's"  test""shell → hello script's testshell
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo \"hello\"  \"script's\"  test\"\"shell");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("hello", "script's", "testshell"), result.args());
	}

	@Test
	@Tag("TG6")
	void get_specialCharsInsideDoubleQuotes_treatedLiterally() {
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo \"$HOME ~/*.txt\"");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("$HOME ~/*.txt"), result.args());
	}

	@Test
	@Tag("TG6")
	void get_mixedSingleAndDoubleQuotes_handledCorrectly() {
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo 'hello'\"world\"");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("helloworld"), result.args());
	}

	@Test
	@Tag("TG6")
	@Tag("GU3")
	void get_backslashInsideDoubleQuotes_preservedForNonEscapableChars() {
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo \"hello\\world\"");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("hello\\world"), result.args());
	}

	@Test
	@Tag("TG6")
	@Tag("GU3")
	void get_multipleBackslashesInsideDoubleQuotes_escapesBackslash() {
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo \"test\\\\case\"");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("test\\case"), result.args());
	}

	@Test
	@Tag("TG6")
	@Tag("GU3")
	void get_escapedDoubleQuoteInsideDoubleQuotes_treatedAsLiteral() {
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo \"say \\\"hello\\\" world\"");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("say \"hello\" world"), result.args());
	}

	// === Stage #YT5: Quoting - Backslash outside quotes ===

	@Test
	@Tag("YT5")
	void get_escapedSpace_createsSingleArg() {
		// echo hello\ world → hello world
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo hello\\ world");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("hello world"), result.args());
	}

	@Test
	@Tag("YT5")
	void get_multipleConsecutiveEscapedSpaces() {
		// echo shell\ \ \ \ \ \ script → shell      script
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo shell\\ \\ \\ \\ \\ \\ script");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("shell      script"), result.args());
	}

	@Test
	@Tag("YT5")
	void get_escapedQuoteCharacters() {
		// echo \'\"script world\"\' → '"script world"'
		// When echoed, args ['\"script, world\"'] join to: '"script world"'
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo \\'\\\"script world\\\"\\'" );

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("'\"script", "world\"'"), result.args());
	}

	@Test
	@Tag("YT5")
	void get_escapedRegularCharacter() {
		// echo world\ntest → worldntest (backslash-n is just 'n', not newline)
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo world\\ntest");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("worldntest"), result.args());
	}

	@Test
	@Tag("YT5")
	void get_escapedBackslash_preservesBackslash() {
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo hello\\\\world");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("hello\\world"), result.args());
	}

	@Test
	@Tag("YT5")
	void get_multipleEscapedSpaces_createsSingleArg() {
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo hello\\ beautiful\\ world");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("hello beautiful world"), result.args());
	}

	@Test
	@Tag("YT5")
	void get_escapedCharacterAtEnd_preservesCharacter() {
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo hello\\!");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("hello!"), result.args());
	}

	@Test
	@Tag("YT5")
	void get_mixedEscapeAndQuotes_handledCorrectly() {
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo 'hello'\\ world");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("hello world"), result.args());
	}

	@Test
	@Tag("LE5")
	void get_backslashInsideSingleQuotes_treatedLiterally() {
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo 'hello\\world'");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("hello\\world"), result.args());
	}

	@Test
	@Tag("LE5")
	void get_multipleBackslashesInsideSingleQuotes_treatedLiterally() {
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo 'test\\\\case'");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("test\\\\case"), result.args());
	}

	@Test
	@Tag("YT5")
	void get_trailingBackslash_isIgnored() {
		// Trailing backslash is dropped (matches bash behavior)
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo hello\\");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("hello"), result.args());
	}

	// === General parsing tests ===

	@Test
	void get_leadingWhitespace_strippedBeforeParsing() {
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("   echo hello");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("hello"), result.args());
	}

	@Test
	void get_trailingWhitespace_strippedBeforeParsing() {
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo hello   ");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("hello"), result.args());
	}

	@Test
	void get_multipleSpacesBetweenCommandAndArgs_handledCorrectly() {
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo   hello");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("hello"), result.args());
	}

	// === Quote Validation Tests ===

	@Test
	void get_unclosedSingleQuote_throwsIllegalArgumentException() {
		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> CommandExtractorUtils.get("echo 'hello")
		);

		assertEquals("Unclosed quote in input", exception.getMessage());
	}

	@Test
	void get_unclosedDoubleQuote_throwsIllegalArgumentException() {
		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> CommandExtractorUtils.get("echo \"hello")
		);

		assertEquals("Unclosed quote in input", exception.getMessage());
	}

	@Test
	void get_unclosedSingleQuoteWithMultipleArgs_throwsIllegalArgumentException() {
		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> CommandExtractorUtils.get("echo hello 'world")
		);

		assertEquals("Unclosed quote in input", exception.getMessage());
	}

	@Test
	void get_unclosedDoubleQuoteWithMultipleArgs_throwsIllegalArgumentException() {
		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> CommandExtractorUtils.get("echo hello \"world")
		);

		assertEquals("Unclosed quote in input", exception.getMessage());
	}

	@Test
	void get_unclosedSingleQuoteAtEnd_throwsIllegalArgumentException() {
		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> CommandExtractorUtils.get("echo hello world '")
		);

		assertEquals("Unclosed quote in input", exception.getMessage());
	}

	@Test
	void get_unclosedDoubleQuoteAtEnd_throwsIllegalArgumentException() {
		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> CommandExtractorUtils.get("echo hello world \"")
		);

		assertEquals("Unclosed quote in input", exception.getMessage());
	}
}
