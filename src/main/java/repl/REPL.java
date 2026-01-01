package repl;

import java.util.Scanner;

public class REPL {
	private static final String COMMAND_NOT_FOUND = "command not found";

	public REPL() {
	}

	public void loop() {
		showPrompt();
		String command = readPrompt();
		processCommand(command);
	}

	private void showPrompt() {
		System.out.print("$ ");
	}

	private String readPrompt() {
		Scanner scanner = new Scanner(System.in);
		return scanner.nextLine();
	}

	private void processCommand(String command) {
		commandNotFound(command);
	}

	private void commandNotFound(String badCommand) {
		System.out.print(badCommand + ": " + COMMAND_NOT_FOUND);
	}

}
