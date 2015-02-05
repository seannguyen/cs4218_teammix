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
	
	private SequenceCommand parseSequence (Vector <String> input) throws ShellException {
		int lastSemiColon = -1;
		SequenceCommand seqCommand = new SequenceCommand();
		for (int i = 0; i < input.size(); i++) {
			if (input.get(i).equals(Configurations.SEMICOLON_TOKEN)) {
				Vector <String> pipe = new Vector<String>();
				if (lastSemiColon >= 0) {
					pipe = new Vector<String>(input.subList(lastSemiColon + 1, i));
				} else {
					pipe = new Vector<String>(input.subList(0, i));
				}
				lastSemiColon = i;
				PipeCommand command = parsePipe(pipe);
				seqCommand.addCommand(command);
			} else if (i == input.size() - 1) {
				Vector <String> pipe;
				pipe = new Vector<String>(input.subList(lastSemiColon + 1, input.size()));
				Command command = parsePipe(pipe);
				seqCommand.addCommand(command);
			}
		}
		return seqCommand;
	}
	
	private PipeCommand parsePipe (Vector <String> input) throws ShellException {
		int lastPipeToken = -1;
		PipeCommand pipeCommand = new PipeCommand();
		for (int i = 0; i < input.size(); i++) {
			if (input.get(i).equals(Configurations.PIPE_TOKEN)) {
				Vector <String> call = new Vector<String>();
				if (lastPipeToken >= 0) {
					call = new Vector<String>(input.subList(lastPipeToken + 1, i));
				} else {
					call = new Vector<String>(input.subList(0, i));
				}
				lastPipeToken = i;
				Command command = parseCall(call);
				pipeCommand.addCommand(command);
			} else if (i == input.size() - 1) {
				Vector <String> call;
				call = new Vector<String>(input.subList(lastPipeToken + 1, input.size()));
				Command command = parseCall(call);
				pipeCommand.addCommand(command);
			}
		}
		return pipeCommand;
	}
	
	private Command parseCall(Vector <String> callLine) throws ShellException {
		if (callLine.size() == 0) {
			return new CallCommand(null, null, null, null);
		}
		String appName = callLine.get(0);
		callLine.remove(0);
		
		//merge all IO redirection token with their directories
		for (int i = 0; i < callLine.size(); i++) {
			if (callLine.get(i).equals(Configurations.INPUTREDIRECTION_TOKEN) 
					|| callLine.get(i).equals(Configurations.OUTPUTREDIRECTION_TOKEN) && i < callLine.size() - 1) {
				String mergedElement = callLine.get(i) + callLine.get(i + 1);
				callLine.insertElementAt(mergedElement, i);
				callLine.remove(i + 1);
			}
		}
		String inputRedirectory = "", outputRedirectory = "";
		for (int i = 0; i < callLine.size(); i++) {
			if (callLine.get(i).startsWith(Configurations.INPUTREDIRECTION_TOKEN)) {
				if (inputRedirectory.length() > 0) {
					error();
				}
				inputRedirectory = callLine.get(i);
				callLine.remove(i);
			} else if (callLine.get(i).startsWith(Configurations.OUTPUTREDIRECTION_TOKEN)) {
				if (outputRedirectory.length() > 0) {
					error();
				}
				outputRedirectory = callLine.get(i);
				callLine.remove(i);
			}
		}
		if (inputRedirectory.length() > 0) {
			inputRedirectory = inputRedirectory.substring(1);
		}
		if (outputRedirectory.length() > 0) {
			outputRedirectory = outputRedirectory.substring(1);
		}
		
		Command command = new CallCommand(appName, inputRedirectory, outputRedirectory, callLine);
		return command;
	}
	
	private Vector <String> preprocessCommandLine (String line) throws ShellException{
		Vector <String> result = extractQuote(line);
		result = splitBySpace(result);
		result = extractToken(result, Configurations.SEMICOLON_TOKEN.charAt(0));
		result = extractToken(result, Configurations.PIPE_TOKEN.charAt(0));
		for (int i = 0; i < result.size(); i++) {
			if (result == null || result.get(i).length() == 0) {
				result.remove(i);
				i--;
			}
		}
		return result;
	}

	private Vector <String> extractToken (Vector <String> input, char tokenChar) {
		String token = String.valueOf(tokenChar);
		Vector <String> result = new Vector<String>();
		for (int i = 0; i < input.size(); i++) {
			String element = input.get(i);
			if (element.length() <= 1 || isQuote(element.charAt(0))) {
				result.add(element);
			} else {
				if (element.charAt(0) == tokenChar) {
					result.add(token);
				}
				Vector <String> splitedPhases;
				if (token.equals(Configurations.PIPE_TOKEN)) {
					splitedPhases = new Vector<String>(Arrays.asList(element.split("\\" + token)));
				} else {
					splitedPhases = new Vector<String>(Arrays.asList(element.split(token)));
				}
				for (int j = 1; j < splitedPhases.size(); j += 2) {
					splitedPhases.insertElementAt(token, j);
				}
				result.addAll(splitedPhases);
				if (element.endsWith(token) && element.length() != 1) {
					result.add(token);
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
				String[] splitedPhases = phase.split(Configurations.WHITESPACEREGEX);
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
