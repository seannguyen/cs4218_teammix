package sg.edu.nus.comp.cs4218.shell;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Configurations;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.app.CatCommand;
import sg.edu.nus.comp.cs4218.impl.app.CdCommand;
import sg.edu.nus.comp.cs4218.impl.app.EchoCommand;
import sg.edu.nus.comp.cs4218.impl.app.FindCommand;
import sg.edu.nus.comp.cs4218.impl.app.HeadCommand;
import sg.edu.nus.comp.cs4218.impl.app.LsCommand;
import sg.edu.nus.comp.cs4218.impl.app.PwdCommand;
import sg.edu.nus.comp.cs4218.impl.app.TailCommand;
import sg.edu.nus.comp.cs4218.impl.app.WcCommand;

public class CommandTest {

	private static final String ARG1 = "Arg1";
	
	private InputStream inputStream;
	private OutputStream outputStream;
	private Vector<String> args;
	
	@BeforeClass
	public static void intiEnvironment() {
		// initialize apps in environment
		Environment.nameAppMaps.put(Configurations.APPNAME_CD, new CdCommand());
		Environment.nameAppMaps.put(Configurations.APPNAME_LS, new LsCommand());
		Environment.nameAppMaps.put(Configurations.APPNAME_ECHO,
				new EchoCommand());
		Environment.nameAppMaps.put(Configurations.APPNAME_CAT,
				new CatCommand());
		Environment.nameAppMaps.put(Configurations.APPNAME_HEAD,
				new HeadCommand());
		Environment.nameAppMaps.put(Configurations.APPNAME_TAIL,
				new TailCommand());
		Environment.nameAppMaps.put(Configurations.APPNAME_PWD,
				new PwdCommand());
		Environment.nameAppMaps.put(Configurations.APPNAME_FIND,
				new FindCommand());
		Environment.nameAppMaps.put(Configurations.APPNAME_WC, new WcCommand());
	}

	@Before
	public void setUp() throws Exception {
		this.inputStream = System.in;
		this.outputStream = System.out;
		args = new Vector<String>();
	}

	@Test
	public void callCommandEvaluate() throws AbstractApplicationException,
			ShellException {
		// expect no throw
		args.add(ARG1);
		CallCommand callCmd = new CallCommand("echo", null, null, args);
		callCmd.evaluate(this.inputStream, this.outputStream);
	}

	@Test(expected = Exception.class)
	public void callCommandInvalidAppName()
			throws AbstractApplicationException, ShellException {
		args.add(ARG1);
		CallCommand callCmd = new CallCommand("invalidAppName", null, null,
				args);
		callCmd.evaluate(this.inputStream, this.outputStream);
	}

	@Test(expected = Exception.class)
	public void callCommandInvalidInputFileName()
			throws AbstractApplicationException, ShellException {
		args.add(ARG1);
		CallCommand callCmd = new CallCommand("invalidAppName",
				"wrongFileName.txt", null, args);
		callCmd.evaluate(this.inputStream, this.outputStream);
	}

	@Test(expected = Exception.class)
	public void callCommandInvalidOutputFileName()
			throws AbstractApplicationException, ShellException {
		args.add(ARG1);
		CallCommand callCmd = new CallCommand("invalidAppName", null,
				"wrongFileName.txt", args);
		callCmd.evaluate(this.inputStream, this.outputStream);
	}

}
