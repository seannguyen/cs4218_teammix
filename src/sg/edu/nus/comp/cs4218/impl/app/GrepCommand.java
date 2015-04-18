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
import sg.edu.nus.comp.cs4218.exception.GrepException;

public class GrepCommand implements Application{
	/**
	 * Perform grep command
	 *
	 * @param args
	 *            input arguments
	 * @param stdin
	 *            inputStream
	 * @param stdout
	 *            outputStream
	 */	
	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws GrepException {
		String absFileName = "";
		String fileName = "";
		String pattern = "";
		
		if(args.length == 0) {
			throw new GrepException("No argument.");
		} else if(args.length == 1) {
			pattern = args[0];
			if(pattern.equals("")) {
				throw new GrepException("No pattern.");
			}
			processStdin(pattern, stdin, stdout);
		} else {
			pattern = args[0];
			if(pattern.equals("")) {
				throw new GrepException("No pattern.");
			}
			int numOfFiles = args.length - 1;
			if(numOfFiles == 1) {
				fileName = args[1];
				absFileName = getAbsolutePath(fileName);
				processFile(pattern, absFileName, fileName, stdin, stdout);
			} else {
				for(int i = 1; i < args.length; i++) {
					fileName = args[i].trim();
					System.out.println(fileName); //testing remove later
					absFileName = getAbsolutePath(fileName);			
					processFiles(pattern, absFileName, fileName, stdin, stdout);
				}
			}
		}
		
	}
	
	/**
	 * process a single file
	 *
	 * @param pattern
	 *            string of pattern
	 * @param absFileName
	 *            absolute file name
	 * @param fileName
	 *            input file name      
	 * @param stdin
	 *            inputStream
	 * @param stdout
	 *            outputStream
	 */	
	public void processFile(String pattern, String absFileName, String fileName, InputStream stdin, OutputStream stdout) throws GrepException {				
		File file = new File(absFileName);	
		if(doesFileExist(file)) {
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
				String line;
				try {
					while((line = bufferedReader.readLine()) != null) {							
						if(line.contains(pattern)) {
							line = line + String.format("%n");						
							stdout.write(line.getBytes());						
						}
						
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else if(isDirectory(file)) {							
			throw new GrepException(fileName + ":" + " Is a directory" + Configurations.NEWLINE);
		} else {
           throw new GrepException(fileName + ":" + " No such file or directory" + Configurations.NEWLINE);
		}
	}
	
	/**
	 * process multiple files
	 *
	 * @param pattern
	 *            string of pattern
	 * @param absFileName
	 *            absolute file name
	 * @param fileName
	 *            input file name      
	 * @param stdin
	 *            inputStream
	 * @param stdout
	 *            outputStream
	 */	
	public void processFiles(String pattern, String absFileName, String fileName, InputStream stdin, OutputStream stdout) throws GrepException {				
		File file = new File(absFileName);			
		if(doesFileExist(file)) {
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
				String line;
				try {
					while((line = bufferedReader.readLine()) != null) {							
						if(line.contains(pattern)) {
							line = fileName + ":" + line + String.format("%n");						
							stdout.write(line.getBytes());						
						}
						
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else if(isDirectory(file)) {						
			//throw new GrepException(" " + fileName + ":" + " Is a directory");
			String msg = "grep: " + fileName + ": Is a directory" + Configurations.NEWLINE;
			try {
				stdout.write(msg.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
           //throw new GrepException(" " + fileName + ":" + " No such file or directory");
			String msg = "grep: " + fileName + ": No such file or directory" + Configurations.NEWLINE;
			try {
				stdout.write(msg.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void processStdin(String pattern, InputStream stdin, OutputStream stdout) throws GrepException {
		BufferedReader bufferedReader = null;
		String line;
		
		if(stdin == null) {
			throw new GrepException("Null stdin");
		}
		try { 
			bufferedReader = new BufferedReader(new InputStreamReader(stdin));
			while ((line = bufferedReader.readLine()) != null) {
				if(line.contains(pattern)) {
					line = line + String.format("%n");
					stdout.write(line.getBytes());
				}
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
}
