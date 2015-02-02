package sg.edu.nus.comp.cs4218.shell;

import java.io.OutputStream;
import java.util.Scanner;

import sg.edu.nus.comp.cs4218.Configurations;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.app.*;

public class SimpleShell implements Shell {
	//public methods
	@Override
	public void parseAndEvaluate(String cmdline, OutputStream stdout)
			throws AbstractApplicationException, ShellException {
		try {
			Parser parser = new Parser();
			Command command = parser.parseCommandLine(cmdline);
			command.evaluate(null, stdout);
		} catch (ShellException shellError) {
			System.out.println(shellError.getMessage());
		} catch (AbstractApplicationException appError) {
			System.out.println(appError.getMessage());
		}
	}

	public static void main(String[] args) throws AbstractApplicationException, ShellException {
		System.out.println(Configurations.MESSAGE_WELCOME);
		//initialize apps in environment
		Environment.nameAppMaps.put(Configurations.APPNAME_CD, new Cd(Environment.currentDirectory));
		Environment.nameAppMaps.put(Configurations.APPNAME_LS, new Ls(Environment.currentDirectory));
		Environment.nameAppMaps.put(Configurations.APPNAME_ECHO, new Echo());
		Environment.nameAppMaps.put(Configurations.APPNAME_PWD, new Pwd(Environment.currentDirectory));
		//setup shell
		Scanner input = new Scanner(System.in);
		Shell shell = new SimpleShell();
		while (Environment.running) {
			//System.out.printf(Configurations.MESSAGE_PROMPT);
			System.out.printf(Environment.currentDirectory + ">");
			String line  = input.nextLine();
			shell.parseAndEvaluate(line, System.out);
			System.out.println();
		}
		input.close();
	}
}
