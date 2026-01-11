package repl.commands;

import org.junit.jupiter.api.Tag;
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

	// === Stage #CZ2: Handle invalid commands ===

	@Test
	@Tag("CZ2")
	void execute_throwsReplExceptionWithCommandNotFoundMessage() {
		when(mockContext.getMainCommandStr()).thenReturn("unknowncmd");

		ReplException exception = assertThrows(
			ReplException.class,
			() -> badCommand.execute(mockContext)
		);

		assertEquals("unknowncmd: command not found", exception.getMessage());
	}

	@Test
	@Tag("CZ2")
	void execute_withDifferentCommand_throwsExceptionIncludingCommandName() {
		when(mockContext.getMainCommandStr()).thenReturn("foobar");

		ReplException exception = assertThrows(
			ReplException.class,
			() -> badCommand.execute(mockContext)
		);

		assertEquals("foobar: command not found", exception.getMessage());
	}

	@Test
	void commandNotFound_constant_hasCorrectValue() {
		assertEquals("command not found", BadCommand.COMMAND_NOT_FOUND);
	}
}
