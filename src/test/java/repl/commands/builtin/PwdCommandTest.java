package repl.commands.builtin;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repl.ReplContext;
import repl.commands.CommandResult;
import repl.utils.DirUtils;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PwdCommandTest {

	@Mock
	private ReplContext mockContext;

	@Mock
	private DirUtils mockDirUtils;

	@TempDir
	Path tempDir;

	private final PwdCommand pwdCommand = new PwdCommand();

	// === Stage #EI0: Navigation - The pwd builtin ===

	@Test
	@Tag("EI0")
	void execute_returnsCurrentDirectoryPath() throws IOException {
		Path realPath = tempDir.toRealPath();
		when(mockContext.getDirUtils()).thenReturn(mockDirUtils);
		when(mockDirUtils.getCurrentDir()).thenReturn(realPath);

		CommandResult result = pwdCommand.execute(mockContext);

		assertEquals(realPath.toAbsolutePath().toString(), result.stdout());
		assertTrue(result.isSuccess());
	}

	@Test
	@Tag("EI0")
	void execute_withDifferentDirectory_returnsCorrectPath() {
		Path customPath = Path.of("/usr/local");
		when(mockContext.getDirUtils()).thenReturn(mockDirUtils);
		when(mockDirUtils.getCurrentDir()).thenReturn(customPath);

		CommandResult result = pwdCommand.execute(mockContext);

		assertEquals("/usr/local", result.stdout());
		assertTrue(result.isSuccess());
	}
}
