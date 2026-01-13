package repl.commands.builtin;

import repl.BuiltinCommand;
import repl.Messages;
import repl.ReplContext;
import repl.commands.Command;
import repl.commands.CommandResult;
import repl.utils.ExecutableUtils;

import java.nio.file.Path;

/**
 * Builtin command that identifies the type of command.
 *
 * <p>Implements the {@code type} command, which determines if a command is a
 * builtin, an executable in PATH, or not found.
 */
public class TypeCommand implements Command {

	/**
	 * Executes the type command to identify a command's type.
	 *
	 * <p>Checks if the specified command is a builtin or exists in PATH,
	 * returning an appropriate message.
	 *
	 * @param context the REPL context containing the command to check
	 * @return command result with description of the command type or error message
	 */
	@Override
	public CommandResult execute(ReplContext context) {
		if (context.getArgs().isEmpty()) {
			return CommandResult.error(Messages.TYPE_MISSING_OPERAND);
		}
		String commandToTest = context.getArgs().getFirst();
		String output;
		if(BuiltinCommand.allCommandMap.containsKey(commandToTest)) {
			output = commandToTest + Messages.TYPE_IS_SHELL_BUILTIN;
		} else {
			Path executablePath = ExecutableUtils.findExecutablePath(commandToTest);
			if (executablePath != null)
				output = commandToTest + " is " + executablePath;
			else
				output = commandToTest + Messages.TYPE_NOT_FOUND;
		}
		return CommandResult.success(output);
	}
}
