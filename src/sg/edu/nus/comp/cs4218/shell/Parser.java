package sg.edu.nus.comp.cs4218.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
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
		try {
			String appName = callLine.get(0);
			callLine.remove(0);
			Vector<String> ioRedirectories = getIoRedirectories(callLine);
			Vector<String> arguments = getFilesFromGrobPattern(callLine);
			Command command = new CallCommand(appName, ioRedirectories.get(0), ioRedirectories.get(1), arguments);
			return command;
		} catch (IOException e) {
			throw new ShellException(e.getMessage());
		}
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

	private Vector<String> getFilesFromGrobPattern(Vector<String> input) throws IOException {
		for (int i = 0; i < input.size(); i++) {
			final Vector<String> results = new Vector<String>();
			String root = "." + File.separator;
			Path startDir = Paths.get(root);
			FileSystem fs = FileSystems.getDefault();
			String globPattern = "glob:" + root + input.get(i);
			if (System.getProperty("file.separator") != null 
					&& System.getProperty("file.separator").equals(Configurations.WINDOWS_FILESEPARATOR)) {
				globPattern = globPattern.replace("\\", "\\\\");
			}
			final PathMatcher matcher = fs.getPathMatcher(globPattern);

			FileVisitor<Path> matcherVisitor = new SimpleFileVisitor<Path>() {
				@Override
			    public FileVisitResult visitFile(Path file, BasicFileAttributes attribs) {
					return checkFileName(file);
			    }
				@Override
				public FileVisitResult preVisitDirectory(Path file, BasicFileAttributes attribs) {
			        return checkFileName(file);
				}
				
				private FileVisitResult checkFileName(Path file) {
					if (matcher.matches(file)) {
			            results.add(file.toString());
			        }
			        return FileVisitResult.CONTINUE;
				}
			};
			Files.walkFileTree(startDir, matcherVisitor);
			for (int j = 0; j < results.size(); j++) {
				int originalLevelDeep = countOccurrences(root + input.get(i), File.separator);
				int resultLevelDeep = countOccurrences(results.get(j), File.separator);
				if (originalLevelDeep != resultLevelDeep) {
					results.remove(j);
					j--;
				}
			}
			if (results.size() > 0) {
				input.remove(i);
				input.addAll(results);
				i += results.size() - 1;
			}
		}
		return input;
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

	private Vector <String> getIoRedirectories(Vector<String> input) throws ShellException {
		String inputRedirectory = "", outputRedirectory = "";
		//merge all IO redirection token with their directories
		for (int i = 0; i < input.size(); i++) {
			if (input.get(i).equals(Configurations.INPUTREDIRECTION_TOKEN) 
					|| input.get(i).equals(Configurations.OUTPUTREDIRECTION_TOKEN) && i < input.size() - 1) {
				String mergedElement = input.get(i) + input.get(i + 1);
				input.insertElementAt(mergedElement, i);
				input.remove(i + 1);
			}
		}
		for (int i = 0; i < input.size(); i++) {
			if (input.get(i).startsWith(Configurations.INPUTREDIRECTION_TOKEN)) {
				if (inputRedirectory.length() > 0) {
					error();
				}
				inputRedirectory = input.get(i);
				input.remove(i);
			} else if (input.get(i).startsWith(Configurations.OUTPUTREDIRECTION_TOKEN)) {
				if (outputRedirectory.length() > 0) {
					error();
				}
				outputRedirectory = input.get(i);
				input.remove(i);
			}
		}
		if (inputRedirectory.length() > 0) {
			inputRedirectory = inputRedirectory.substring(1);
		}
		if (outputRedirectory.length() > 0) {
			outputRedirectory = outputRedirectory.substring(1);
		}
		Vector<String> result = new Vector<String>();
		result.addElement(inputRedirectory);
		result.add(outputRedirectory);
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

	private int countOccurrences(String s, String token) {
		int counter = 0;
		for( int i = 0; i < s.length(); i++ ) {
		    if (String.valueOf(s.charAt(i)).equals(token)) {
		        counter++;
		    } 
		}
		return counter;
	}
}
