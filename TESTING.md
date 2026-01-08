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
| `NI6` | Quoting - Single quotes | `CommandExtractorUtilsTest` | 8 |
| `TG6` | Quoting - Double quotes | `CommandExtractorUtilsTest` | 10 |
| `YT5` | Quoting - Backslash outside quotes | `CommandExtractorUtilsTest` | 8 |

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
| `IZ3` | Implement echo | `EchoCommandTest` | 4 |
| `PN5` | Implement exit | `ExitCommandTest` | 1 |
| `EZ5` | Implement type | `TypeCommandTest` | 4 |
| `CZ2` | Handle invalid commands | `BadCommandTest` | 2 |

### Executable Stages

| Stage | Description | Test Class | Test Count |
|-------|-------------|------------|------------|
| `MG5` | Locate executable files | `TypeCommandTest` | 1 |
| `IP1` | Run a program | `ExecutableCommandTest` | 3 |

## Test Files Overview

| Test File | Package | Description |
|-----------|---------|-------------|
| `CommandExtractorUtilsTest` | `repl.utils` | Command parsing, quoting, escaping |
| `DirUtilsTest` | `repl.utils` | Directory operations, path resolution |
| `EchoCommandTest` | `repl.commands.builtin` | Echo command behavior |
| `ExitCommandTest` | `repl.commands.builtin` | Exit command behavior |
| `TypeCommandTest` | `repl.commands.builtin` | Type command, builtin detection |
| `PwdCommandTest` | `repl.commands.builtin` | Pwd command behavior |
| `ChangeDirCommandTest` | `repl.commands.builtin` | Cd command, path navigation |
| `ExecutableCommandTest` | `repl.commands` | External program execution |
| `BadCommandTest` | `repl.commands` | Invalid command handling |
| `ReplContextTest` | `repl` | Context builder and parsing |
| `ReplEvaluatorTest` | `repl` | Command evaluation integration |
| `ReplExceptionTest` | `repl.exceptions` | Exception handling |
| `GracefulExitExceptionTest` | `repl.exceptions` | Exit exception behavior |
