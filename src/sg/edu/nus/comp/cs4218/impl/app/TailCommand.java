package sg.edu.nus.comp.cs4218.impl.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Configurations;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.HeadException;
import sg.edu.nus.comp.cs4218.exception.TailException;

public class TailCommand implements Application{
	private boolean singleFileFlag = false;
	private String ERROR_MSG_DIRECTORY = "%1$s%2$s: Is a directory" + Configurations.NEWLINE;
	private String ERROR_MSG = "%1$s%2$s: No such file or directory" + Configurations.NEWLINE;
	public static final int DEFAULT_DISPLAY_LINES = 10;
	
	/**
	 * Perform Tail command
	 *
	 * @param args
	 *            input arguments
	 * @param stdin
	 *            inputStream
	 * @param stdout
	 *            outputStream
	 */	
	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws TailException {
		ArrayList<String> listOfLines = new ArrayList<String>();		
		String fileName = "";
		int numOfLines = 0;
		int numOfFiles = 0;
		int index = 0;
		singleFileFlag = false;

		if(args.length > 0 && args[0].startsWith("-") && !args[0].equals("-n")) {
			throw new TailException("illegal option -- " + args[0]);
		}
		
		if(args.length > 3 && args[0].equals("-n")) {
			try {
				numOfLines = Integer.parseInt(args[1]);
				if(numOfLines < 0) {
					throw new TailException("illegal line count -- " + numOfLines);
				}
			} catch(NumberFormatException e) {
				//e.printStackTrace();
				throw new TailException("illegal line count -- " + args[1]);
			}
			numOfFiles = args.length - 2;
			index = 2;
		} else if (args.length == 3 && args[0].equals("-n")) {			
			singleFileFlag = true;
			try {
				numOfFiles = 1;
				index = 2;
				numOfLines = Integer.parseInt(args[1]);
				if(numOfLines < 0) {
					throw new TailException("illegal line count -- " + numOfLines);
				}
			} catch(NumberFormatException e) {
				//e.printStackTrace();
				throw new TailException("illegal line count -- " + args[1]);
			}
		} else if (args.length == 1 && !args[0].equals("-n")) {
			singleFileFlag = true;
			numOfFiles = 1;			
			numOfLines = DEFAULT_DISPLAY_LINES;
			index = 0;			
		} else if(args.length > 0 && !args[0].equals("-n")) { 
			index = 0;
			numOfFiles = args.length;
			numOfLines = DEFAULT_DISPLAY_LINES;
		}else {
			//throw new TailException("Incorrect argument(s)");
			if(args.length == 2 && args[0].equals("-n")) {
				numOfLines = Integer.parseInt(args[1]);
				if(numOfLines < 0) {
					//illegal line count -- numOfLines
					throw new TailException("illegal line count -- " + numOfLines);
				}
				processInputStream(listOfLines, numOfLines, stdin, stdout);
			} else {
				processInputStream(listOfLines, DEFAULT_DISPLAY_LINES, stdin, stdout);
			}
		}
		/*
		if(fileName.equals("")) {
			throw new TailException("Null argument(s)");
		}
		*/
		String[] arrayOfFiles = new String[numOfFiles];
		System.arraycopy(args, index, arrayOfFiles, 0, numOfFiles);
		for(int i = 0; i < numOfFiles; i++) { 
			processFiles(stdout, listOfLines, arrayOfFiles[i], numOfLines);
		}
	}
	
