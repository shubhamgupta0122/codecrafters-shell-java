# Architecture Documentation

## Overview

This is a Java-based POSIX-compliant shell implementation built as part of the CodeCrafters Shell Challenge. The codebase is organized into a clean, layered architecture with proper separation of concerns.

**Tech Stack:**
- Java 25 (with preview features enabled)
- Maven for build management
- JUnit 5 & Mockito for testing
- No external runtime dependencies

---

## Project Structure

```text
src/main/java/
├── Main.java                          # Entry point - unnamed main method
└── repl/
    ├── REPL.java                      # Main REPL loop orchestrator
    ├── ReplContext.java               # Context/dependency injection
    ├── ReplEvaluator.java             # Command resolution & evaluation
    ├── EvaluationResult.java          # Wrapper for command result + redirect targets
    ├── BuiltinCommand.java            # Builtin command registry
    ├── Constants.java                 # Shared constants
    ├── Messages.java                  # Centralized error messages
    ├── commands/
    │   ├── Command.java               # Core interface
    │   ├── CommandResult.java         # Command output (stdout, stderr, exit code)
    │   ├── ExecutableCommand.java     # External process handler
    │   ├── BadCommand.java            # Error handler for unknown commands
    │   └── builtin/
    │       ├── EchoCommand.java
    │       ├── ExitCommand.java
    │       ├── ChangeDirCommand.java
    │       ├── PwdCommand.java
    │       └── TypeCommand.java
    ├── exceptions/
    │   ├── ReplException.java         # Base exception
    │   └── GracefulExitException.java # Control flow for exit
    └── utils/
        ├── CommandExtractorUtils.java # Parsing with quote/escape handling
        ├── ExecutableUtils.java       # PATH searching with LRU caching
        └── DirUtils.java              # Directory management
```

---

## Core Components

### 1. Entry Point: Main.java

Uses Java 25's **unnamed main method** feature:
```java
void main() → REPL.loop()
```

Simple entry point that creates a REPL instance and starts the interactive loop.

---

### 2. REPL Loop (REPL.java)

Implements the classic Read-Eval-Print Loop pattern using **tail recursion** instead of a while loop:

```
REPL.loop() [tail recursion]
├── 1. read()      → Displays "$" prompt, reads user input
├── 2. eval()      → Creates ReplEvaluator, evaluates command → EvaluationResult
├── 3. handleIO()  → Handles output and redirection based on EvaluationResult
│   ├── Stdout:   redirect to file OR print to terminal
│   └── Stderr:   redirect to file OR print to terminal
└── 4. loop()      → Recursive call
                     Catches: GracefulExitException (exit normally)
                     Catches: ReplException (print error to stderr, continue loop)
                     Catches: RuntimeException (print class+message to stderr, continue loop)
```

**Design Choice:** Uses tail recursion with `@SuppressWarnings("InfiniteRecursion")` instead of a traditional while loop.

**I/O Handling:** REPL is responsible for all file I/O redirection via `handleIO(EvaluationResult)` method. Creates parent directories if needed, overwrites existing files.

---

### 3. Context & Dependency Injection (ReplContext.java)

Two-tier context design using the **Builder pattern**:

**Shared Services** (session-scoped):
- `DirUtils` - Manages working directory state across commands

**Per-Request Data** (command-scoped):
- `originalInput` - Raw user input
- `mainCommandStr` - Parsed command name
- `args` - List of parsed arguments
- `stdoutRedirectTo` - Target file for stdout redirection (null if no redirection)
- `stderrRedirectTo` - Target file for stderr redirection (null if no redirection)
- `executablePath` - Cached resolved executable path for performance (null if not cached)

**Builder Pattern Implementation:**
```java
// Created once at REPL startup
ReplContext.Builder builder = ReplContext.builder(dirUtils);

// For each command
ReplContext context = builder.originalInput(userInput).build();
```

The builder automatically parses the input during `build()` by calling `CommandExtractorUtils.get()`.

---

### 4. Command Resolution (ReplEvaluator.java)

Resolves and executes commands with the following priority order:

**Resolution Order: builtin → executable in PATH → bad command**

```java
processCommand() → EvaluationResult:
├── 1. Check BuiltinCommand.allCommandMap
│   └── Found? Instantiate via Supplier factory
├── 2. Search PATH using ExecutableUtils.findExecutablePath()
│   ├── Found? Cache path in context.setExecutablePath()
│   └── Create ExecutableCommand instance
├── 3. Neither? Create BadCommand instance
├── 4. Execute command → get CommandResult (stdout, stderr, exit code)
└── 5. Return EvaluationResult(commandResult, stdoutRedirectTo, stderrRedirectTo)
```

