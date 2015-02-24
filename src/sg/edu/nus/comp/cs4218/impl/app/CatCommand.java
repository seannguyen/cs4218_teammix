package sg.edu.nus.comp.cs4218.impl.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CatException;

public class CatCommand implements Application{	
	
	public CatCommand() {
		// This constructor is intentionally empty. Nothing special is needed here.
	}
	
	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws CatException {
		String fileName = "";
		
		if(args.length == 0) {
			throw new CatException(" " + fileName + ":" + " No argument(s)");
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
			} else if(isDirectory(file)) {
				//cat: sample/: Is a directory								
				throw new CatException(" " + fileName + ":" + " Is a directory");
			} else {
				//cat: sample.txt: No such file or directory
				throw new CatException(" " + fileName + ":" + " No such file or directory");
			}
		}
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
