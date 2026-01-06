package repl.exceptions;

public class NoSuchFileOrDirectoryException extends ReplException {
	public NoSuchFileOrDirectoryException(String pathStr) {
		super(pathStr + ": No such file or directory");
	}
}
