# Test Documentation

This document describes the test organization and how tests map to [CodeCrafters Shell Challenge](https://app.codecrafters.io/courses/shell/overview) stages.

## Running Tests

```bash
# Run all tests
mvn test

# Run tests for a specific stage
mvn test -Dgroups=EZ5

# Run tests for multiple stages
mvn test -Dgroups=NI6,TG6,YT5
```

## Stage Tags

Tests are tagged with JUnit 5 `@Tag` annotations corresponding to CodeCrafters challenge stages.

### Quoting Stages

| Stage | Description | Test Class | Test Count |
|-------|-------------|------------|------------|
| `QJ0` | Quoting - Executing a quoted executable | `CommandExtractorUtilsTest` | 16 |
| `NI6` | Quoting - Single quotes | `CommandExtractorUtilsTest` | 11 |
| `TG6` | Quoting - Double quotes | `CommandExtractorUtilsTest` | 13 |
| `YT5` | Quoting - Backslash outside quotes | `CommandExtractorUtilsTest` | 11 |
| `GU3` | Quoting - Backslash within double quotes | `CommandExtractorUtilsTest` | 3 |
| `LE5` | Quoting - Backslash within single quotes | `CommandExtractorUtilsTest` | 2 |

### Navigation Stages

| Stage | Description | Test Class | Test Count |
|-------|-------------|------------|------------|
| `EI0` | Navigation - The pwd builtin | `PwdCommandTest`, `TypeCommandTest` | 3 |
| `RA6` | Navigation - cd builtin: Absolute paths | `ChangeDirCommandTest` | 2 |
| `GQ9` | Navigation - cd builtin: Relative paths | `ChangeDirCommandTest` | 1 |
| `GP4` | Navigation - cd builtin: Home directory | `ChangeDirCommandTest` | 1 |

### Command Stages

| Stage | Description | Test Class | Test Count |
|-------|-------------|------------|------------|
| `FF0` | Implement a REPL | `ReplEvaluatorTest` | 1 |
| `IZ3` | Implement echo | `EchoCommandTest` | 4 |
| `PN5` | Implement exit | `ExitCommandTest` | 1 |
| `EZ5` | Implement type | `TypeCommandTest` | 4 |
| `CZ2` | Handle invalid commands | `BadCommandTest`, `ReplEvaluatorTest` | 3 |

### Executable Stages

| Stage | Description | Test Class | Test Count |
|-------|-------------|------------|------------|
| `MG5` | Locate executable files | `TypeCommandTest` | 1 |
| `IP1` | Run a program | `ExecutableCommandTest` | 4 |

### Redirection Stages

| Stage | Description | Test Class | Test Count |
|-------|-------------|------------|------------|
| `JV1` | Redirection - Redirect stdout | `ReplEvaluatorTest`, `REPLTest` | 18 |
| `VZ4` | Redirection - Redirect stderr | `ReplEvaluatorTest`, `REPLTest` | 9 |

### Other Stages

| Stage | Description | Notes |
|-------|-------------|-------|
| `OO8` | Print a prompt | Trivial - REPL always prints `$` prompt |

## Test Files Overview

| Test File | Package | Description |
|-----------|---------|-------------|
| `CommandExtractorUtilsTest` | `repl.utils` | Command parsing, quoting, escaping, quoted executable names |
| `DirUtilsTest` | `repl.utils` | Directory operations, path resolution, Windows HOME fallback |
| `EchoCommandTest` | `repl.commands.builtin` | Echo command behavior |
| `ExitCommandTest` | `repl.commands.builtin` | Exit command behavior |
| `TypeCommandTest` | `repl.commands.builtin` | Type command, builtin detection |
| `PwdCommandTest` | `repl.commands.builtin` | Pwd command behavior |
| `ChangeDirCommandTest` | `repl.commands.builtin` | Cd command, path navigation |
| `ExecutableCommandTest` | `repl.commands` | External program execution |
| `BadCommandTest` | `repl.commands` | Invalid command handling |
| `ReplContextTest` | `repl` | Context builder and parsing |
| `ReplEvaluatorTest` | `repl` | Command evaluation integration, stdout/stderr redirection, PATH caching |
| `REPLTest` | `repl` | REPL I/O handling integration tests (file redirection) |
| `ReplExceptionTest` | `repl.exceptions` | Exception handling |
| `GracefulExitExceptionTest` | `repl.exceptions` | Exit exception behavior |
