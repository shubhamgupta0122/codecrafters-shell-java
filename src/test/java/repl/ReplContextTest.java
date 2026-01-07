package repl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repl.utils.DirUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReplContextTest {

	@Mock
	private DirUtils mockDirUtils;

	private ReplContext.Builder builder;

	@BeforeEach
	void setUp() {
		builder = ReplContext.builder(mockDirUtils);
	}

	@Test
	void builder_createWithDirUtils_returnsBuilder() {
		assertNotNull(builder);
	}

	@Test
	void build_withAllFields_createsContext() {
		ReplContext context = builder
				.originalInput("echo hello")
				.mainCommandStr("echo")
				.args(List.of("hello"))
				.build();

		assertNotNull(context);
	}

	@Test
	void getDirUtils_returnsInjectedInstance() {
		ReplContext context = builder
				.originalInput("test")
				.mainCommandStr("test")
				.args(List.of())
				.build();

		assertSame(mockDirUtils, context.getDirUtils());
	}

	@Test
	void getOriginalInput_returnsSetValue() {
		ReplContext context = builder
				.originalInput("echo hello world")
				.mainCommandStr("echo")
				.args(List.of("hello", "world"))
				.build();

		assertEquals("echo hello world", context.getOriginalInput());
	}

	@Test
	void getMainCommandStr_returnsSetValue() {
		ReplContext context = builder
				.originalInput("pwd")
				.mainCommandStr("pwd")
				.args(List.of())
				.build();

		assertEquals("pwd", context.getMainCommandStr());
	}

	@Test
	void getArgs_returnsSetValue() {
		List<String> args = List.of("arg1", "arg2", "arg3");
		ReplContext context = builder
				.originalInput("cmd arg1 arg2 arg3")
				.mainCommandStr("cmd")
				.args(args)
				.build();

		assertEquals(args, context.getArgs());
	}

	@Test
	void getArgs_emptyList_returnsEmptyList() {
		ReplContext context = builder
				.originalInput("pwd")
				.mainCommandStr("pwd")
				.args(List.of())
				.build();

		assertTrue(context.getArgs().isEmpty());
	}

	@Test
	void builder_canBeReusedForMultipleContexts() {
		ReplContext context1 = builder
				.originalInput("echo first")
				.mainCommandStr("echo")
				.args(List.of("first"))
				.build();

		ReplContext context2 = builder
				.originalInput("echo second")
				.mainCommandStr("echo")
				.args(List.of("second"))
				.build();

		assertEquals("echo first", context1.getOriginalInput());
		assertEquals("echo second", context2.getOriginalInput());
		// Both share the same DirUtils
		assertSame(context1.getDirUtils(), context2.getDirUtils());
	}

	@Test
	void builder_fluentApi_allowsChaining() {
		ReplContext context = ReplContext.builder(mockDirUtils)
				.originalInput("test")
				.mainCommandStr("test")
				.args(List.of())
				.build();

		assertNotNull(context);
	}
}
