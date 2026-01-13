package repl.commands.builtin;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repl.ReplContext;
import repl.commands.CommandResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TypeCommandTest {

	@Mock
	private ReplContext mockContext;

	private final TypeCommand typeCommand = new TypeCommand();

	// === Stage #EZ5: Implement type ===

	@Test
	@Tag("EZ5")
	void execute_builtinEcho_returnsShellBuiltin() {
		when(mockContext.getArgs()).thenReturn(List.of("echo"));

		CommandResult result = typeCommand.execute(mockContext);

		assertEquals("echo is a shell builtin", result.stdout());
		assertTrue(result.isSuccess());
	}

	@Test
	@Tag("EZ5")
	void execute_builtinExit_returnsShellBuiltin() {
		when(mockContext.getArgs()).thenReturn(List.of("exit"));

		CommandResult result = typeCommand.execute(mockContext);

		assertEquals("exit is a shell builtin", result.stdout());
		assertTrue(result.isSuccess());
	}

	@Test
	@Tag("EZ5")
	void execute_builtinType_returnsShellBuiltin() {
		when(mockContext.getArgs()).thenReturn(List.of("type"));

		CommandResult result = typeCommand.execute(mockContext);

		assertEquals("type is a shell builtin", result.stdout());
		assertTrue(result.isSuccess());
	}

	@Test
	@Tag("EI0")
	void execute_builtinPwd_returnsShellBuiltin() {
		when(mockContext.getArgs()).thenReturn(List.of("pwd"));

		CommandResult result = typeCommand.execute(mockContext);

		assertEquals("pwd is a shell builtin", result.stdout());
		assertTrue(result.isSuccess());
	}

	@Test
	void execute_builtinCd_returnsShellBuiltin() {
		when(mockContext.getArgs()).thenReturn(List.of("cd"));

		CommandResult result = typeCommand.execute(mockContext);

		assertEquals("cd is a shell builtin", result.stdout());
		assertTrue(result.isSuccess());
	}

	@Test
	@Tag("EZ5")
	void execute_unknownCommand_returnsNotFound() {
		when(mockContext.getArgs()).thenReturn(List.of("unknowncmd123"));

		CommandResult result = typeCommand.execute(mockContext);

		assertEquals("unknowncmd123: not found", result.stdout());
		assertTrue(result.isSuccess());
	}

	// === Stage #MG5: Locate executable files ===

	@Test
	@Tag("MG5")
	void execute_existingExecutable_returnsPath() {
		// 'ls' should exist on most Unix systems
		when(mockContext.getArgs()).thenReturn(List.of("ls"));

		CommandResult result = typeCommand.execute(mockContext);

		assertTrue(result.stdout().startsWith("ls is "));
		assertTrue(result.stdout().contains("/ls"));
		assertTrue(result.isSuccess());
	}

	// === Input validation ===

	@Test
	void execute_noArgs_returnsErrorWithMissingOperand() {
		when(mockContext.getArgs()).thenReturn(List.of());

		CommandResult result = typeCommand.execute(mockContext);

		assertEquals("type: missing operand", result.stderr());
		assertFalse(result.isSuccess());
	}
}
