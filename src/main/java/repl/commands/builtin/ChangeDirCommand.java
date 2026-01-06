package repl.commands.builtin;

import repl.BuiltinCommand;
import repl.commands.Command;
import repl.exceptions.NoSuchFileOrDirectoryException;
import repl.utils.DirUtils;

import java.util.List;


public class ChangeDirCommand implements Command {
	@Override
	public String execute(String originalInput, String mainCommandStr, List<String> args) {
		String requestedPath = args.getFirst();
		try {
			DirUtils.setCurrentDir(requestedPath);
			return null;
		} catch (NoSuchFileOrDirectoryException e){
			return BuiltinCommand.cd + ": " + e.getMessage();
		}
	}
}
