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
class EchoCommandTest {

	@Mock
	private ReplContext mockContext;

	private final EchoCommand echoCommand = new EchoCommand();

	// === Stage #IZ3: Implement echo ===

	@Test
	@Tag("IZ3")
	void execute_singleArg_returnsArg() {
		when(mockContext.getArgs()).thenReturn(List.of("hello"));

		CommandResult result = echoCommand.execute(mockContext);

		assertEquals("hello", result.stdout());
		assertTrue(result.isSuccess());
	}

	@Test
	@Tag("IZ3")
	void execute_multipleArgs_returnsArgsJoinedBySpaces() {
		when(mockContext.getArgs()).thenReturn(List.of("hello", "world"));

		CommandResult result = echoCommand.execute(mockContext);

		assertEquals("hello world", result.stdout());
		assertTrue(result.isSuccess());
	}

	@Test
	@Tag("IZ3")
	void execute_noArgs_returnsEmptyString() {
		when(mockContext.getArgs()).thenReturn(List.of());

		CommandResult result = echoCommand.execute(mockContext);

		assertEquals("", result.stdout());
		assertTrue(result.isSuccess());
	}

	@Test
	@Tag("IZ3")
	void execute_manyArgs_joinsAll() {
		when(mockContext.getArgs()).thenReturn(List.of("a", "b", "c", "d", "e"));

		CommandResult result = echoCommand.execute(mockContext);

		assertEquals("a b c d e", result.stdout());
		assertTrue(result.isSuccess());
	}
}
