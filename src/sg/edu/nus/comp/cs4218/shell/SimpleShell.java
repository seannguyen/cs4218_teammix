package sg.edu.nus.comp.cs4218.shell;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

import sg.edu.nus.comp.cs4218.Configurations;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public class SimpleShell implements Shell {
	//public methods
	@Override
	public void parseAndEvaluate(String cmdline, OutputStream stdout)
			throws AbstractApplicationException, ShellException {
		Parser parser = new Parser();
		Command command = parser.parseCommand(cmdline);
		command.evaluate(null, stdout);
	}

	public static void main(String[] args) throws AbstractApplicationException, ShellException {
		System.out.println(Configurations.MESSAGE_WELCOME);
		Scanner input = new Scanner(System.in);
		Shell shell = new SimpleShell();
		while (Environment.running) {
			System.out.printf(Configurations.MESSAGE_PROMPT);
			String line  = input.nextLine();
			shell.parseAndEvaluate(line, System.out);
		}
		input.close();
	}
}
