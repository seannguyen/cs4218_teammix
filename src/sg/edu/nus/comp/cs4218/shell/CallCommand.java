package sg.edu.nus.comp.cs4218.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Configurations;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public class CallCommand implements Command {

	//attributes
	private final String appName;
	private final Vector<String> arguments;
	private final String inputFile;
	private final String outputFile;
	private InputStream inputStream;
	private OutputStream outputStream;
	
	//constructor
	public CallCommand (String appName, String inputFile, String outputFile, Vector<String> arguments) {
		this.appName = appName;
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.arguments = arguments;
	}
	
	//public methods
	@Override
	public void evaluate(InputStream stdin, OutputStream stdout) throws AbstractApplicationException, ShellException {
		Application app = Environment.nameAppMaps.get(appName);
		if (app == null) {
			throw new ShellException(Configurations.MESSAGE_E_MISSA);
		}
		try {
			initIoStreams(stdin, stdout);
			String[] args = new String[arguments.size()];
			arguments.toArray(args);
			app.run(args, this.inputStream, this.outputStream);
		} catch (Exception e) {
			terminate();
			throw e;
		}
		terminate();
	}

	@Override
	public void terminate() {
		//close IO streams
		try {
			if (this.inputStream != null && this.inputFile != null && this.inputFile.length() > 0) {
				this.inputStream.close();
			}
			if (this.outputStream != null && this.outputStream != null && this.outputFile.length() > 0) {
				this.outputStream.close();
			}
		} catch (IOException e) {
		}
	}
	
	public String getAppName() {
		return this.appName;
	}
	
	public Vector<String> getArgs() {
		return this.arguments;
	}
	
	public String getInputFile() {
		return this.inputFile;
	}
	
	public String getOutputFile() {
		return this.outputFile;
	}
	
	//private helper methods

	private void initIoStreams(InputStream stdin, OutputStream stdout) throws ShellException {
		try {
			if (inputFile != null && inputFile.length() > 0) {
				this.inputStream = getInputStream(inputFile);
			} else {
				this.inputStream = stdin;
			}
			if (outputFile != null && outputFile.length() > 0) {
				this.outputStream = getOutputStream(outputFile);
			} else {
				this.outputStream = stdout;
			}
		} catch (Exception e) {
			error(Configurations.MESSGE_E_MISSF + e.getMessage());
		}
	}

	private InputStream getInputStream(String fileName) throws ShellException, FileNotFoundException {
			return new FileInputStream(Environment.currentDirectory + File.separator + fileName);
	}
	
	private OutputStream getOutputStream(String fileName) throws ShellException, FileNotFoundException {
		return new FileOutputStream(Environment.currentDirectory + File.separator + fileName);
	}

	private void error(String message) throws ShellException {
		throw new ShellException(message);
	}
}
