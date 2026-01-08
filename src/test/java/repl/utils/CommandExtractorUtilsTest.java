package repl.utils;

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

	@Test
	void get_singleQuotedArg_preservesSpaces() {
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo 'hello     world'");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("hello     world"), result.args());
	}

	@Test
	void get_multipleSpacesOutsideQuotes_collapsesToSingleDelimiter() {
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo hello     world");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("hello", "world"), result.args());
	}

	@Test
	void get_adjacentQuotedStrings_concatenatesIntoSingleArg() {
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo 'hello''world'");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("helloworld"), result.args());
	}

	@Test
	void get_emptyQuotes_ignoredAndConcatenates() {
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo hello''world");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("helloworld"), result.args());
	}

	@Test
	void get_quotedAndUnquotedMixed_concatenatesCorrectly() {
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo hello'world'");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("helloworld"), result.args());
	}

	@Test
	void get_multipleQuotedArgs_parsesAsSeparateArgs() {
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo 'hello' 'world'");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("hello", "world"), result.args());
	}

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

	@Test
	void get_specialCharsInsideQuotes_treatedLiterally() {
		CommandExtractorUtils.ExtractedCommand result = CommandExtractorUtils.get("echo '$HOME ~/*.txt'");

		assertEquals("echo", result.mainCommandStr());
		assertEquals(List.of("$HOME ~/*.txt"), result.args());
	}
}
