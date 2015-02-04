package sg.edu.nus.comp.cs4218.shell;

import java.util.Arrays;
import java.util.Stack;
import java.util.Vector;

import sg.edu.nus.comp.cs4218.Configurations;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public class Parser {
	public Command parseCommandLine (String commandLine) throws ShellException {
		Vector <String> preprocessedLine = preprocessCommandLine(commandLine);
		Command  command = parseSequence(preprocessedLine);
		return command;
	}

	//PRIVATE HELPER METHODS
	
	private Command parseSequence (Vector <String> sequenceLine) throws ShellException {
		int lastSemiColon = -1;
		SequenceCommand seqCommand = new SequenceCommand();
		for (int i = 0; i < sequenceLine.size(); i++) {
			if (sequenceLine.get(i) == Configurations.SEMICOLON) {
				Vector <String> callLine = new Vector<String>();
				if (lastSemiColon >= 0) {
					callLine = new Vector<String>(sequenceLine.subList(lastSemiColon + 1, i));
				} else {
					callLine = new Vector<String>(sequenceLine.subList(0, i));
				}
				lastSemiColon = i;
				Command command = parseCall(callLine);
				seqCommand.addCommand(command);
			} else if (i == sequenceLine.size() - 1) {
				Vector <String> commandPhase;
				if (lastSemiColon >= 0) {
					commandPhase = new Vector<String>(sequenceLine.subList(lastSemiColon + 1, i +1));
				} else {
					commandPhase = sequenceLine;
				}
				Command command = parseCall(commandPhase);
				seqCommand.addCommand(command);
			}
		}
		return seqCommand;
	}
//	
//	private Vector <Command> parsePipe (Vector <String> pipeLine) {
//		Vector <Command> pipe = new Vector<Command>(); 
//		pipe.add(parseCall(pipeLine));
//		return pipe;
//	}
//	
	private Command parseCall(Vector <String> callLine) throws ShellException {
		if (callLine.size() == 0) {
			return new CallCommand("", null);
		}
		String appName = callLine.get(0);
		callLine.remove(0);
		
		for (int i = 0; i < callLine.size(); i++) {
			String string = callLine.get(i);
			if (string == Configurations.INPUTREDIRECTION_TOKEN) {
				if (i > callLine.size() - 2 && callLine.get(i + 1) == Configurations.INPUTREDIRECTION_TOKEN
						&& callLine.get(i + 1) == Configurations.OUTPUTREDIRECTION_TOKEN) { 
					//mean that this is not the last element
					throw new ShellException(Configurations.MESSAGE_ERROR_PARSING);
				}
			} else {
				
			}
		}
		
		Command command = new CallCommand(appName, callLine); 
		return command;
	}
	
	private Vector <String> preprocessCommandLine (String line) throws ShellException{
		Vector <String> result = extractQuote(line);
		result = splitBySpace(result);
		result = extractSemiColon(result);
		for (int i = 0; i < result.size(); i++) {
			if (result.get(i).length() == 0) {
				result.remove(i);
				i--;
			}
		}
		return result;
	}
	
	private Vector <String> extractSemiColon (Vector <String> input) {
		Vector <String> result = new Vector<String>();
		for (int i = 0; i < input.size(); i++) {
			String element = input.get(i);
			if (element.length() == 0 || isQuote(element.charAt(0))) {
				result.add(element);
			} else {
				if (element.charAt(0) == Configurations.SEMICOLONCHAR) {
					result.add(Configurations.SEMICOLON);
				}
				Vector <String> splitedPhases = new Vector<String>(Arrays.asList(element.split(Configurations.SEMICOLON)));
				for (int j = 1; j < splitedPhases.size(); j += 2) {
					splitedPhases.insertElementAt(Configurations.SEMICOLON, j);
				}
				result.addAll(splitedPhases);
				if (element.charAt(element.length() - 1) == Configurations.SEMICOLONCHAR && element.length() != 1) {
					result.add(Configurations.SEMICOLON);
				}
			}
		}
		return result;
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
				String[] splitedPhases = phase.split(Configurations.SEPERATORREGEX);
				result.addAll(new Vector<String>(Arrays.asList(splitedPhases)));
			}
		}
		return result;
	}
	
	private Vector <String> extractQuote (String input) throws ShellException {
		Vector <String> result = new Vector<String>();
		Stack <Character> quotes = new Stack<Character>();
		int lastQuoteIndex = -1;
		for (int i = 0; i < input.length(); i++) {
			if (quotes.isEmpty()) {
				if (isQuote(input.charAt(i))) {
					result.add(input.substring(lastQuoteIndex + 1, i));
					lastQuoteIndex = i;
					quotes.push(input.charAt(i));
				} else if (i == input.length() - 1) {
					if (lastQuoteIndex >= 0) {
						result.add(input.substring(lastQuoteIndex + 1, i + 1));
					} else {
						result.add(input);
					}		
				}
			} else {
				if (quotes.peek() == Configurations.QUOTE_DOUBLE && input.charAt(i) == Configurations.QUOTE_BACK) {
					quotes.push(Configurations.QUOTE_BACK);
				} else if (input.charAt(i) == Configurations.QUOTE_BACK && quotes.size() > 1) {
					quotes.pop();
				} else if (input.charAt(i) == quotes.peek()) {
					quotes.pop();
					result.add(input.substring(lastQuoteIndex, i + 1));
					lastQuoteIndex = i;
				}
			}
		}
		if (quotes.size() != 0) {
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
