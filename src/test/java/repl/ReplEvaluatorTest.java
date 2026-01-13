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

		String result = evaluator.eval();

		assertEquals("hello", result);
	}

	@Test
	void eval_echoMultipleArgs_returnsJoinedText() throws ReplException {
		ReplEvaluator evaluator = new ReplEvaluator("echo hello world", contextBuilder);

		String result = evaluator.eval();

		assertEquals("hello world", result);
	}

	@Test
	void eval_exitCommand_throwsGracefulExitException() {
		ReplEvaluator evaluator = new ReplEvaluator("exit 0", contextBuilder);

		assertThrows(GracefulExitException.class, evaluator::eval);
	}

	@Test
	void eval_typeBuiltin_returnsShellBuiltin() throws ReplException {
		ReplEvaluator evaluator = new ReplEvaluator("type echo", contextBuilder);

		String result = evaluator.eval();

		assertEquals("echo is a shell builtin", result);
	}

	@Test
	void eval_typeUnknown_returnsNotFound() throws ReplException {
		ReplEvaluator evaluator = new ReplEvaluator("type unknowncmd123", contextBuilder);

		String result = evaluator.eval();

		assertEquals("unknowncmd123: not found", result);
	}

	@Test
	void eval_pwdCommand_returnsCurrentDirectory() throws ReplException, IOException {
		ReplEvaluator evaluator = new ReplEvaluator("pwd", contextBuilder);

		String result = evaluator.eval();

		// DirUtils stores the path passed to constructor, not the real path
		// So we compare with tempDir directly (without toRealPath)
		assertEquals(tempDir.toAbsolutePath().toString(), result);
	}

	@Test
	void eval_cdCommand_changesDirectory() throws ReplException, IOException {
		Path subDir = Files.createDirectory(tempDir.resolve("testdir"));
		ReplEvaluator cdEvaluator = new ReplEvaluator("cd testdir", contextBuilder);

		String cdResult = cdEvaluator.eval();

		assertNull(cdResult);

		// Verify by running pwd
		ReplEvaluator pwdEvaluator = new ReplEvaluator("pwd", contextBuilder);
		String pwdResult = pwdEvaluator.eval();

		assertEquals(subDir.toRealPath().toAbsolutePath().toString(), pwdResult);
	}

	@Test
	@Tag("FF0")
	@Tag("CZ2")
	void eval_unknownCommand_throwsReplExceptionWithNotFound() {
		ReplEvaluator evaluator = new ReplEvaluator("unknowncmd123", contextBuilder);

		ReplException exception = assertThrows(
			ReplException.class,
			() -> evaluator.eval()
		);

		assertEquals("unknowncmd123: command not found", exception.getMessage());
	}

	@Test
	void eval_externalCommand_executesAndReturnsOutput() throws ReplException {
		ReplEvaluator evaluator = new ReplEvaluator("echo external", contextBuilder);

		String result = evaluator.eval();

		assertEquals("external", result);
	}

	// === Stdout redirection tests ===

	@Test
	@Tag("JV1")
	void eval_redirectWithGreaterThan_writesOutputToFile() throws ReplException, IOException {
		ReplEvaluator evaluator = new ReplEvaluator("echo hello world > output.txt", contextBuilder);

		String result = evaluator.eval();

		// When redirecting, eval should return null
		assertNull(result);

		// Verify file was created and contains the output
		Path outputFile = tempDir.resolve("output.txt");
		assertTrue(Files.exists(outputFile), "Output file should exist");
		String fileContent = Files.readString(outputFile);
		assertEquals("hello world", fileContent);
	}

	@Test
	@Tag("JV1")
	void eval_redirectWith1GreaterThan_writesOutputToFile() throws ReplException, IOException {
		ReplEvaluator evaluator = new ReplEvaluator("echo test content 1> output2.txt", contextBuilder);

		String result = evaluator.eval();

		assertNull(result);

		Path outputFile = tempDir.resolve("output2.txt");
		assertTrue(Files.exists(outputFile));
		String fileContent = Files.readString(outputFile);
		assertEquals("test content", fileContent);
	}

	@Test
	@Tag("JV1")
	void eval_redirectToNestedDirectory_createsDirectoriesAndFile() throws ReplException, IOException {
		ReplEvaluator evaluator = new ReplEvaluator("echo nested output > subdir/nested/file.txt", contextBuilder);

		String result = evaluator.eval();

		assertNull(result);

		// Verify nested directories were created
		Path outputFile = tempDir.resolve("subdir/nested/file.txt");
		assertTrue(Files.exists(outputFile), "Nested output file should exist");
		assertTrue(Files.isDirectory(outputFile.getParent()), "Parent directory should exist");

		String fileContent = Files.readString(outputFile);
		assertEquals("nested output", fileContent);
	}

	@Test
	@Tag("JV1")
	void eval_redirectBuiltinCommand_writesOutputToFile() throws ReplException, IOException {
		ReplEvaluator evaluator = new ReplEvaluator("pwd > pwd_output.txt", contextBuilder);

		String result = evaluator.eval();

		assertNull(result);

		Path outputFile = tempDir.resolve("pwd_output.txt");
		assertTrue(Files.exists(outputFile));
		String fileContent = Files.readString(outputFile);
		assertEquals(tempDir.toAbsolutePath().toString(), fileContent);
	}

	@Test
	@Tag("JV1")
	void eval_redirectOverwritesExistingFile() throws ReplException, IOException {
		// Create a file with initial content
		Path outputFile = tempDir.resolve("overwrite.txt");
		Files.writeString(outputFile, "initial content");

		// Redirect to the same file
		ReplEvaluator evaluator = new ReplEvaluator("echo new content > overwrite.txt", contextBuilder);
		String result = evaluator.eval();

		assertNull(result);

		// Verify the file was overwritten
		String fileContent = Files.readString(outputFile);
		assertEquals("new content", fileContent);
	}

	@Test
	@Tag("JV1")
	void eval_redirectEmptyOutput_createsEmptyFile() throws ReplException, IOException {
		ReplEvaluator evaluator = new ReplEvaluator("echo '' > empty.txt", contextBuilder);

		String result = evaluator.eval();

		assertNull(result);

		Path outputFile = tempDir.resolve("empty.txt");
		assertTrue(Files.exists(outputFile));
		String fileContent = Files.readString(outputFile);
		assertEquals("", fileContent);
	}

	@Test
	@Tag("JV1")
	void eval_commandFailsButProducesStdout_redirectsStdoutAndThrows() throws IOException {
		// Create a file to read successfully
		Path validFile = tempDir.resolve("valid.txt");
		Files.writeString(validFile, "valid content\n");

		// cat valid.txt nonexistent > output.txt
		// Should redirect "valid content" to file even though command fails
		ReplEvaluator evaluator = new ReplEvaluator(
			"cat " + validFile + " nonexistent > output.txt",
			contextBuilder
		);

		// Command should throw exception due to nonexistent file
		ReplException exception = assertThrows(
			ReplException.class,
			() -> evaluator.eval()
		);

		// Error message should be about the nonexistent file
		assertTrue(exception.getMessage().contains("nonexistent") ||
				   exception.getMessage().contains("No such file"),
			"Exception message should mention the error: " + exception.getMessage());

		// But stdout should still be redirected to the file
		Path outputFile = tempDir.resolve("output.txt");
		assertTrue(Files.exists(outputFile), "Output file should exist even though command failed");
		String fileContent = Files.readString(outputFile);
		assertEquals("valid content", fileContent,
			"Stdout should be redirected even when command fails");
	}
}
