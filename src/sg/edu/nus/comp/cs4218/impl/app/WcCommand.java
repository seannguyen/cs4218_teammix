package sg.edu.nus.comp.cs4218.impl.app;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.WcException;

public class WcCommand implements Application {
	private String fileName;
	private int lineCount = 0;
	private int wordCount = 0;
	private int charCount = 0;
	private int totalLineCount = 0;
	private int totalWordCount = 0;
	private int totalCharCount = 0;
	private boolean lineFlag = false;
	private boolean wordFlag = false;
	private boolean charFlag = false;

	public WcCommand() {
		// This constructor is intentionally empty. Nothing special is needed
		// here.
	}

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout)
			throws WcException {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-l")) { // Print only the newline counts
				lineFlag = true;
			} else if (args[i].equals("-w")) { // Print only the word counts
				wordFlag = true;
			} else if (args[i].equals("-m")) { // Print only the character
												// counts
				charFlag = true;
			} else if (args[i].charAt(0) == '-') {
				// wc: illegal option -- z
				throw new WcException("illegal option " + args[i]);
			} else {
				int numOfFiles = args.length - i;
				String[] arrayOfFiles = new String[numOfFiles];
				System.arraycopy(args, i, arrayOfFiles, 0, numOfFiles);
				processFiles(arrayOfFiles, stdin, stdout);
				break;
			}
		}
		processStdin(stdout);
	}

	public void processFiles(String args[], InputStream stdin,
			OutputStream stdout) throws WcException {
		for (int i = 0; i < args.length; i++) {
			fileName = getAbsolutePath(args[i]);
			File file = new File(fileName);

			if (doesFileExist(file)) {
				try {
					BufferedReader bufferedReader = new BufferedReader(
							new FileReader(file));
					String line;
					while ((line = bufferedReader.readLine()) != null) {
						lineCount++;
						wordCount += line.trim().split("\\s+").length;
						charCount += line.length() + 1;
					}
					totalLineCount += lineCount;
					totalWordCount += wordCount;
					totalCharCount += charCount;
					printResults(args[i], stdout);
					resetCounters();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (isDirectory(file)) {
				// cat: sample/: Is a directory
				throw new WcException(" " + fileName + ":" + " Is a directory");
			} else {
				// cat: sample.txt: No such file or directory
				throw new WcException(" " + fileName + ":"
						+ " No such file or directory");
			}

			if (args.length > 1) {
				printTotalResults(stdout);
			}
			resetAllCounters();
		}
	}

	public void resetAllCounters() {
		lineCount = 0;
		wordCount = 0;
		charCount = 0;
		totalLineCount = 0;
		totalWordCount = 0;
		totalCharCount = 0;
		lineFlag = false;
		wordFlag = false;
		charFlag = false;
	}

	public void resetCounters() {
		lineCount = 0;
		wordCount = 0;
		charCount = 0;
	}

	public void printTotalResults(OutputStream stdout) {
		try {
			if (lineFlag == false && wordFlag == false && charFlag == false) {
				// print all in the following order: newline, word, character
				stdout.write(String.valueOf(totalLineCount).getBytes());
				stdout.write("\t".getBytes());
				stdout.write(String.valueOf(totalWordCount).getBytes());
				stdout.write("\t".getBytes());
				stdout.write(String.valueOf(totalCharCount).getBytes());
				stdout.write("\t".getBytes());
			}

			if (lineFlag) {
				stdout.write(String.valueOf(totalLineCount).getBytes());
				stdout.write("\t".getBytes());
			}

			if (wordFlag) {
				stdout.write(String.valueOf(totalWordCount).getBytes());
				stdout.write("\t".getBytes());
			}

			if (charFlag) {
				stdout.write(String.valueOf(totalCharCount).getBytes());
				stdout.write("\t".getBytes());
			}

			stdout.write("Total".getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void printResults(String fileName, OutputStream stdout) {
		try {
			if (lineFlag == false && wordFlag == false && charFlag == false) {
				// print all in the following order: newline, word, character
				stdout.write(String.valueOf(lineCount).getBytes());
				stdout.write("\t".getBytes());
				stdout.write(String.valueOf(wordCount).getBytes());
				stdout.write("\t".getBytes());
				stdout.write(String.valueOf(charCount).getBytes());
				stdout.write("\t".getBytes());
			}

			if (lineFlag) {
				stdout.write(String.valueOf(lineCount).getBytes());
				stdout.write("\t".getBytes());
			}

			if (wordFlag) {
				stdout.write(String.valueOf(wordCount).getBytes());
				stdout.write("\t".getBytes());
			}

			if (charFlag) {
				stdout.write(String.valueOf(charCount).getBytes());
				stdout.write("\t".getBytes());
			}

			stdout.write(fileName.getBytes());
			stdout.write(String.format("%n").getBytes());			
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}

	public String getAbsolutePath(String filePath) {
		if (filePath.startsWith(Environment.currentDirectory)) {
			return filePath;
		}
		return Environment.currentDirectory + File.separator + filePath;
	}

	public boolean doesFileExist(File file) {
		if (file.exists() && !file.isDirectory()) {
			return true;
		}
		return false;
	}

	public boolean isDirectory(File file) {
		if (file.isDirectory()) {
			return true;
		}
		return false;
	}

	public void processStdin(OutputStream stdout) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {
			br = new BufferedReader(new InputStreamReader(System.in));
			while ((line = br.readLine()) != null) {
				// sb.append(line);
				lineCount++;
				wordCount += line.trim().split("\\s+").length;
				charCount += line.length() + 1;
			}
			printResults("", stdout);
			resetCounters();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
