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

public class CatCommand implements Application{
	private String ERROR_MSG_DIRECTORY = "%1$s%2$s: Is a directory" + Configurations.NEWLINE;
	private String ERROR_MSG = "%1$s%2$s: No such file or directory" + Configurations.NEWLINE;
	boolean singleFileFlag = false;
	/**
	 * Perform cat command
	 *
	 * @param args
	 *            input arguments
	 * @param stdin
	 *            inputStream
	 * @param stdout
	 *            outputStream
	 */	
	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws CatException {
		String fileName = "";		
		singleFileFlag = false;
		
		if(args.length == 0) {
			//throw new CatException(" " + fileName + ":" + " No argument(s)");
			processInputStream(stdin, stdout);
			return;
		}
		
		if(args.length == 1) {
			singleFileFlag = true;
		}
	
		for(int i = 0; i < args.length; i++) {
			fileName = getAbsolutePath(args[i]);			
			File file = new File(fileName);
			
			if(args[i].equals("")) {
				throw new CatException(" " + fileName + ":" + " Null argument(s)");
			}
			
			if(doesFileExist(file)) {
				try {
					BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
					String line;
					try {						
						while((line = bufferedReader.readLine()) != null) {	
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
				// cat: sample/: Is a directory
				printExceptions(ERROR_MSG_DIRECTORY, file.getName(), stdout);
			} else {
				// cat: sample.txt: No such file or directory	
				printExceptions(ERROR_MSG, file.getName(), stdout);				
			}
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
	 * throw WcException
	 */
	public void printExceptions(String msg, String fileName, OutputStream stdout) throws CatException {
		if(singleFileFlag) {			
			throw new CatException(String.format(msg, "", fileName + ":"));
		} else {
			String errorMsg = String.format(msg, "cat: ", fileName + ":");
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
	 * @throws CatException 
	 */
	public void processInputStream(InputStream stdin, OutputStream stdout) throws CatException {		 			
		BufferedReader bufferedReader = null;
		String line;
		
		if(stdin == null) {
			throw new CatException("Null stdin");
		}
		try { 
			bufferedReader = new BufferedReader(new InputStreamReader(stdin));
			while ((line = bufferedReader.readLine()) != null) {
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
