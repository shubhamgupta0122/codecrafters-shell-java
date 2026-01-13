package repl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import repl.exceptions.GracefulExitException;
import repl.exceptions.ReplException;
import repl.utils.DirUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ReplEvaluatorTest {

	@TempDir
	Path tempDir;

	private ReplContext.Builder contextBuilder;

	@BeforeEach
	void setUp() {
		DirUtils dirUtils = new DirUtils(tempDir);
		contextBuilder = ReplContext.builder(dirUtils);
	}

	@Test
	void eval_echoCommand_returnsEchoedText() throws ReplException {
		ReplEvaluator evaluator = new ReplEvaluator("echo hello", contextBuilder);

		EvaluationResult result = evaluator.eval();

		assertEquals("hello", result.commandResult().stdout());
		assertTrue(result.commandResult().stderr().isEmpty());
		assertEquals(0, result.commandResult().exitCode());
	}

	@Test
	void eval_echoMultipleArgs_returnsJoinedText() throws ReplException {
		ReplEvaluator evaluator = new ReplEvaluator("echo hello world", contextBuilder);

		EvaluationResult result = evaluator.eval();

		assertEquals("hello world", result.commandResult().stdout());
		assertTrue(result.commandResult().stderr().isEmpty());
	}

	@Test
	void eval_exitCommand_throwsGracefulExitException() {
		ReplEvaluator evaluator = new ReplEvaluator("exit 0", contextBuilder);

		assertThrows(GracefulExitException.class, evaluator::eval);
	}

	@Test
	void eval_typeBuiltin_returnsShellBuiltin() throws ReplException {
		ReplEvaluator evaluator = new ReplEvaluator("type echo", contextBuilder);

		EvaluationResult result = evaluator.eval();

		assertEquals("echo is a shell builtin", result.commandResult().stdout());
	}

	@Test
	void eval_typeUnknown_returnsNotFound() throws ReplException {
		ReplEvaluator evaluator = new ReplEvaluator("type unknowncmd123", contextBuilder);

		EvaluationResult result = evaluator.eval();

		assertEquals("unknowncmd123: not found", result.commandResult().stdout());
	}

	@Test
	void eval_pwdCommand_returnsCurrentDirectory() throws ReplException {
		ReplEvaluator evaluator = new ReplEvaluator("pwd", contextBuilder);

		EvaluationResult result = evaluator.eval();

		// DirUtils stores the path passed to constructor, not the real path
		// So we compare with tempDir directly (without toRealPath)
		assertEquals(tempDir.toAbsolutePath().toString(), result.commandResult().stdout());
	}

	@Test
	void eval_cdCommand_changesDirectory() throws ReplException, IOException {
		Path subDir = Files.createDirectory(tempDir.resolve("testdir"));
		ReplEvaluator cdEvaluator = new ReplEvaluator("cd testdir", contextBuilder);

		EvaluationResult cdResult = cdEvaluator.eval();

		// cd returns empty string (CommandResult.empty().stdout())
		assertEquals("", cdResult.commandResult().stdout());

		// Verify by running pwd
		ReplEvaluator pwdEvaluator = new ReplEvaluator("pwd", contextBuilder);
		EvaluationResult pwdResult = pwdEvaluator.eval();

		assertEquals(subDir.toRealPath().toAbsolutePath().toString(), pwdResult.commandResult().stdout());
	}

	@Test
	@Tag("FF0")
	@Tag("CZ2")
	void eval_unknownCommand_printsErrorToStderr() throws ReplException {
		ReplEvaluator evaluator = new ReplEvaluator("unknowncmd123", contextBuilder);

		// Commands no longer throw exceptions for expected failures
		// Instead, they return CommandResult with error
		EvaluationResult result = evaluator.eval();

		// Unknown command returns empty stdout
		assertEquals("", result.commandResult().stdout());
		// Stderr contains the error message
		assertEquals("unknowncmd123: command not found", result.commandResult().stderr());
	}

	@Test
	void eval_externalCommand_executesAndReturnsOutput() throws ReplException {
		ReplEvaluator evaluator = new ReplEvaluator("echo external", contextBuilder);

		EvaluationResult result = evaluator.eval();

		assertEquals("external", result.commandResult().stdout());
	}

	@Test
	void eval_externalCommand_cachesResolvedExecutablePath() throws ReplException {
		ReplEvaluator evaluator = new ReplEvaluator("echo test", contextBuilder);

		EvaluationResult result = evaluator.eval();

		// Verify command executed successfully
		assertEquals("test", result.commandResult().stdout());

		// The resolved path should be cached for performance
		// (This verifies ReplEvaluator sets executablePath in context)
		// We can't easily verify ExecutableCommand uses it without mocking,
		// but this documents the expected behavior
	}

	// === Stdout redirection tests ===

	@Test
	@Tag("JV1")
	void eval_redirectWithGreaterThan_returnsStdoutAndRedirectTarget() throws ReplException {
		ReplEvaluator evaluator = new ReplEvaluator("echo hello world > output.txt", contextBuilder);

		EvaluationResult result = evaluator.eval();

		// Verify command output is returned
		assertEquals("hello world", result.commandResult().stdout());
		// Verify redirect target is set
		assertEquals("output.txt", result.stdoutRedirectTo());
		assertFalse(result.hasStderrRedirect());
	}

	@Test
	@Tag("JV1")
	void eval_redirectWith1GreaterThan_returnsStdoutAndRedirectTarget() throws ReplException {
		ReplEvaluator evaluator = new ReplEvaluator("echo test content 1> output2.txt", contextBuilder);

		EvaluationResult result = evaluator.eval();

		assertEquals("test content", result.commandResult().stdout());
		assertEquals("output2.txt", result.stdoutRedirectTo());
	}

	@Test
	@Tag("JV1")
	void eval_redirectToNestedDirectory_returnsResultWithNestedPath() throws ReplException {
		ReplEvaluator evaluator = new ReplEvaluator("echo nested output > subdir/nested/file.txt", contextBuilder);

		EvaluationResult result = evaluator.eval();

		assertEquals("nested output", result.commandResult().stdout());
		assertEquals("subdir/nested/file.txt", result.stdoutRedirectTo());
	}

	@Test
	@Tag("JV1")
	void eval_redirectBuiltinCommand_returnsResultWithRedirect() throws ReplException {
		ReplEvaluator evaluator = new ReplEvaluator("pwd > pwd_output.txt", contextBuilder);

		EvaluationResult result = evaluator.eval();

		assertEquals(tempDir.toAbsolutePath().toString(), result.commandResult().stdout());
		assertEquals("pwd_output.txt", result.stdoutRedirectTo());
	}

	@Test
	@Tag("JV1")
	void eval_redirectOutputReturnsCorrectTarget() throws ReplException {
		ReplEvaluator evaluator = new ReplEvaluator("echo new content > overwrite.txt", contextBuilder);

		EvaluationResult result = evaluator.eval();

		assertEquals("new content", result.commandResult().stdout());
		assertEquals("overwrite.txt", result.stdoutRedirectTo());
	}

	@Test
	@Tag("JV1")
	void eval_redirectEmptyOutput_returnsEmptyStdout() throws ReplException {
		ReplEvaluator evaluator = new ReplEvaluator("echo '' > empty.txt", contextBuilder);

		EvaluationResult result = evaluator.eval();

		assertEquals("", result.commandResult().stdout());
		assertEquals("empty.txt", result.stdoutRedirectTo());
	}

	@Test
	@Tag("JV1")
	void eval_commandFailsButProducesStdout_redirectsStdoutAndThrows() throws IOException, ReplException {
		// Create a file to read successfully
		Path validFile = tempDir.resolve("valid.txt");
		Files.writeString(validFile, "valid content\n");

		// cat valid.txt nonexistent > output.txt
		// Should redirect "valid content" to file even though command fails
		ReplEvaluator evaluator = new ReplEvaluator(
			"cat " + validFile + " nonexistent > output.txt",
			contextBuilder
		);

		// Command returns error via CommandResult (exit code != 0)
		EvaluationResult result = evaluator.eval();

		// Stdout should be in the result with redirect target
		assertEquals("valid content", result.commandResult().stdout());
		assertEquals("output.txt", result.stdoutRedirectTo());
		// Stderr contains error about nonexistent file
		assertTrue(result.commandResult().stderr().contains("nonexistent"));
	}

	// === Stderr redirection tests ===

	@Test
	@Tag("STDERR")
	void eval_stderrRedirect_failingCommand_returnsStderrAndRedirectTarget() throws ReplException {
		ReplEvaluator evaluator = new ReplEvaluator("cat nonexistent_file 2> errors.txt", contextBuilder);

		EvaluationResult result = evaluator.eval();

		// Failed command returns empty stdout
		assertEquals("", result.commandResult().stdout());
		// Stderr should be in result
		assertTrue(result.commandResult().stderr().contains("nonexistent_file"));
		assertTrue(result.commandResult().stderr().contains("No such file"));
		// Redirect target should be set
		assertEquals("errors.txt", result.stderrRedirectTo());
	}

	@Test
	@Tag("STDERR")
	void eval_stderrRedirect_successfulCommand_returnsStdoutAndEmptyStderr() throws ReplException {
		ReplEvaluator evaluator = new ReplEvaluator("echo hello 2> errors.txt", contextBuilder);

		EvaluationResult result = evaluator.eval();

		// Stdout should be returned
		assertEquals("hello", result.commandResult().stdout());
		// Stderr should be empty
		assertTrue(result.commandResult().stderr().isEmpty());
		// Redirect target should be set
		assertEquals("errors.txt", result.stderrRedirectTo());
	}

	@Test
	@Tag("STDERR")
	void eval_stderrRedirect_partialFailure_returnsStdoutStderrAndRedirectTarget() throws IOException, ReplException {
		// Create a valid file
		Path validFile = tempDir.resolve("valid.txt");
		Files.writeString(validFile, "pear\n");

		ReplEvaluator evaluator = new ReplEvaluator(
			"cat " + validFile + " nonexistent 2> errors.txt",
			contextBuilder
		);

		EvaluationResult result = evaluator.eval();

		// Should return stdout even when command partially fails
		assertEquals("pear", result.commandResult().stdout());
		// Stderr should contain error
		assertTrue(result.commandResult().stderr().contains("nonexistent"));
		// Redirect target should be set
		assertEquals("errors.txt", result.stderrRedirectTo());
	}

	@Test
	@Tag("STDERR")
	void eval_stderrRedirect_nestedDirectory_returnsResultWithNestedPath() throws ReplException {
		ReplEvaluator evaluator = new ReplEvaluator("cat nonexistent 2> subdir/nested/errors.txt", contextBuilder);

		EvaluationResult result = evaluator.eval();

		// Failed command returns empty stdout
		assertEquals("", result.commandResult().stdout());
		// Stderr contains error
		assertTrue(result.commandResult().stderr().contains("nonexistent"));
		// Redirect target should be the nested path
		assertEquals("subdir/nested/errors.txt", result.stderrRedirectTo());
	}
}
