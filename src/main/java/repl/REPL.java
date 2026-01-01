package repl;

import repl.exceptions.GracefulExitException;
import repl.exceptions.ReplException;

import java.util.Arrays;
import java.util.Scanner;

public class REPL {

	public REPL() {
	}

	public void loop() {
		try {
			String input = read();
			ReplEvaluator evaluator = new ReplEvaluator(input);
			String output = evaluator.eval();
			print(output);
			loop();
		} catch (GracefulExitException _) {
		}
	}

	private String read() {
		showPrompt();
		return readPrompt();
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

}
