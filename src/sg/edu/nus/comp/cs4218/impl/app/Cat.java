package sg.edu.nus.comp.cs4218.impl.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CatException;

public class Cat implements Application{
	public Cat() {
		
	}
	
	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws CatException {
		String fileName = "";
	
		for(int i = 0; i < args.length; i++) {
			fileName = args[i];
			File file = new File(fileName);
			
			if(doesFileExist(file)) {
				try {
					BufferedReader br = new BufferedReader(new FileReader(file));
					String line;
					try {
						while((line = br.readLine()) != null) {	
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
