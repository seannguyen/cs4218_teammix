package sg.edu.nus.comp.cs4218.shell;

import java.io.ByteArrayOutputStream;
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
import java.util.Stack;
import java.util.Vector;

import sg.edu.nus.comp.cs4218.Configurations;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public class Parser {
	Vector<Boolean> quoteFlags = new Vector<Boolean>();
	
	public Command parseCommandLine (String commandLine) throws ShellException, AbstractApplicationException {
		refreshParser();
		Vector <String> preprocessedLine = splitLine(commandLine);
		return parseSequence(preprocessedLine);
	}

	//PROTECTED CORE METHODS
	
	protected Vector <String> splitLine (String input) throws ShellException{
		Vector <String> result = new Vector<String>();
		if (input == null || input.length() == 0) {
			return result;
		}
		markQuotes(input);
		int lastStop = -1;
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == Configurations.SPACE_CHAR || input.charAt(i) == Configurations.TAB_CHAR) {
				if (this.quoteFlags.get(i)) {
					continue;
				}
				String element = input.substring(lastStop + 1, i);
				result.add(element);
				lastStop = i;
			} else if (input.charAt(i) == Configurations.PIPE_TOKEN.charAt(0) 
					|| input.charAt(i) == Configurations.SEMICOLON_TOKEN.charAt(0)) {
				if (this.quoteFlags.get(i)) {
					continue;
				}
				String element = input.substring(lastStop + 1, i);
				String token = input.substring(i, i + 1);
				result.add(element);
				result.add(token);
				lastStop = i;
			} else if (i == input.length() - 1) {
					String element = input.substring(lastStop + 1, i + 1);
					result.add(element);
			}
		}
		
		//remove empty elements
		for (int i = 0; i < result.size(); i++) {
			if (result == null || result.get(i).length() == 0) {
				result.remove(i);
				i--;
			}
		}
		return result;
	}
	
	protected CallCommand parseCall(Vector <String> callLine) throws ShellException, AbstractApplicationException {
		if (callLine.size() == 0) {
			return new CallCommand(null, null, null, null);
		}
		try {
			//process IO redirection first
			Vector<String> ioRedirectories = getIoRedirectories(callLine);
			if (callLine.size() == 0) {
				return new CallCommand(null, null, null, null);
			}
			
			//then process command substitution and globing
			callLine = substituteCommand(callLine);
			Vector<String> namePart = new Vector<String>();
			namePart.add(callLine.get(0));
			callLine.remove(0);
			namePart = removeQuoteTokens(namePart);
			namePart = getFilesFromGrobPattern(namePart);
			String appName = namePart.get(0);
			namePart.remove(0);
			
			callLine = removeQuoteTokens(callLine);
			Vector<String> args = new Vector<String>();
			if (!appName.equals(Configurations.APPNAME_FIND)) {
				callLine = getFilesFromGrobPattern(callLine);
			}
			args.addAll(namePart);
			args.addAll(callLine);
			CallCommand command = new CallCommand(appName, ioRedirectories.get(0), ioRedirectories.get(1), args);
			return command;
		} catch (IOException e) {
			throw new ShellException(e.getMessage());
		}
	}
	
	protected Vector<String> substituteCommand(Vector<String> input) throws ShellException, AbstractApplicationException {
		Vector<String> result = new Vector<String>();
		if (input == null) {
			return result;
		}
		
		for (String element : input) {
			Stack <Character> quotes = new Stack<Character>();
			int lastQuotePos = -1;
			for (int i = element.length() - 1; i >= 0; i--) {
				if (isQuote(element.charAt(i))) {
					if (quotes.isEmpty()) {
						quotes.push(element.charAt(i));
						lastQuotePos = i;
					} else if (quotes.peek() == Configurations.QUOTE_DOUBLE 
							&& element.charAt(i) == Configurations.QUOTE_BACK) {
						quotes.push(Configurations.QUOTE_BACK);
						lastQuotePos = i;
					} else if (element.charAt(i) == quotes.peek()) {
						quotes.pop();
						if (element.charAt(i) == Configurations.QUOTE_BACK) {
							String subCommandLine = element.substring(i + 1, lastQuotePos);
							Command subCmd = parseCommandLine(subCommandLine);
							ByteArrayOutputStream outStream = new ByteArrayOutputStream();
							subCmd.evaluate(null, outStream);
							String evaluatedResult = Configurations.QUOTE_BACK + 
									outStream.toString() + Configurations.QUOTE_BACK;
							String firstHalf = element.substring(0, i);
							String secondHalf = element.substring(lastQuotePos + 1);
							element = firstHalf + evaluatedResult + secondHalf;
						}
					}
				}
			}
			if (quotes.size() != 0) {
				error();
			}
			result.add(element);
		}
		return result;
	}
	
	protected Vector<String> getFilesFromGrobPattern(Vector<String> input) throws IOException {
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
	
	protected Vector <String> getIoRedirectories(Vector<String> input) 
			throws ShellException, AbstractApplicationException {
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
				inputRedirectory = substituteCommand(inputRedirectory);
			} else if (input.get(i).startsWith(Configurations.OUTPUTREDIRECTION_TOKEN)) {
				if (outputRedirectory.length() > 0) {
					error();
				}
				outputRedirectory = input.get(i);
				input.remove(i);
				outputRedirectory = substituteCommand(outputRedirectory);
			}
		}
		if (inputRedirectory.length() > 0) {
			inputRedirectory = inputRedirectory.substring(1);
		}
		if (outputRedirectory.length() > 0) {
			outputRedirectory = outputRedirectory.substring(1);
		}
		Vector<String> result = new Vector<String>();
		result.add(inputRedirectory);
		result.add(outputRedirectory);
		return result;
	}

	protected Vector <String> removeQuoteTokens(Vector<String> input) throws ShellException{
		Vector<String> result = new Vector<String>();
		for (String element : input) {
			Stack <Character> quotes = new Stack<Character>();
			for (int i = element.length() - 1; i >= 0; i--) {
				if (isQuote(element.charAt(i))) {
					if (quotes.isEmpty()) {
						quotes.push(element.charAt(i));
						element = removeCharFromString(element, i);
					} else if (quotes.peek() == Configurations.QUOTE_DOUBLE 
							&& element.charAt(i) == Configurations.QUOTE_BACK) {
						quotes.push(Configurations.QUOTE_BACK);
						element = removeCharFromString(element, i);
					} else if (element.charAt(i) == quotes.peek()) {
						quotes.pop();
						element = removeCharFromString(element, i);
					}
				}
			}
			if (quotes.size() != 0) {
				error();
			}
			result.add(element);
		}
		return result;
	}

	//PRIVATE HELPER METHODS (This methods are trivial, so no need test cases for this)
	
	private SequenceCommand parseSequence (Vector <String> input) throws ShellException, AbstractApplicationException {
		SequenceCommand seqCommand = new SequenceCommand();
		Vector<Vector<String>> pipes = splitByToken(input, Configurations.SEMICOLON_TOKEN);
		for (Vector<String> pipe : pipes) {
			PipeCommand pipeCmd = parsePipe(pipe);
			seqCommand.addCommand(pipeCmd);
		}
		return seqCommand;
	}
	
	private PipeCommand parsePipe (Vector <String> input) throws ShellException, AbstractApplicationException {
		PipeCommand pipeCommand = new PipeCommand();
		Vector<Vector<String>> calls = splitByToken(input, Configurations.PIPE_TOKEN);
		for (Vector<String> call : calls) {
			CallCommand callCmd = parseCall(call);
			pipeCommand.addCommand(callCmd);
		}
		return pipeCommand;
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

	private void refreshParser() {
		this.quoteFlags = new Vector<Boolean>();
	}

	private Vector<Vector<String>> splitByToken(Vector<String> input, String token) {
		 Vector<Vector<String>> result = new Vector<Vector<String>>();
		int lastToken = -1;
		for (int i = 0; i < input.size(); i++) {
			if (input.get(i).equals(token)) {
				Vector <String> subList = new Vector<String>();
				if (lastToken >= 0) {
					subList = new Vector<String>(input.subList(lastToken + 1, i));
				} else {
					subList = new Vector<String>(input.subList(0, i));
				}
				lastToken = i;
				result.add(subList);
			} else if (i == input.size() - 1) {
				Vector <String> subList;
				subList = new Vector<String>(input.subList(lastToken + 1, input.size()));
				result.add(subList);
			}
		}
		return result;
	}

	private String substituteCommand(String input) throws ShellException, AbstractApplicationException {
		Vector<String> vectorInput = new Vector<String>();
		vectorInput.add(input);
		Vector<String> results = substituteCommand(vectorInput);
		String result = "";
		for (String string : results) {
			result += string;
		}
		return result;
	}
	
	private String removeCharFromString(String input, int index) {
		StringBuilder sb = new StringBuilder(input);
		sb.deleteCharAt(index);
		return sb.toString();
	}
	
	private void markQuotes (String input) throws ShellException {
		Stack <Character> quotes = new Stack<Character>();
		for (int i = 0; i < input.length(); i++) {
			if (isQuote(input.charAt(i))) {
				if (quotes.isEmpty()) {
					quotes.push(input.charAt(i));
					this.quoteFlags.add(true);
				} else if (quotes.peek() == Configurations.QUOTE_DOUBLE && input.charAt(i) == Configurations.QUOTE_BACK) {
					quotes.push(Configurations.QUOTE_BACK);
					this.quoteFlags.add(true);
				} else if (input.charAt(i) == quotes.peek()) {
					quotes.pop();
					this.quoteFlags.add(true);
				} else {
					this.quoteFlags.add(false);
				}
			} else {
				if (quotes.isEmpty()) {
					this.quoteFlags.add(false);
				} else {
					this.quoteFlags.add(true);
				}
			}
		}		
		if (quotes.size() != 0) {
			error();
		}
	}
}