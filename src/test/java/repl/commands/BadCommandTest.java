package repl.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repl.ReplContext;
import repl.exceptions.ReplException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BadCommandTest {

	@Mock
	private ReplContext mockContext;

	private final BadCommand badCommand = new BadCommand();

	@Test
	void execute_returnsCommandNotFoundMessage() throws ReplException {
		when(mockContext.getMainCommandStr()).thenReturn("unknowncmd");

		String result = badCommand.execute(mockContext);

		assertEquals("unknowncmd: command not found", result);
	}

	@Test
	void execute_withDifferentCommand_includesCommandName() throws ReplException {
		when(mockContext.getMainCommandStr()).thenReturn("foobar");

		String result = badCommand.execute(mockContext);

		assertEquals("foobar: command not found", result);
	}

	@Test
	void commandNotFound_constant_hasCorrectValue() {
		assertEquals("command not found", BadCommand.COMMAND_NOT_FOUND);
	}
}
