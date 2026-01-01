package repl;

import java.util.Scanner;

public class REPL {
	private static final String COMMAND_NOT_FOUND = "command not found";

	public REPL() {
	}

	public void loop() {
		String input = read();
		String output = eval(input);
		print(output);
		loop();
	}

	private String read() {
		showPrompt();
		return readPrompt();
	}

	private String eval(String input) {
		return commandNotFound(input);
	}

	private void print(String output) {
		System.out.println(output);
	}

	private void showPrompt() {
		System.out.print("$ ");
	}

	private String readPrompt() {
		Scanner scanner = new Scanner(System.in);
		return scanner.nextLine();
	}

	private String commandNotFound(String badCommand) {
		return badCommand + ": " + COMMAND_NOT_FOUND;
	}

}
