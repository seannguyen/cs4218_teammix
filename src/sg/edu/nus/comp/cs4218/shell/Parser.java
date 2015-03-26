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

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Configurations;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public class Parser {
	Vector<Boolean> quoteFlags = new Vector<Boolean>();

	public Command parseCommandLine(String commandLine) throws ShellException,
			AbstractApplicationException {
		refreshParser();
		Vector<String> preprocessedLine = splitLine(commandLine);
		return parseSequence(preprocessedLine);
	}

	// PROTECTED CORE METHODS
	protected Vector<String> splitLine(Vector<String> input) throws ShellException {
		Vector<String> result = new Vector<String>();
		for (int i = 0; i < input.size(); i++) {
			result.addAll(splitLine(input.get(i)));
		}
		return result;
	}
	
	protected Vector<String> splitLine(String input) throws ShellException {
		Vector<String> result = new Vector<String>();
		if (input == null || input.length() == 0) {
			return result;
		}
		markQuotes(input);
		int lastStop = -1;
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == Configurations.SPACE_CHAR
					|| input.charAt(i) == Configurations.TAB_CHAR) {
				if (this.quoteFlags.get(i)) {
					continue;
				}
				String element = input.substring(lastStop + 1, i);
				result.add(element);
				lastStop = i;
			} else if (input.charAt(i) == Configurations.PIPE_TOKEN.charAt(0)
					|| input.charAt(i) == Configurations.SEMICOLON_TOKEN.charAt(0)
					|| input.charAt(i) == Configurations.IN_REIO_TOKEN.charAt(0)
					|| input.charAt(i) == Configurations.OUT_REIO_TOKEN.charAt(0)) {
				if (this.quoteFlags.get(i)) {
					continue;
				}
				String element = input.substring(lastStop + 1, i);
				String token = input.substring(i, i + 1);
				result.add(element);
				result.add(token);
				lastStop = i;
			} else if (i < input.length() - Configurations.NEWLINE.length()) {
				if (this.quoteFlags.get(i)) {
					continue;
				}
				String consideredText = input.substring(i);
				if (consideredText.startsWith(Configurations.NEWLINE)) {
					String element = input.substring(lastStop + 1, i);
					result.add(element);
					i += Configurations.NEWLINE.length() - 1;
					lastStop = i;
				}
			} else if (i == input.length() - 1) {
				String element = input.substring(lastStop + 1, i + 1);
				result.add(element);
			}
		}

		// remove empty elements
		for (int i = 0; i < result.size(); i++) {
			if (result == null || result.get(i).length() == 0) {
				result.remove(i);
				i--;
			}
		}
		return result;
	}

	protected CallCommand parseCall(Vector<String> callLine)
			throws ShellException, AbstractApplicationException, IOException {
		if (callLine.isEmpty()) {
			return new CallCommand(null, null, null, null);
		}
		Vector<String> elements = callLine;
		// process IO redirection first
		Vector<String> ioRedirectories = getIoRedirectories(elements);
		if (elements.isEmpty()) {
			return new CallCommand(null, null, null, null);
		}

		// then process command substitution and globing
		elements = substituteCommand(elements);
		Vector<String> namePart = new Vector<String>();
		namePart.add(elements.get(0));
		elements.remove(0);
		namePart = removeQuoteTokens(namePart);
		namePart = splitLine(namePart.firstElement());
		
		namePart = getFilesByGlob(namePart);
		String appName = namePart.get(0);
		appName = appName.toLowerCase();
		namePart.remove(0);

		elements = splitLine(elements);
		elements = removeQuoteTokens(elements);
		Vector<String> args = new Vector<String>();
		if (!appName.equals(Configurations.APPNAME_FIND)) {
			elements = getFilesByGlob(elements);
		}
		args.addAll(namePart);
		args.addAll(elements);
		CallCommand command = new CallCommand(appName,
				ioRedirectories.get(0), ioRedirectories.get(1), args);
		return command;
	}

	protected Vector<String> substituteCommand(Vector<String> input)
			throws ShellException, AbstractApplicationException {
		Vector<String> result = new Vector<String>();
		if (input == null) {
			return result;
		}

		for (String element : input) {
			Stack<Character> quotes = new Stack<Character>();
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
							String subCommandLine = element.substring(i + 1,
									lastQuotePos);
							Command subCmd = parseCommandLine(subCommandLine);
							ByteArrayOutputStream outStream = new ByteArrayOutputStream();
							subCmd.evaluate(null, outStream);
							String evaluatedResult = outStream.toString();
							String firstHalf = element.substring(0, i);
							String secondHalf = element
									.substring(lastQuotePos + 1);
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

	protected Vector<String> getFilesByGlob(Vector<String> input)
			throws IOException {
		for (int i = 0; i < input.size(); i++) {
			final Vector<String> results = new Vector<String>();
			String root = "." + File.separator;
			Path startDir = Paths.get(root);
			FileSystem fileSystem = FileSystems.getDefault();
			String globPattern = "glob:" + root + input.get(i);
			if (System.getProperty("file.separator") != null
					&& System.getProperty("file.separator").equals(
							Configurations.W_FILESEPARATOR)) {
				globPattern = globPattern.replace("\\", "\\\\");
			}
			final PathMatcher matcher = fileSystem.getPathMatcher(globPattern);

			FileVisitor<Path> matcherVisitor = new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attribs) {
					return checkFileName(file);
				}

				@Override
				public FileVisitResult preVisitDirectory(Path file,
						BasicFileAttributes attribs) {
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
				int originalLevelDeep = countOccurrences(root + input.get(i),
						File.separator);
				int resultLevelDeep = countOccurrences(results.get(j),
						File.separator);
				if (originalLevelDeep != resultLevelDeep) {
					results.remove(j);
					j--;
				}
			}
			if (!results.isEmpty()) {
				input.remove(i);
				input.addAll(results);
				i += results.size() - 1;
			}
		}
		return input;
	}

	protected Vector<String> getIoRedirectories(Vector<String> input)
			throws ShellException, AbstractApplicationException {
		String inputRedirectory = "", outputRedirectory = "";
		for (int i = 0; i < input.size(); i++) {
			if (input.get(i).equals((Configurations.IN_REIO_TOKEN))) {
				if (inputRedirectory.length() > 0) {
					error();
				}
				if (i == input.size() - 1) {
					input.remove(i);
					continue;
				}
				inputRedirectory = input.get(i + 1);
				input.remove(i);
				input.remove(i);
				inputRedirectory = substituteCommand(inputRedirectory);
				i --;
			} else if (input.get(i).equals((Configurations.OUT_REIO_TOKEN))) {
				if (outputRedirectory.length() > 0) {
					error();
				}
				if (i == input.size() - 1) {
					input.remove(i);
					continue;
				}
				outputRedirectory = input.get(i + 1);
				input.remove(i);
				input.remove(i);
				outputRedirectory = substituteCommand(outputRedirectory);
				i --;
			}
		}
		Vector<String> result = new Vector<String>();
		result.add(inputRedirectory);
		result.add(outputRedirectory);
		result = removeQuoteTokens(result);
		return result;
	}

	protected Vector<String> removeQuoteTokens(Vector<String> input)
			throws ShellException {
		Vector<String> result = new Vector<String>();
		for (String element : input) {
			Stack<Character> quotes = new Stack<Character>();
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

	// PRIVATE HELPER METHODS (This methods are trivial, so no need test cases
	// for this)

	private SequenceCommand parseSequence(Vector<String> input)
			throws ShellException, AbstractApplicationException {
		SequenceCommand seqCommand = new SequenceCommand();
		Vector<Vector<String>> pipes = splitByToken(input,
				Configurations.SEMICOLON_TOKEN);
		for (Vector<String> pipe : pipes) {
			PipeCommand pipeCmd = parsePipe(pipe);
			seqCommand.addCommand(pipeCmd);
		}
		return seqCommand;
	}

	private PipeCommand parsePipe(Vector<String> input) throws ShellException,
			AbstractApplicationException {
		PipeCommand pipeCommand = new PipeCommand();
		Vector<Vector<String>> calls = splitByToken(input,
				Configurations.PIPE_TOKEN);
		for (Vector<String> call : calls) {
			try {
				CallCommand callCmd = parseCall(call);
				pipeCommand.addCommand(callCmd);
			} catch (IOException e) {
				throw new ShellException(Configurations.MESSAGE_E_PARSING);
			}
		}
		return pipeCommand;
	}

	private void error() throws ShellException {
		throw new ShellException(Configurations.MESSAGE_E_PARSING);
	}

	private boolean isQuote(char token) {
		if (token == Configurations.QUOTE_SINGLE
				|| token == Configurations.QUOTE_DOUBLE
				|| token == Configurations.QUOTE_BACK) {
			return true;
		}
		return false;
	}

	private int countOccurrences(String input, String token) {
		int counter = 0;
		for (int i = 0; i < input.length(); i++) {
			if (String.valueOf(input.charAt(i)).equals(token)) {
				counter++;
			}
		}
		return counter;
	}

	private void refreshParser() {
		this.quoteFlags = new Vector<Boolean>();
	}

	private Vector<Vector<String>> splitByToken(Vector<String> input,
			String token) {
		Vector<Vector<String>> result = new Vector<Vector<String>>();
		int lastToken = -1;
		for (int i = 0; i < input.size(); i++) {
			if (input.get(i).equals(token)) {
				Vector<String> subList = new Vector<String>();
				if (lastToken >= 0) {
					subList = new Vector<String>(
							input.subList(lastToken + 1, i));
				} else {
					subList = new Vector<String>(input.subList(0, i));
				}
				lastToken = i;
				result.add(subList);
			} else if (i == input.size() - 1) {
				Vector<String> subList;
				subList = new Vector<String>(input.subList(lastToken + 1,
						input.size()));
				result.add(subList);
			}
		}
		return result;
	}

	private String substituteCommand(String input) throws ShellException,
			AbstractApplicationException {
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
		StringBuilder stringBuilder = new StringBuilder(input);
		stringBuilder.deleteCharAt(index);
		return stringBuilder.toString();
	}

	private void markQuotes(String input) throws ShellException {
		Stack<Character> quotes = new Stack<Character>();
		for (int i = 0; i < input.length(); i++) {
			if (isQuote(input.charAt(i))) {
				if (quotes.isEmpty()) {
					quotes.push(input.charAt(i));
					this.quoteFlags.add(true);
				} else if (quotes.peek() == Configurations.QUOTE_DOUBLE
						&& input.charAt(i) == Configurations.QUOTE_BACK) {
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