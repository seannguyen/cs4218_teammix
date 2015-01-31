package sg.edu.nus.comp.cs4218;

import java.io.OutputStream;
import java.util.Scanner;
import java.util.Vector;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public class SimpleShell implements Shell {
	//attributes
	
	//public methods
	@Override
	public void parseAndEvaluate(String cmdline, OutputStream stdout)
			throws AbstractApplicationException, ShellException {
		Vector <Vector <Command>> pipes = parseCommandLine(cmdline);
		
		//process sequence
		for (int i = 0; i < pipes.size(); i++) {
			Vector <Command> pipe = pipes.get(i);
			evaluatePipe(pipe);
		}
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

	//private helper methods
	private Vector <Vector <Command>> parseCommandLine(String commandline) {
		Vector <String> processedLine = preprocessCommandLine(commandline);
		return parseSequence(processedLine);
	}

	private Vector <Vector <Command>> parseSequence (Vector <String> sequenceLine) {
		Vector <Vector <Command>> seq = new Vector<Vector<Command>>(); 
		seq.add(parsePipe(sequenceLine));
		return seq;
	}
	
	private Vector <Command> parsePipe (Vector <String> pipeLine) {
		Vector <Command> pipe = new Vector<Command>(); 
		pipe.add(parseCall(pipeLine));
		return pipe;
	}
	
	private Command parseCall(Vector <String> callLine) {
		
		return null;
	}
	
	private Vector <String> preprocessCommandLine (String line) {
		
		return null;
	}
	
	private void evaluatePipe(Vector<Command> pipe) {
		
	}
}
