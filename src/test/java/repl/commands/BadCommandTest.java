package repl.commands;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repl.Messages;
import repl.ReplContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BadCommandTest {

	@Mock
	private ReplContext mockContext;

	private final BadCommand badCommand = new BadCommand();

	// === Stage #CZ2: Handle invalid commands ===

	@Test
	@Tag("CZ2")
	void execute_returnsErrorWithCommandNotFoundMessage() {
		when(mockContext.getMainCommandStr()).thenReturn("unknowncmd");

		CommandResult result = badCommand.execute(mockContext);

		assertFalse(result.isSuccess());
		assertEquals("unknowncmd: command not found", result.stderr());
	}

	@Test
	@Tag("CZ2")
	void execute_withDifferentCommand_returnsErrorIncludingCommandName() {
		when(mockContext.getMainCommandStr()).thenReturn("foobar");

		CommandResult result = badCommand.execute(mockContext);

		assertFalse(result.isSuccess());
		assertEquals("foobar: command not found", result.stderr());
	}

	@Test
	void commandNotFound_constant_hasCorrectValue() {
		assertEquals("command not found", Messages.COMMAND_NOT_FOUND);
	}
}
