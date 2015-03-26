package sg.edu.nus.comp.cs4218.impl.app;

import java.io.BufferedReader;
import java.io.File;
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
	protected int lineCount = 0;
	protected int wordCount = 0;
	protected int charCount = 0;
	protected int totalLineCount = 0;
	protected int totalWordCount = 0;
	protected int totalCharCount = 0;
	protected boolean lineFlag = false;
	protected boolean wordFlag = false;
	protected boolean charFlag = false;
	private boolean argFlag = false;

	/**
	 * Perform Wc command
	 *
	 * @param args
	 *            input arguments
	 * @param stdin
	 *            inputStream
	 * @param stdout
	 *            outputStream
	 * @throws WcException 
	 */	
	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout)
			throws WcException {
		if(args.length == 0) {
			processInputStream(stdin, stdout);
			resetAllCounters();
			return;
		}
		for (int i = 0; i < args.length; i++) {
			if(args[i].equals("")) {
				resetAllCounters();
				throw new WcException("Null argument(s)");
			}
			if (args[i].equals("-l")) { // Print only the newline counts
				lineFlag = true;
			} else if (args[i].equals("-w")) { // Print only the word counts
				wordFlag = true;
			} else if (args[i].equals("-m")) { // Print only the character
												// counts
				charFlag = true;
			} else if (args[i].charAt(0) == '-') {
				// wc: illegal option -- z
				resetAllCounters();
				throw new WcException("illegal option " + args[i]);
			} else {			
				argFlag = true;
				int numOfFiles = args.length - i;
				String[] arrayOfFiles = new String[numOfFiles];
				System.arraycopy(args, i, arrayOfFiles, 0, numOfFiles);
				processFiles(arrayOfFiles, stdin, stdout);
				break;
			}
		}
		if (!argFlag) {
			//resetAllCounters();
			//throw new WcException("Null stdin");
			processInputStream(stdin, stdout);
			resetAllCounters();
			return;
		}
	}

	/**
	 * Write to outputStream the lines(numOfLines) starting from the bottom of Arraylist.
	 *
	 * @param args
	 *            args storing file names
	 * @param stdin
	 * 			  inputStream
	 * @param stdout
	 * 			  outputStream     
	 *  
	 */
	public void processFiles(String args[], InputStream stdin,
			OutputStream stdout) throws WcException {
		if(args.length == 0) {
			resetAllCounters();
			throw new WcException("No argument(s)");
		}
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
				resetAllCounters();
				throw new WcException(" " + fileName + ":" + " Is a directory");
			} else {
				// cat: sample.txt: No such file or directory
				resetAllCounters();
				throw new WcException(" " + fileName + ":"
						+ " No such file or directory");
			}			
		}
		if (args.length > 1) {
			printTotalResults(stdout);
		}
		resetAllCounters();
	}

	/**
	 * Reset all counters to zero
	 */
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

	/**
	 * Reset line, word and char counters to zero
	 */
	public void resetCounters() {
		lineCount = 0;
		wordCount = 0;
		charCount = 0;
	}

	/**
	 * Print Totalresults to stdout
	 * 
	 * @param stdout
	 * 			outputStream
	 */
	public void printTotalResults(OutputStream stdout) {
		try {
			if (!lineFlag && !wordFlag && !charFlag) {
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

			stdout.write("total".getBytes());
		} catch (IOException e) {
			resetAllCounters();
			e.printStackTrace();
		}
	}

	/**
	 * Print Results to stdout
	 * 
	 * @param file
	 * 			name of file
	 * @param stdout
	 * 			outputStream
	 */
	public void printResults(String fileName, OutputStream stdout) {
		try {
			if (!lineFlag && !wordFlag && !charFlag) {
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
			resetAllCounters();
			e.printStackTrace();
		}
	}

	/**
	 * Get absolute path of given filePath
	 *
	 * @param filePath
	 *            filePath to get absolute
	 */
	public String getAbsolutePath(String filePath) {
		if (filePath.startsWith(Environment.currentDirectory)) {
			return filePath;
		}
		return Environment.currentDirectory + File.separator + filePath;
	}

	/**
	 * Checks if given file exist
	 *
	 * @param file
	 *            file to be checked
	 */
	public boolean doesFileExist(File file) {
		if (file.exists() && !file.isDirectory()) {
			return true;
		}
		return false;
	}

	/**
	 * Check if given file is a directory
	 *
	 * @param file
	 *            file to be check for directory
	 */
	public boolean isDirectory(File file) {
		if (file.isDirectory()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Print stdin to stdout
	 * 
	 * @param stdin
	 * 			InputStream
	 * @param stdout
	 * 			OutputStream
	 * @throws WcException 
	 */
	public void processInputStream(InputStream stdin, OutputStream stdout) throws WcException {		 			
		BufferedReader bufferedReader = null;
		String line;
		
		if(stdin == null) {
			throw new WcException("Null stdin");
		}
		try { 
			bufferedReader = new BufferedReader(new InputStreamReader(stdin));
			while ((line = bufferedReader.readLine()) != null) {
				lineCount++;
				wordCount += line.trim().split("\\s+").length;
				charCount += line.length() + 1;
			} 
			printResults("", stdout);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return;
	}
}
