package sg.edu.nus.comp.cs4218.shell;

import java.util.Vector;

import sg.edu.nus.comp.cs4218.Configurations;

public class Parser {
	public Command parseCommand (String commandLine) {
		
		return null;
	}
//	
//	private Vector <Vector <Command>> parseCommandLine(String commandline) {
//		Vector <String> processedLine = preprocessCommandLine(commandline);
//		return parseSequence(processedLine);
//	}
//
//	private Vector <Vector <Command>> parseSequence (Vector <String> sequenceLine) {
//		Vector <Vector <Command>> seq = new Vector<Vector<Command>>(); 
//		seq.add(parsePipe(sequenceLine));
//		return seq;
//	}
//	
//	private Vector <Command> parsePipe (Vector <String> pipeLine) {
//		Vector <Command> pipe = new Vector<Command>(); 
//		pipe.add(parseCall(pipeLine));
//		return pipe;
//	}
//	
//	private Command parseCall(Vector <String> callLine) {
//		
//		return null;
//	}
//	
//	private Vector <String> preprocessCommandLine (String line) {
//		Vector <String> extractedQuotes = extractQuote(line);
//		return null;
//	}
//	
//	private Vector <String> extractQuote (String input) {
//		boolean insideQuote = false;
//		for (int i = 0; i < input.length(); i++) {
//			if (!insideQuote && (input.charAt(i) == Configurations.QUOTE_SINGLE 
//				|| input.charAt(i) == Configurations.QUOTE_DOUBLE || input.charAt(i) == Configurations.QUOTE_BACK)) {
//				insideQuote = !insideQuote;
//			} else {
//				quoteFlags.put(i,  insideQuote);
//			}
//		}
//		
//		if (quotePositions.size() % 2 == 1) {
//			error();
//		} 
//		
//		for (int i = 0; i < input;  )
//		
//		return null;
//	}
//
//	private void error() {
//		
//	}
//	
//	private void evaluatePipe(Vector<Command> pipe) {
//		
//	}
}
