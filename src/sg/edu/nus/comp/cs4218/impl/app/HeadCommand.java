package sg.edu.nus.comp.cs4218.impl.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Configurations;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.exception.HeadException;
import sg.edu.nus.comp.cs4218.exception.WcException;

public class HeadCommand implements Application{
	private boolean singleFileFlag = false;
	private String ERROR_MSG_DIRECTORY = "%1$s%2$s: Is a directory" + Configurations.NEWLINE;
	private String ERROR_MSG = "%1$s%2$s: No such file or directory" + Configurations.NEWLINE;
	public static final int DEFAULT_NUM_OF_LINES = 10;	
	
	/**
	 * Perform Head command
	 *
	 * @param args
	 *            input arguments
	 * @param stdin
	 *            inputStream
	 * @param stdout
	 *            outputStream
	 */	
	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws HeadException {
		String fileName = "";
		int numOfLines = 0;
		int numOfFiles = 0;
		int index = 0;
		singleFileFlag = false;

		if(args.length > 3 && args[0].equals("-n")) {
			try {				
				numOfLines = Integer.parseInt(args[1]);
				if(numOfLines < 0) {
					//illegal line count -- numOfLines
					throw new HeadException("illegal line count -- " + numOfLines);
				}								
			} catch(NumberFormatException e) {
				e.printStackTrace();
			}
			//fileName = args[2];
			numOfFiles = args.length - 2;
			index = 2;
		} else if (args.length == 3 && args[0].equals("-n")) {	
			singleFileFlag = true;
			try {
				numOfFiles = 1;
				index = 2;
				numOfLines = Integer.parseInt(args[1]);
				if(numOfLines < 0) {
					//illegal line count -- numOfLines
					throw new HeadException("illegal line count -- " + numOfLines);
				}								
			} catch(NumberFormatException e) {
				e.printStackTrace();
			}			
		} else if (args.length == 1 && !args[0].equals("-n") ) {
			singleFileFlag = true;
			numOfLines = DEFAULT_NUM_OF_LINES;			
			numOfFiles = 1;
			index = 0;
		} else if(args.length > 0 && !args[0].equals("-n")) {			
			index = 0;
			numOfFiles = args.length;
			numOfLines = DEFAULT_NUM_OF_LINES;
		} else {
			//throw new HeadException("Incorrect argument(s)");
			if(args.length == 2 && args[0].equals("-n")) {
				numOfLines = Integer.parseInt(args[1]);
				if(numOfLines < 0) {
					//illegal line count -- numOfLines
					throw new HeadException("illegal line count -- " + numOfLines);
				}
				processInputStream(numOfLines, stdin, stdout);
			} else {
				processInputStream(DEFAULT_NUM_OF_LINES, stdin, stdout);
			}
		}
		
		String[] arrayOfFiles = new String[numOfFiles];
		System.arraycopy(args, index, arrayOfFiles, 0, numOfFiles);
		for(int i = 0; i < numOfFiles; i++) {
			processFiles(stdout, arrayOfFiles[i], numOfLines);
		}
	}
	
	public void processFiles(OutputStream stdout, String fileName,
			int numOfLines) throws HeadException {
		fileName = getAbsolutePath(fileName);
		File file = new File(fileName);

		if (doesFileExist(file)) {
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
				String line;
				try {
					if(!singleFileFlag) {
						String title = "==>" + fileName + "<==" + Configurations.NEWLINE;
						stdout.write(title.getBytes());
					}
					while ((line = bufferedReader.readLine()) != null && numOfLines-- > 0) {
						String newLine = line + String.format("%n");
						stdout.write(newLine.getBytes());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else if (isDirectory(file)) {
			// head: sample/: Is a directory
			printExceptions(ERROR_MSG_DIRECTORY, fileName, stdout);
		} else {
			// head: sample.txt: No such file or directory
			printExceptions(ERROR_MSG, fileName, stdout);
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
	 * throw HeadException
	 */
	public void printExceptions(String msg, String fileName, OutputStream stdout) throws HeadException {
		if(singleFileFlag) {			
			throw new HeadException(String.format(msg, "", fileName + ":"));
		} else {
			String errorMsg = String.format(msg, "head: ", fileName + ":");
			try {
				stdout.write(errorMsg.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Print stdin to stdout
	 * 
	 * @param stdin
	 * 			InputStream
	 * @param stdout
	 * 			OutputStream
	 * @throws HeadException 
	 */
	public void processInputStream(int numOfLines, InputStream stdin, OutputStream stdout) throws HeadException {		 			
		BufferedReader bufferedReader = null;
		String line;
		
		if(stdin == null) {
			throw new HeadException("Null stdin");
		}
		try { 
			bufferedReader = new BufferedReader(new InputStreamReader(stdin));
			while ((line = bufferedReader.readLine()) != null && numOfLines-- > 0) {
				String newLine = line + String.format("%n");
				stdout.write(newLine.getBytes());
			} 
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