**Key Features:**
- Returns `EvaluationResult` containing command output + redirect targets
- Caches resolved executable paths in context for performance
- Does NOT handle file I/O (delegated to REPL.handleIO())
- Commands return `CommandResult` with stdout, stderr, and exit code

---

### 5. Command Registry (BuiltinCommand.java)

Static registry mapping command names to command classes:

```java
static Map<String, Class<? extends Command>> allCommandMap = Map.of(
    "exit", ExitCommand.class,
    "echo", EchoCommand.class,
    "type", TypeCommand.class,
    "pwd", PwdCommand.class,
    "cd", ChangeDirCommand.class
)
```

Commands are instantiated via method reference factories (Supplier pattern) for better performance and type safety.

---

### 6. Result Types

**EvaluationResult** - Wrapper separating command output from I/O instructions
```java
record EvaluationResult(
    CommandResult commandResult,
    String stdoutRedirectTo,
    String stderrRedirectTo
)
```
- Returned by `ReplEvaluator.eval()`
- Tells REPL what the command produced AND where to send output
- Clean separation of evaluation logic from I/O handling

**CommandResult** - Command output with exit code
```java
record CommandResult(String stdout, String stderr, int exitCode)
```
- Returned by `Command.execute()`
- Explicit modeling of both output streams
- Exit code indicates success/failure (0 = success)
- Factory methods: `success()`, `empty()`, `error()`

**Messages** - Centralized error message constants
```java
public final class Messages {
    public static final String TYPE_IS_SHELL_BUILTIN = " is a shell builtin";
    public static final String COMMAND_NOT_FOUND = "command not found";
    // ... other constants
}
```
- Eliminates magic strings scattered across codebase
- Single source of truth for error messages
- Easier to maintain and update messages

---

## Design Patterns

### Command Pattern

All commands implement a uniform interface:
```java
interface Command {
    CommandResult execute(ReplContext context) throws ReplException;
}

record CommandResult(String stdout, String stderr, int exitCode) {
    static CommandResult success(String stdout) { ... }
    static CommandResult empty() { ... }
    static CommandResult error(String stderr) { ... }
}
```

**Implementations:**
- **Builtin commands**: `EchoCommand`, `ExitCommand`, `TypeCommand`, `PwdCommand`, `ChangeDirCommand`
- **External commands**: `ExecutableCommand` (spawns external processes)
- **Error handler**: `BadCommand` (throws ReplException for unknown commands)

**Benefits:**
- Uniform interface makes it easy to add new commands
- Each command is self-contained and independently testable
- Clear separation of command logic from parsing/evaluation/I/O
- Explicit modeling of stdout, stderr, and exit codes

---

### Builder Pattern

`ReplContext.Builder` separates construction from representation:
- Shared services injected once at builder creation
- Per-request data set via builder methods
- Automatic parsing during `build()`

---

### Dependency Injection

Context-based injection pattern:
- Shared services (DirUtils) passed through ReplContext
- Commands access services via `context.getDirUtils()`
- Allows test isolation by injecting mock services

---

## Command Types

### Builtin Commands

**EchoCommand** - Prints arguments
- Returns: `CommandResult.success(String.join(" ", args))`

**ExitCommand** - Terminates shell
- Throws: `GracefulExitException` (control flow exception)
- Caught by REPL loop to exit gracefully

**PwdCommand** - Prints working directory
- Returns: `CommandResult.success(DirUtils.getCurrentDir().toAbsolutePath().toString())`

**ChangeDirCommand** - Changes working directory
- Supports: Absolute paths, relative paths, home expansion (`~`)
- Error handling: Catches `NoSuchFileException` → throws `ReplException` with error message
- Throws: `ReplException` if path doesn't exist or I/O error occurs
- Returns: `CommandResult.empty()` on success

**TypeCommand** - Identifies command type
- Checks builtin registry then searches PATH
- Throws: `ReplException` if no command argument provided
- Returns: `CommandResult.success("is a shell builtin" | full path | "not found")`

### External Commands

**ExecutableCommand** - Spawns external processes
- Uses `ProcessBuilder` to execute system commands
- Uses cached executable path from context if available (performance optimization)
- Captures stdout and stderr separately with explicit UTF-8 encoding
- Null checks for process streams (defensive programming)
- Checks process exit code after completion
- Returns: `CommandResult(stdout, stderr, exitCode)` with trailing whitespace stripped
- Throws: `ReplException` with captured output if exit code is non-zero
- Throws: `ReplException` wrapping IOException/InterruptedException on execution failure

