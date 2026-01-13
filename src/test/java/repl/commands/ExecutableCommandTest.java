package repl.commands;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repl.ReplContext;
import repl.commands.CommandResult;
import repl.exceptions.ReplException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExecutableCommandTest {

	@Mock
	private ReplContext mockContext;

	private final ExecutableCommand executableCommand = new ExecutableCommand();

	// === Stage #IP1: Run a program ===

	@Test
	@Tag("IP1")
	void execute_echoCommand_capturesOutput() throws ReplException {
		when(mockContext.getMainCommandStr()).thenReturn("echo");
		when(mockContext.getArgs()).thenReturn(List.of("hello", "world"));

		CommandResult result = executableCommand.execute(mockContext);

		assertEquals("hello world", result.stdout());
		assertTrue(result.isSuccess());
	}

	@Test
	@Tag("IP1")
	void execute_pwdCommand_returnsDirectory() throws ReplException {
		when(mockContext.getMainCommandStr()).thenReturn("pwd");
		when(mockContext.getArgs()).thenReturn(List.of());

		CommandResult result = executableCommand.execute(mockContext);

		assertNotNull(result.stdout());
		assertFalse(result.stdout().isEmpty());
		assertTrue(result.isSuccess());
	}

	@Test
	@Tag("IP1")
	void execute_commandWithNoArgs_works() throws ReplException {
		when(mockContext.getMainCommandStr()).thenReturn("date");
		when(mockContext.getArgs()).thenReturn(List.of());

		CommandResult result = executableCommand.execute(mockContext);

		assertNotNull(result.stdout());
		assertTrue(result.isSuccess());
	}

	// === Error handling ===

	@Test
	void execute_nonExistentCommand_throwsReplException() {
		when(mockContext.getMainCommandStr()).thenReturn("nonexistent_command_xyz");
		when(mockContext.getArgs()).thenReturn(List.of());

		ReplException exception = assertThrows(
			ReplException.class,
			() -> executableCommand.execute(mockContext)
		);

		String message = exception.getMessage();
		assertTrue(message.contains("nonexistent_command_xyz"));
		assertTrue(message.contains("execution failed") || message.contains("Cannot run program"));
	}

	// === Argv[0] behavior ===

	@Test
	@Tag("IP1")
	void execute_usesCommandNameNotFullPath_forArgv0() throws ReplException {
		// Verify that argv[0] is the command name, not the full path
		// Use 'sh -c' to print $0 which shows argv[0] of the sh command
		when(mockContext.getMainCommandStr()).thenReturn("sh");
		when(mockContext.getArgs()).thenReturn(List.of("-c", "basename $0"));

		CommandResult result = executableCommand.execute(mockContext);

		// Should print "sh" (command name) not "/bin/sh" or "/usr/bin/sh" (full path)
		assertEquals("sh", result.stdout());
		assertTrue(result.isSuccess());
	}
}
