package repl.commands.builtin;

import repl.commands.Command;
import repl.exceptions.ReplException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class PwdCommand implements Command {
	@Override
	public String execute(String originalInput, String mainCommandStr, List<String> args) throws ReplException {
		Path currentDirRelativePath = Paths.get("");
		Path currentDirAbsolutePath = currentDirRelativePath.toAbsolutePath();
		return currentDirAbsolutePath.toString();
	}
}
