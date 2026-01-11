package repl.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReplExceptionTest {

	@Test
	void constructor_withCause_setsCause() {
		Throwable cause = new RuntimeException("test cause");

		ReplException exception = new ReplException(cause);

		assertSame(cause, exception.getCause());
	}

	@Test
	void constructor_withNullCause_allowsNull() {
		ReplException exception = new ReplException((String)null);

		assertNull(exception.getCause());
	}
}
