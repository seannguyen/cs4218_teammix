package sg.edu.nus.comp.cs4218.impl.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.TailException;

public class TailCommand implements Application{
	public static final int DEFAULT_DISPLAY_LINES = 10;
	
	public TailCommand() {
		// This constructor is intentionally empty. Nothing special is needed here.
	}
	
	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws TailException {
		ArrayList<String> listOfLines = new ArrayList<String>();		
		String fileName = "";
		int numOfLines = 0;

		if (args.length == 3 && args[0].equals("-n")) {			
			try {
				numOfLines = Integer.parseInt(args[1]);
			} catch(NumberFormatException e) {
				e.printStackTrace();
			}
			fileName = args[2];
		} else if (args.length == 1) {
			numOfLines = DEFAULT_DISPLAY_LINES;
			fileName = args[0];
		} else {
			throw new TailException("Incorrect arguements");
		}
		
		fileName = getAbsolutePath(fileName);
		File file = new File(fileName);

		if (doesFileExist(file)) {
			listOfLines = addLinesToArrayListFromFile(listOfLines, numOfLines, file);
			try {
				outputLines(stdout, listOfLines, numOfLines);
			} catch (IOException e) {				
				e.printStackTrace();
			}
		} else if (isDirectory(file)) {
			// head: sample/: Is a directory
			throw new TailException(" " + fileName + ":" + " Is a directory");
		} else {
			// head: sample.txt: No such file or directory
			throw new TailException(" " + fileName + ":" + " No such file or directory");
		}
	}

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
	
	public String getAbsolutePath(String filePath) {				
		if(filePath.startsWith(Environment.currentDirectory)) {
			return filePath;
		}
		return Environment.currentDirectory + File.separator + filePath;
	}
	
	public boolean doesFileExist(File file) {
		if(file.exists() && !file.isDirectory()) {
			return true;
		}
		return false;
	}
	
	public boolean isDirectory(File file) {
		if(file.isDirectory()) {
			return true;
		}
		return false;
	}
}