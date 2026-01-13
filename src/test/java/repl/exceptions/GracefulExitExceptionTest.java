package repl.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GracefulExitExceptionTest {

	@Test
	void constructor_createsException() {
		GracefulExitException exception = new GracefulExitException();

		assertNotNull(exception);
	}

	@Test
	void extendsReplException() {
		GracefulExitException exception = new GracefulExitException();

		assertInstanceOf(ReplException.class, exception);
	}

	@Test
	void getCause_returnsNull() {
		GracefulExitException exception = new GracefulExitException();

		assertNull(exception.getCause());
	}
}
