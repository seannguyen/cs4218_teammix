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
		} catch (ShellException e) {
			System.out.println(e.getMessage());
		} catch (AbstractApplicationException e)  {
			System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args) throws AbstractApplicationException, ShellException {
		System.out.println(Configurations.MESSAGE_WELCOME);
		//initialize apps in environment
		Environment.nameAppMaps.put(Configurations.APPNAME_CD, new CdCommand(Environment.currentDirectory));
		Environment.nameAppMaps.put(Configurations.APPNAME_LS, new LsCommand(Environment.currentDirectory));
		Environment.nameAppMaps.put(Configurations.APPNAME_ECHO, new Echo());
		Environment.nameAppMaps.put(Configurations.APPNAME_CAT, new Cat());
		Environment.nameAppMaps.put(Configurations.APPNAME_HEAD, new Head());
		Environment.nameAppMaps.put(Configurations.APPNAME_TAIL, new Tail());
		Environment.nameAppMaps.put(Configurations.APPNAME_PWD, new PwdCommand(Environment.currentDirectory));
		//setup shell
		Scanner input = new Scanner(System.in);
		Shell shell = new SimpleShell();
		while (Environment.running) {
			System.out.printf(Environment.currentDirectory + Configurations.MESSAGE_PROMPT);
			String line  = input.nextLine();
			shell.parseAndEvaluate(line, System.out);
		}
		input.close();
	}
}
