package repl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import repl.utils.DirUtils;
import repl.utils.CommandExtractorUtils;

import java.util.List;

/**
 * Context object containing shared state and per-request data for command execution.
 *
 * <p>Contains two categories of data:
 * <ul>
 *   <li><b>Shared services</b> (injected once): DirUtils, and future services like
 *       environment variables, command history, etc.</li>
 *   <li><b>Per-request data</b> (derived per command): original input, command name, arguments</li>
 * </ul>
 *
 * <p>Use {@link Builder} to construct instances. The builder is created with shared
 * services, then {@code originalInput} is set for each command. The command name and
 * arguments are automatically parsed from the input during {@link Builder#build()}.
 *
 * <p>Example usage:
 * <pre>{@code
 * // Create builder once with shared services
 * ReplContext.Builder ctxBuilder = ReplContext.builder(dirUtils);
 *
 * // For each command, build context with original input
 * // (command name and args are parsed automatically)
 * ReplContext ctx = ctxBuilder
 *     .originalInput(input)
 *     .build();
 * }</pre>
 */
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReplContext {
	// === Shared services (session-scoped) ===

	/** The directory utilities instance for managing working directory. */
	@Getter
	DirUtils dirUtils;

	// === Per-request data (command-scoped) ===

	/** The complete original input string from the user. */
	@Getter
	String originalInput;

	/** The main command name extracted from input. */
	@Getter
	String mainCommandStr;

	/** The list of arguments passed to the command. */
	@Getter
	List<String> args;

	@Getter
	String stdoutRedirectTo;

	/**
	 * Private constructor - use {@link Builder} to create instances.
	 */
	private ReplContext(Builder builder) {
		this.dirUtils = builder.dirUtils;
		this.originalInput = builder.originalInput;
		this.mainCommandStr = builder.mainCommandStr;
		this.args = builder.args;
		this.stdoutRedirectTo = builder.stdoutRedirectTo;
	}

	/**
	 * Creates a new builder with the given shared services.
	 *
	 * @param dirUtils the directory utilities instance (shared across commands)
	 * @return a new builder instance
	 */
	public static Builder builder(DirUtils dirUtils) {
		return new Builder(dirUtils);
	}

	/**
	 * Builder for creating ReplContext instances.
	 *
	 * <p>Initialized with shared services, then per-request data is set
	 * via fluent methods before calling {@link #build()}.
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@FieldDefaults(level = AccessLevel.PRIVATE)
	public static class Builder {
		// Shared services (set once in constructor)
		final DirUtils dirUtils;

		// Per-request data (set via builder methods)
		String originalInput;
		String mainCommandStr;
		List<String> args;
		String stdoutRedirectTo;

		/**
		 * Sets the original input string.
		 *
		 * @param originalInput the complete user input
		 * @return this builder for chaining
		 */
		public Builder originalInput(String originalInput) {
			this.originalInput = originalInput;
			return this;
		}

		/**
		 * Builds the ReplContext instance.
		 *
		 * @return a new immutable ReplContext
		 */
		public ReplContext build() {
			CommandExtractorUtils.ExtractedCommand extractedCommand = CommandExtractorUtils.get(originalInput);
			mainCommandStr = extractedCommand.mainCommandStr();
			args = extractedCommand.args();
			stdoutRedirectTo = extractedCommand.stdoutRedirectTo();
			return new ReplContext(this);
		}
	}
}
