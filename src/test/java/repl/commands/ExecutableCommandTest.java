package repl.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repl.ReplContext;
import repl.exceptions.ReplException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExecutableCommandTest {

	@Mock
	private ReplContext mockContext;

	private final ExecutableCommand executableCommand = new ExecutableCommand();

	@Test
	void execute_echoCommand_capturesOutput() throws ReplException {
		when(mockContext.getMainCommandStr()).thenReturn("echo");
		when(mockContext.getArgs()).thenReturn(List.of("hello", "world"));

		String result = executableCommand.execute(mockContext);

		assertEquals("hello world", result);
	}

	@Test
	void execute_pwdCommand_returnsDirectory() throws ReplException {
		when(mockContext.getMainCommandStr()).thenReturn("pwd");
		when(mockContext.getArgs()).thenReturn(List.of());

		String result = executableCommand.execute(mockContext);

		assertNotNull(result);
		assertFalse(result.isEmpty());
	}

	@Test
	void execute_nonExistentCommand_returnsErrorMessage() throws ReplException {
		when(mockContext.getMainCommandStr()).thenReturn("nonexistent_command_xyz");
		when(mockContext.getArgs()).thenReturn(List.of());

		String result = executableCommand.execute(mockContext);

		assertTrue(result.contains("nonexistent_command_xyz"));
		assertTrue(result.contains("execution failed"));
	}

	@Test
	void execute_commandWithNoArgs_works() throws ReplException {
		when(mockContext.getMainCommandStr()).thenReturn("date");
		when(mockContext.getArgs()).thenReturn(List.of());

		String result = executableCommand.execute(mockContext);

		assertNotNull(result);
	}
}
