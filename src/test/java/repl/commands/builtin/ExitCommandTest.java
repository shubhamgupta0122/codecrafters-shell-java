package repl.commands.builtin;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repl.ReplContext;
import repl.exceptions.GracefulExitException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExitCommandTest {

	@Mock
	private ReplContext mockContext;

	private final ExitCommand exitCommand = new ExitCommand();

	@Test
	void execute_throwsGracefulExitException() {
		assertThrows(GracefulExitException.class, () ->
				exitCommand.execute(mockContext));
	}
}
