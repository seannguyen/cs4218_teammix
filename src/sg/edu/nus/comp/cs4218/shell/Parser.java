package sg.edu.nus.comp.cs4218.shell;

import java.util.Arrays;
import java.util.Vector;

import sg.edu.nus.comp.cs4218.Configurations;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public class Parser {
	public Command parseCommand (String commandLine) throws ShellException {
		Vector <String> preprocessedLine = preprocessCommandLine(commandLine);
		Command  command = parseCall(preprocessedLine);
		return command;
	}

	//PRIVATE HELPER METHODS
	
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
	private Command parseCall(Vector <String> callLine) throws ShellException {
		if (callLine.isEmpty()) {
			error();
		}
		String appName = callLine.get(0);
		callLine.remove(0);
		Command command = new CallCommand(appName, callLine); 
		return command;
	}
	
	private Vector <String> preprocessCommandLine (String line) throws ShellException{
		Vector <String> extractedQuotes = extractQuote(line);
		Vector <String> phases = splitBySpace(extractedQuotes);
		for (int i = 0; i < phases.size(); i++) {
			if (phases.get(i).length() == 0) {
				phases.remove(i);
				i--;
			}
		}
		return phases;
	}
	
	private Vector <String> splitBySpace (Vector <String> input) {
		Vector <String> result = new Vector<String>();
		for (int i = 0; i < input.size(); i++) {
			String phase = input.get(i);
			if (input.get(i) == null || input.get(i).length() == 0) {
				continue;
			}
			if (isQuote(phase.charAt(0))) {
				result.add(input.get(i));
			} else {
				String[] splitedPhases = phase.split(" ");
				result.addAll(new Vector<String>(Arrays.asList(splitedPhases)));
			}
		}
		return result;
	}
	
	private Vector <String> extractQuote (String input) throws ShellException {
		Vector <String> result = new Vector<String>();
		boolean insideQuote = false;
		int lastQuoteIndex = 0;
		for (int i = 0; i < input.length(); i++) {
			if (!insideQuote && isQuote(input.charAt(i))) {
				result.add(input.substring(lastQuoteIndex, i));
				lastQuoteIndex = i;
				insideQuote = !insideQuote;
			} else if (insideQuote && input.charAt(i) == input.charAt(lastQuoteIndex)) {
				result.add(input.substring(lastQuoteIndex, i + 1));
				lastQuoteIndex = i;
				insideQuote = !insideQuote;
			} else if (i == input.length() - 1) {
				if (isQuote(input.charAt(lastQuoteIndex))) {
					result.add(input.substring(lastQuoteIndex + 1, i + 1));
				} else {
					result.add(input);
				}
			}
		}
		if (insideQuote) {
			error();
		}
		return result;
	}

	private void error() throws ShellException{
		throw new ShellException(Configurations.MESSAGE_ERROR_PARSING);
	}
	
	private boolean isQuote(char c) {
		if (c == Configurations.QUOTE_SINGLE || c == Configurations.QUOTE_DOUBLE || c ==  Configurations.QUOTE_BACK) {
			return true;
		}
		return false;
	}
}
