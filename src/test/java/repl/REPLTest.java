package repl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import repl.commands.CommandResult;
import repl.exceptions.ReplException;
import repl.utils.DirUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for REPL I/O handling (stdout/stderr redirection).
 *
 * <p>Tests the actual file I/O that happens when REPL.handleIO() processes
 * EvaluationResult objects with redirection targets.
 */
class REPLTest {

	@TempDir
	Path tempDir;

	private REPL repl;

	@BeforeEach
	void setUp() {
		DirUtils dirUtils = new DirUtils(tempDir);
		repl = new REPL(dirUtils);
	}

	// === Stdout redirection tests ===

	@Test
	@Tag("JV1")
	void handleIO_stdoutRedirect_writesOutputToFile() throws ReplException, IOException {
		CommandResult cmdResult = CommandResult.success("hello world");
		EvaluationResult result = new EvaluationResult(cmdResult, "output.txt", null);

		repl.handleIO(result);

		// Verify file was created and contains the output
		Path outputFile = tempDir.resolve("output.txt");
		assertTrue(Files.exists(outputFile), "Output file should exist");
		String fileContent = Files.readString(outputFile);
		assertEquals("hello world", fileContent);
	}

	@Test
	@Tag("JV1")
	void handleIO_stdoutRedirect_nestedDirectory_createsParentDirs() throws ReplException, IOException {
		CommandResult cmdResult = CommandResult.success("nested output");
		EvaluationResult result = new EvaluationResult(cmdResult, "subdir/nested/file.txt", null);

		repl.handleIO(result);

		// Verify nested directories were created
		Path outputFile = tempDir.resolve("subdir/nested/file.txt");
		assertTrue(Files.exists(outputFile), "Nested output file should exist");
		assertTrue(Files.isDirectory(outputFile.getParent()), "Parent directory should exist");

		String fileContent = Files.readString(outputFile);
		assertEquals("nested output", fileContent);
	}

	@Test
	@Tag("JV1")
	void handleIO_stdoutRedirect_overwritesExistingFile() throws ReplException, IOException {
		// Create a file with initial content
		Path outputFile = tempDir.resolve("overwrite.txt");
		Files.writeString(outputFile, "initial content");

		// Redirect to the same file
		CommandResult cmdResult = CommandResult.success("new content");
		EvaluationResult result = new EvaluationResult(cmdResult, "overwrite.txt", null);

		repl.handleIO(result);

		// Verify the file was overwritten
		String fileContent = Files.readString(outputFile);
		assertEquals("new content", fileContent);
	}

	@Test
	@Tag("JV1")
	void handleIO_stdoutRedirect_emptyOutput_createsEmptyFile() throws ReplException, IOException {
		CommandResult cmdResult = CommandResult.success("");
		EvaluationResult result = new EvaluationResult(cmdResult, "empty.txt", null);

		repl.handleIO(result);

		Path outputFile = tempDir.resolve("empty.txt");
		assertTrue(Files.exists(outputFile));
		String fileContent = Files.readString(outputFile);
		assertEquals("", fileContent);
	}

	@Test
	@Tag("JV1")
	void handleIO_stdoutRedirect_commandFails_stillWritesStdout() throws ReplException, IOException {
		// Failed command (exit code 1) but produced stdout
		CommandResult cmdResult = new CommandResult("partial output", "some error", 1);
		EvaluationResult result = new EvaluationResult(cmdResult, "output.txt", null);

		repl.handleIO(result);

		// Stdout should still be redirected even though command failed
		Path outputFile = tempDir.resolve("output.txt");
		assertTrue(Files.exists(outputFile), "Output file should exist even though command failed");
		String fileContent = Files.readString(outputFile);
		assertEquals("partial output", fileContent);
	}

	// === Stderr redirection tests ===