	/**
	 * Print stdin to stdout
	 * 
	 * @param stdin
	 * 			InputStream
	 * @param stdout
	 * 			OutputStream
	 * @throws TailException 
	 */
	public void processInputStream(ArrayList<String> listOfLines, int numOfLines, InputStream stdin, OutputStream stdout) throws TailException {		 			
		BufferedReader bufferedReader = null;
		String line;
		
		if(stdin == null) {
			throw new TailException("Null stdin");
		}
		try { 			
			listOfLines = addLinesToArrayListFromInputStream(listOfLines, numOfLines, stdin);
			outputLines(stdout, listOfLines, numOfLines);			
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
	
	private ArrayList<String> addLinesToArrayListFromInputStream(			
			ArrayList<String> listOfLines, int numOfLines, InputStream stdin) {
		BufferedReader bufferedReader = null;
		bufferedReader = new BufferedReader(new InputStreamReader(stdin));
		String line;
		try {
			while ((line = bufferedReader.readLine()) != null) {						
				listOfLines.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return listOfLines;
	}

	public void processFiles(OutputStream stdout,
			ArrayList<String> listOfLines, String fileName, int numOfLines)
			throws TailException {
		if(fileName.equals("")) {
			throw new TailException("Empty argument");
		}
		fileName = getAbsolutePath(fileName);
		File file = new File(fileName);

		if (doesFileExist(file)) {
			listOfLines = addLinesToArrayListFromFile(listOfLines, numOfLines, file);
			try {
				if(!singleFileFlag) {
					String title = "==>" + file.getName()+ "<==" + Configurations.NEWLINE;
					stdout.write(title.getBytes());
				}
				outputLines(stdout, listOfLines, numOfLines);
			} catch (IOException e) {				
				e.printStackTrace();
			}
		} else if (isDirectory(file)) {
			// head: sample/: Is a directory
			printExceptions(ERROR_MSG_DIRECTORY, file.getName(), stdout);
		} else {
			// head: sample.txt: No such file or directory
			printExceptions(ERROR_MSG, file.getName(), stdout);
		}
	}
	
	/**
	 * print exceptions
	 * @param msg
	 * 			error msg
	 * @param fileName
	 * 			file name of the test file
	 * @param stdout
	 * 			OutputStream
	 * 
	 * throw TailException
	 */
	public void printExceptions(String msg, String fileName, OutputStream stdout) throws TailException {
		if(singleFileFlag) {			
			throw new TailException(String.format(msg, "", fileName + ":"));
		} else {
			String errorMsg = String.format(msg, "tail: ", fileName + ":");
			try {
				stdout.write(errorMsg.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Write to outputStream the lines(numOfLines) starting from the bottom of Arraylist.
	 *
	 * @param stdout
	 *            outputStream
	 * @param listOfLines
	 * 			  lines from file
	 * @param numOfLines
	 * 			  number of lines to be displayed from bottom      
	 *  
	 */
	public void outputLines(OutputStream stdout, ArrayList<String> listOfLines, int numOfLines) throws IOException {
		int numLineToDisplay = listOfLines.size() - numOfLines;
		
		if(numLineToDisplay < 0) {
			numLineToDisplay = 0;
		}
		
		for(;numLineToDisplay < listOfLines.size(); numLineToDisplay++) {
			String newLine = listOfLines.get(numLineToDisplay) + String.format("%n");
			stdout.write(newLine.getBytes());
		}
	}

	/**
	 * Read from a given File and add each line in the file to an arraylist
	 *
	 * @param listOfLines
	 *            list to store lines from file
	 * @param numOflines
	 * 			  number of lines to add to arraylist
	 * @param file
	 * 			  file to read from      
	 *  
	 */
	public ArrayList<String> addLinesToArrayListFromFile(ArrayList<String> listOfLines, int numOfLines, File file) {
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			String line;
			try {
				while ((line = bufferedReader.readLine()) != null) {						
					listOfLines.add(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return listOfLines;
	}
	
	/**
	 * Get absolute path of given filePath
	 *
	 * @param filePath
	 *            filePath to get absolute
	 */
	public String getAbsolutePath(String filePath) {				
		if(filePath.startsWith(Environment.currentDirectory)) {
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
		if(file.exists() && !file.isDirectory()) {
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
		if(file.isDirectory()) {
			return true;
		}
		return false;
	}
}
