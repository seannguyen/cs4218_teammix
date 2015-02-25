package sg.edu.nus.comp.cs4218.shell;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;

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

public class ShellTest {

	private Shell shell;

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
		this.shell = new SimpleShell();
	}

	@Test
	public void simpleCmdLine() throws AbstractApplicationException,
			ShellException {
		String cmdLine = "echo abc";
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		shell.parseAndEvaluate(cmdLine, outputStream);
		String result = outputStream.toString();
		String expected = "abc";
		assertEquals(expected, result);
	}

	@Test
	public void appNameInQuote() throws AbstractApplicationException,
			ShellException {
		String cmdLine = "\"echo\" abc";
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		shell.parseAndEvaluate(cmdLine, outputStream);
		String result = outputStream.toString();
		String expected = "abc";
		assertEquals(expected, result);
	}

	@Test
	public void appNameFromCmdSubstitute() throws AbstractApplicationException,
			ShellException {
		String cmdLine = "`echo echo` abc";
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		shell.parseAndEvaluate(cmdLine, outputStream);
		String result = outputStream.toString();
		String expected = "abc";
		assertEquals(expected, result);
	}
	
	@Test
	public void appNameFromCmdSubstitute2() throws AbstractApplicationException,
			ShellException {
		String cmdLine = "`cat c.txt`";
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		shell.parseAndEvaluate(cmdLine, outputStream);
		String result = outputStream.toString();
		String expected = "abc";
		assertEquals(expected, result);
	}

	@Test
	public void appNameFromCmdSubstituteInQuote()
			throws AbstractApplicationException, ShellException {
		String cmdLine = "\"`echo echo`\" abc";
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		shell.parseAndEvaluate(cmdLine, outputStream);
		String result = outputStream.toString();
		String expected = "abc";
		assertEquals(expected, result);
	}
	
	@Test
	public void seqCommands()
			throws AbstractApplicationException, ShellException {
		String cmdLine = "echo abc;echo xyz";
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		shell.parseAndEvaluate(cmdLine, outputStream);
		String result = outputStream.toString();
		String expected = "abcxyz";
		assertEquals(expected, result);
	}
	
	@Test
	public void pipeCommands()
			throws AbstractApplicationException, ShellException {
		String cmdLine = "echo abc|echo xyz";
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		shell.parseAndEvaluate(cmdLine, outputStream);
		String result = outputStream.toString();
		String expected = "xyz";
		assertEquals(expected, result);
	}
	
	@Test
	public void substituteCommands()
			throws AbstractApplicationException, ShellException {
		String cmdLine = "echo \"front `echo middle` back\"";
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		shell.parseAndEvaluate(cmdLine, outputStream);
		String result = outputStream.toString();
		String expected = "front middle back";
		assertEquals(expected, result);
	}
	
	@Test
	public void incomleteCommands()
			throws AbstractApplicationException, ShellException {
		String cmdLine = "echo \"front `echo middle back\"";
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		shell.parseAndEvaluate(cmdLine, outputStream);
		String result = outputStream.toString();
		String expected = "shell: " + Configurations.MESSAGE_E_PARSING;
		assertEquals(expected, result);
	}
	
	@Test
	public void missingApp()
			throws AbstractApplicationException, ShellException {
		String cmdLine = "someapp arg1";
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		shell.parseAndEvaluate(cmdLine, outputStream);
		String result = outputStream.toString();
		String expected = "shell: " + Configurations.MESSAGE_E_MISSA;
		assertEquals(expected, result);
	}
	
	@Test
	public void missingDir()
			throws AbstractApplicationException, ShellException {
		String cmdLine = "cat <somedir.txt";
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		shell.parseAndEvaluate(cmdLine, outputStream);
		String result = outputStream.toString();
		String expected = "shell: somedir.txt: " + Configurations.MESSGE_E_MISSF;
		assertEquals(expected, result);
	}
	
	@Test
	public void appError()
			throws AbstractApplicationException, ShellException {
		String cmdLine = "cat";
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		shell.parseAndEvaluate(cmdLine, outputStream);
		String result = outputStream.toString();
		String expected = "cat:  : No argument(s)";
		assertEquals(expected, result);
	}
	
	
	
//	@Test
//	public void ioBeforeAppName()
//			throws AbstractApplicationException, ShellException {
//		String cmdLine = "<a.txt cat";
//		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//		shell.parseAndEvaluate(cmdLine, outputStream);
//		String result = outputStream.toString();
//		String expected = "this is a";
//		assertEquals(expected, result);
//	}
//	
//	@Test (expected = Exception.class)
//	public void ioFromcmdSubstitution()
//			throws AbstractApplicationException, ShellException {
//		String cmdLine = "cat `cat b.txt`";
//		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//		shell.parseAndEvaluate(cmdLine, outputStream);
//		String result = outputStream.toString();
//		String expected = "this is a";
//		assertEquals(expected, result);
//	}
}