	@Test
	@Tag("STDERR")
	void handleIO_stderrRedirect_writesErrorToFile() throws ReplException, IOException {
		CommandResult cmdResult = new CommandResult("", "cat: nonexistent: No such file or directory", 1);
		EvaluationResult result = new EvaluationResult(cmdResult, null, "errors.txt");

		repl.handleIO(result);

		// Verify error file was created and contains stderr
		Path errorFile = tempDir.resolve("errors.txt");
		assertTrue(Files.exists(errorFile), "Error file should exist");
		String fileContent = Files.readString(errorFile);
		assertEquals("cat: nonexistent: No such file or directory", fileContent);
	}

	@Test
	@Tag("STDERR")
	void handleIO_stderrRedirect_successfulCommand_createsEmptyFile() throws ReplException, IOException {
		// Successful command with no stderr
		CommandResult cmdResult = CommandResult.success("hello");
		EvaluationResult result = new EvaluationResult(cmdResult, null, "errors.txt");

		repl.handleIO(result);

		// Stderr file should be created but empty
		Path errorFile = tempDir.resolve("errors.txt");
		assertTrue(Files.exists(errorFile), "Error file should exist");
		String fileContent = Files.readString(errorFile);
		assertTrue(fileContent.isEmpty(), "Error file should be empty for successful command");
	}

	@Test
	@Tag("STDERR")
	void handleIO_stderrRedirect_nestedDirectory_createsParentDirs() throws ReplException, IOException {
		CommandResult cmdResult = new CommandResult("", "error message", 1);
		EvaluationResult result = new EvaluationResult(cmdResult, null, "subdir/nested/errors.txt");

		repl.handleIO(result);

		Path errorFile = tempDir.resolve("subdir/nested/errors.txt");
		assertTrue(Files.exists(errorFile), "Nested error file should exist");
		assertTrue(Files.isDirectory(errorFile.getParent()), "Parent directory should exist");

		String fileContent = Files.readString(errorFile);
		assertEquals("error message", fileContent);
	}

	@Test
	@Tag("STDERR")
	void handleIO_stderrRedirect_partialFailure_bothStdoutAndStderr() throws ReplException, IOException {
		// Command that outputs to both stdout and stderr
		CommandResult cmdResult = new CommandResult("valid output", "error: file not found", 1);
		EvaluationResult result = new EvaluationResult(cmdResult, null, "errors.txt");

		repl.handleIO(result);

		// Stderr should be redirected to file
		Path errorFile = tempDir.resolve("errors.txt");
		assertTrue(Files.exists(errorFile), "Error file should exist");
		String fileContent = Files.readString(errorFile);
		assertEquals("error: file not found", fileContent);

		// Note: stdout would go to System.out (terminal), not easily testable
	}

	// === Combined redirection (future feature placeholder) ===

	@Test
	void handleIO_noRedirect_doesNotCreateFiles() throws ReplException {
		CommandResult cmdResult = CommandResult.success("output to terminal");
		EvaluationResult result = new EvaluationResult(cmdResult, null, null);

		repl.handleIO(result);

		// No files should be created
		assertEquals(0, tempDir.toFile().listFiles().length, "No files should be created without redirection");
	}

	@Test
	@Tag("JV1")
	@Tag("STDERR")
	void handleIO_bothRedirects_writesToBothFiles() throws ReplException, IOException {
		// This would require supporting both redirects simultaneously
		// For now, parser rejects this, but test documents expected behavior
		CommandResult cmdResult = new CommandResult("stdout content", "stderr content", 0);
		EvaluationResult result = new EvaluationResult(cmdResult, "out.txt", "err.txt");

		repl.handleIO(result);

		// Both files should be created
		Path outFile = tempDir.resolve("out.txt");
		Path errFile = tempDir.resolve("err.txt");

		assertTrue(Files.exists(outFile), "Stdout file should exist");
		assertTrue(Files.exists(errFile), "Stderr file should exist");

		assertEquals("stdout content", Files.readString(outFile));
		assertEquals("stderr content", Files.readString(errFile));
	}
}
