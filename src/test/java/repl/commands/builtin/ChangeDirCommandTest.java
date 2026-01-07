package repl.commands.builtin;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repl.ReplContext;
import repl.exceptions.ReplException;
import repl.utils.DirUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeDirCommandTest {

	@Mock
	private ReplContext mockContext;

	@Mock
	private DirUtils mockDirUtils;

	@TempDir
	Path tempDir;

	private final ChangeDirCommand cdCommand = new ChangeDirCommand();

	@Test
	void execute_withValidPath_returnsNull() throws ReplException, IOException {
		Path subDir = Files.createDirectory(tempDir.resolve("subdir"));
		when(mockContext.getArgs()).thenReturn(List.of(subDir.toString()));
		when(mockContext.getDirUtils()).thenReturn(mockDirUtils);

		String result = cdCommand.execute(mockContext);

		assertNull(result);
		verify(mockDirUtils).setCurrentDir(subDir.toString());
	}

	@Test
	void execute_noArgs_changesToHomeDir() throws ReplException, IOException {
		when(mockContext.getArgs()).thenReturn(List.of());
		when(mockContext.getDirUtils()).thenReturn(mockDirUtils);

		String result = cdCommand.execute(mockContext);

		assertNull(result);
		verify(mockDirUtils).setCurrentDir("~");
	}

	@Test
	void execute_nonExistentPath_returnsErrorMessage() throws ReplException, IOException {
		when(mockContext.getArgs()).thenReturn(List.of("nonexistent"));
		when(mockContext.getDirUtils()).thenReturn(mockDirUtils);
		doThrow(new NoSuchFileException("nonexistent")).when(mockDirUtils).setCurrentDir("nonexistent");

		String result = cdCommand.execute(mockContext);

		assertEquals("cd: nonexistent: No such file or directory", result);
	}

	@Test
	void execute_withIOException_throwsRuntimeException() throws IOException {
		when(mockContext.getArgs()).thenReturn(List.of("somepath"));
		when(mockContext.getDirUtils()).thenReturn(mockDirUtils);
		doThrow(new IOException("IO error")).when(mockDirUtils).setCurrentDir("somepath");

		assertThrows(RuntimeException.class, () -> cdCommand.execute(mockContext));
	}

	@Test
	void execute_integrationTest_actuallyChangesDirectory() throws ReplException, IOException {
		Path subDir = Files.createDirectory(tempDir.resolve("testdir"));
		DirUtils realDirUtils = new DirUtils(tempDir);

		when(mockContext.getArgs()).thenReturn(List.of("testdir"));
		when(mockContext.getDirUtils()).thenReturn(realDirUtils);

		String result = cdCommand.execute(mockContext);

		assertNull(result);
		assertEquals(subDir.toRealPath(), realDirUtils.getCurrentDir());
	}
}
