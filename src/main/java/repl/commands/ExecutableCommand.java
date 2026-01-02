package repl.commands;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutableCommand implements Command {
	@Override
	public String process(String originalInput, String mainCommandStr, List<String> args) {
		StringBuilder sb = new StringBuilder();
		appendArgs(mainCommandStr, args, sb);
		return sb.toString();
	}

	private static void appendArgs(String mainCommandStr, List<String> args, StringBuilder sb) {
		appendArgCounts(args, sb);
		sb.append("Arg #0 (program name): ");
		sb.append(mainCommandStr);
		sb.append("\n");
		AtomicInteger count = new AtomicInteger(1);
		args.forEach(arg -> {
			sb.append("Arg #");
			sb.append(count.getAndIncrement());
			sb.append(": ");
			sb.append(arg);
		});
	}

	private static void appendArgCounts(List<String> args, StringBuilder sb) {
		sb.append("Program was passed ");
		sb.append(args.size() + 1);
		sb.append(" args (including program name).\n");
	}

}
