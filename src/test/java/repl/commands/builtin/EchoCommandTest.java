package repl.commands.builtin;

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
class EchoCommandTest {

	@Mock
	private ReplContext mockContext;

	private final EchoCommand echoCommand = new EchoCommand();

	@Test
	void execute_singleArg_returnsArg() throws ReplException {
		when(mockContext.getArgs()).thenReturn(List.of("hello"));

		String result = echoCommand.execute(mockContext);

		assertEquals("hello", result);
	}

	@Test
	void execute_multipleArgs_returnsArgsJoinedBySpaces() throws ReplException {
		when(mockContext.getArgs()).thenReturn(List.of("hello", "world"));

		String result = echoCommand.execute(mockContext);

		assertEquals("hello world", result);
	}

	@Test
	void execute_noArgs_returnsEmptyString() throws ReplException {
		when(mockContext.getArgs()).thenReturn(List.of());

		String result = echoCommand.execute(mockContext);

		assertEquals("", result);
	}

	@Test
	void execute_manyArgs_joinsAll() throws ReplException {
		when(mockContext.getArgs()).thenReturn(List.of("a", "b", "c", "d", "e"));

		String result = echoCommand.execute(mockContext);

		assertEquals("a b c d e", result);
	}
}