### Error Handler

**BadCommand** - Unknown command handler
- Throws: `ReplException` with message from `Messages.COMMAND_NOT_FOUND` constant

---

## Parsing & Utilities

### Command Parsing (CommandExtractorUtils.java)

**Parsing Architecture:**

The parser uses a unified token-based approach:
1. **`parseTokens(String input)`** - Core parsing engine that processes the entire input string
2. **`get(String input)`** - Splits result into command (first token) and arguments (remaining tokens)

This architecture supports **quoted executable names**, where the command itself can contain spaces or special characters:
```bash
'my program' arg          → command: "my program", args: ["arg"]
"exe with spaces" file    → command: "exe with spaces", args: ["file"]
```

**State Machine with Enum-based States:**
- `NORMAL` - Outside any quotes
- `SINGLE_QUOTED` - Inside single quotes
- `DOUBLE_QUOTED` - Inside double quotes
- `ESCAPING` - After backslash (outside quotes)
- `ESCAPING_IN_DOUBLE_QUOTES` - After backslash (inside double quotes)

**Quoting & Escaping Semantics:**
- **Single quotes (`'`)** - Literal string, all chars treated as-is, no escape sequences
- **Double quotes (`"`)** - Most chars treated as-is, but backslash only escapes: `"`, `\`, `$`, `` ` ``, `\n`
- **Backslash (`\`)** - Outside quotes: escapes any char; inside double quotes: only escapes specific chars
- **Adjacent quoted strings** - Concatenated into single argument
- **Empty quotes** - Ignored/removed

**Examples:**
```bash
# Quoted arguments
echo 'hello     world'    → args: ["hello     world"]  # spaces preserved
echo hello\ world         → args: ["hello world"]      # escaped space
echo 'hello'"world"       → args: ["helloworld"]       # concatenated
echo "hello\3"            → args: ["hello\3"]          # '3' not escapable, backslash preserved
echo "test\\case"         → args: ["test\case"]        # backslash escapes backslash
echo "say \"hi\""         → args: ["say "hi""]         # backslash escapes quote

# Quoted executable names
'my program' arg          → command: "my program", args: ["arg"]
"exe with spaces" file    → command: "exe with spaces", args: ["file"]
'prog'gram arg            → command: "proggram", args: ["arg"]  # concatenation
```

**Stream Redirection Parsing:**

After tokenization, the parser identifies redirection operators in the token list:
- **Stdout redirection**: `>` or `1>` operators
- **Stderr redirection**: `2>` operator
- Finds the redirect operator position (must be followed by exactly one token - the target filename)
- Splits tokens into:
  - Command (first token)
  - Arguments (tokens between command and redirect operator)
  - Redirect target (token after redirect operator)

Examples:
```bash
# Stdout redirection
echo hello > output.txt       → command: "echo", args: ["hello"], stdout redirect: "output.txt"
pwd 1> /tmp/dir.txt           → command: "pwd", args: [], stdout redirect: "/tmp/dir.txt"
echo "test" > dir/file.txt    → command: "echo", args: ["test"], stdout redirect: "dir/file.txt"

# Stderr redirection
cat nonexistent 2> errors.txt → command: "cat", args: ["nonexistent"], stderr redirect: "errors.txt"
ls invalid 2> logs/err.txt    → command: "ls", args: ["invalid"], stderr redirect: "logs/err.txt"
```

Error handling:
- Multiple tokens after redirect operator → throws `IllegalArgumentException`
- Redirect operator without target → throws `IllegalArgumentException`
- Both stdout and stderr redirects in same command → throws `IllegalArgumentException` (currently not supported)

---

### Directory Management (DirUtils.java)

**State:**
- `initialDir` - Starting directory (from `System.getProperty("user.dir")`)
- `currentDir` - Current working directory (mutable)
- `HomeDirPath` - Static constant for home directory with Windows fallback

**Features:**
- Home directory expansion: `~` → `$HOME` (with fallback to `user.home` property on Windows)
- Relative path resolution: Resolves against `currentDir`
- Symlink resolution: Uses `.toRealPath()`
- Instance-based (allows test isolation)
- Cross-platform HOME detection: Tries `System.getenv("HOME")` first, then `System.getProperty("user.home")`

---

### Executable Discovery (ExecutableUtils.java)

**PATH Searching with Bounded LRU Caching:**
- Splits `System.getenv("PATH")` using platform-specific separator (`File.pathSeparator`)
- Handles missing or empty PATH gracefully (returns empty array)
- Implements two-level LRU caching with bounds for optimal performance:
  - **Command cache**: Maps command names → resolved paths (max 256 entries, LRU eviction)
  - **Directory listing cache**: Caches scanned directories (max 64 entries, LRU eviction)
- For each directory: Checks cached listings first, scans only on cache miss
- Verification: `Files.isExecutable()` check
- Returns: First match or null
- Thread-safe using synchronized LinkedHashMap with LRU eviction

**Performance:**
- First lookup: Scans directories once, caches results
- Subsequent lookups: O(1) hash map lookup (no filesystem access)
- Bounded cache prevents unbounded memory growth
- Significant improvement for repeated command executions

**Platform Compatibility:**
- Uses `File.pathSeparator` for cross-platform support (`:` on Unix, `;` on Windows)
- Null-safe initialization prevents crashes if PATH is unset

**Exception Handling:**
- `NoSuchFileException` - Silently skipped (PATH dir might not exist)
- Other `IOException` - Wrapped in `RuntimeException`

---

## Exception Handling

### Exception Hierarchy

```
Exception
└── ReplException (recoverable REPL errors)
    └── GracefulExitException (normal exit, not an error)
```

### Exception Usage

**ReplException** - Base for command errors
- Multiple constructors support different error scenarios:
  - `ReplException(Throwable cause)` - Wraps underlying exception
  - `ReplException(String message, Throwable cause)` - Custom message with cause
  - `ReplException(String message)` - Message-only error
- Used by all commands to signal errors
- In REPL: Caught → message printed to stderr, loop continues

**GracefulExitException** - Control flow for exit
- Extends `ReplException` but created with `super((String) null)` (no message)
- Used by ExitCommand to signal clean shutdown
- In REPL: Caught separately → loop terminates (clean exit)

**Error Handling Flow:**
1. Command encounters error (e.g., file not found, invalid input, process failure)
2. Command throws `ReplException` with descriptive error message
3. REPL catches exception and prints message to stderr
4. REPL continues loop (doesn't crash)
5. User sees error message and can correct their input

---

## Testing

Tests are organized by stage tags corresponding to CodeCrafters challenge stages. See [TESTING.md](TESTING.md) for complete documentation.

**Test Organization:**
- **Unit tests** with Mockito for mocking dependencies
- **Integration tests** for end-to-end flows
- **Stage tags** for running challenge-specific tests

**Run all tests:**
```bash
mvn test
```

**Run specific stage:**
```bash
mvn test -Dgroups=EZ5
```

---

## Key Design Decisions

### 1. Tail Recursion for REPL Loop

**Why:** Functional programming style, clean separation of read/eval/print steps

**Tradeoff:** Stack depth could be an issue for extremely long sessions (unlikely in practice)

### 2. Supplier Pattern for Builtin Commands

**Why:** Centralized registry using Supplier pattern, easy to add new commands

**Tradeoff:** None - method references are type-safe, performant, and eliminate reflection overhead

### 3. Null vs Empty String Returns

**Why:** Some commands (like `cd`) have no output on success

**Convention:** Commands return `null` to suppress output, REPL checks `if(output != null)` before printing

### 4. Context as Service Locator

**Why:** Passes both data and services through single object

**Tradeoff:** Hidden dependencies (commands access services via context getters)

### 5. Instance-based DirUtils

**Why:** Allows test isolation (each test can have independent directory state)

**Tradeoff:** Slight overhead vs. static methods

---

## Future Extensibility

### Adding New Builtin Commands

1. Create new class implementing `Command` interface
2. Add to `BuiltinCommand.allCommandMap`
3. Ensure class has no-arg constructor (for reflection)

### Adding New Features

The architecture supports adding:
- Command history (modify REPL to track inputs)
- Pipes and redirection (modify parsing in CommandExtractorUtils)
- Variables and expansion (add to parsing and context)
- Job control (modify command execution)

### Testing New Commands

1. Create test class extending from appropriate pattern
2. Add stage tags with `@Tag("STAGE_ID")`
3. Document in TESTING.md

---

## Dependencies

**Build Dependencies:**
- `org.junit.jupiter:junit-jupiter:5.11.4` (test)
- `org.mockito:mockito-core:5.14.2` (test)
- `org.mockito:mockito-junit-jupiter:5.14.2` (test)

**Java Version:** 25 (with `--enable-preview` flag)

**Maven Compiler:** 3.13.0 with preview features enabled

---

## References

- [CodeCrafters Shell Challenge](https://app.codecrafters.io/courses/shell/overview)
- [TESTING.md](TESTING.md) - Test organization and stage mapping
- [CODE_REVIEW.md](CODE_REVIEW.md) - Detailed code review and recommendations
