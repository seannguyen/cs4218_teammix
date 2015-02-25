package sg.edu.nus.comp.cs4218.shell;

import static org.junit.Assert.*;

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

public class ParserReadCommandTest {

	private static final String APPNAME = "app";
	private static final String TEXT1 = "abc";

	private Parser parser;

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
		parser = new Parser();
	}

	// base command
	@Test
	public void normalNoQuoteSingleArg() throws ShellException,
			AbstractApplicationException {
		String cmdLine = "app arg1";
		Vector<String> args = new Vector<String>();
		args.add("arg1");

		SequenceCommand expectedCmd = buildSeqCommand(APPNAME, args, "", "");
		SequenceCommand actualCmd = (SequenceCommand) parser
				.parseCommandLine(cmdLine);

		compareCommands(expectedCmd, actualCmd);
	}

	@Test
	public void normalNoQuote2Arg() throws ShellException,
			AbstractApplicationException {
		String cmdLine = "app arg1 arg2";
		Vector<String> args = new Vector<String>();
		args.add("arg1");
		args.add("arg2");

		SequenceCommand expectedCmd = buildSeqCommand(APPNAME, args, "", "");
		SequenceCommand actualCmd = (SequenceCommand) parser
				.parseCommandLine(cmdLine);

		compareCommands(expectedCmd, actualCmd);
	}

	@Test
	public void normalNoQuoteNoArg() throws ShellException,
			AbstractApplicationException {
		String cmdLine = "app";
		Vector<String> args = new Vector<String>();

		SequenceCommand expectedCmd = buildSeqCommand(APPNAME, args, "", "");
		SequenceCommand actualCmd = (SequenceCommand) parser
				.parseCommandLine(cmdLine);

		compareCommands(expectedCmd, actualCmd);
	}

	// single quotes
	@Test
	public void singleQuote() throws ShellException,
			AbstractApplicationException {
		String cmdLine = "app 'arg'";

		Vector<String> args = new Vector<String>();
		args.add("arg");

		SequenceCommand expectedCmd = buildSeqCommand(APPNAME, args, "", "");
		SequenceCommand actualCmd = (SequenceCommand) parser
				.parseCommandLine(cmdLine);

		compareCommands(expectedCmd, actualCmd);
	}

	@Test
	public void singleQuoteContainDoubleQuote() throws ShellException,
			AbstractApplicationException {
		String cmdLine = "app 'a\"bc\"'";

		Vector<String> args = new Vector<String>();
		args.add("a\"bc\"");

		SequenceCommand expectedCmd = buildSeqCommand(APPNAME, args, "", "");
		SequenceCommand actualCmd = (SequenceCommand) parser
				.parseCommandLine(cmdLine);

		compareCommands(expectedCmd, actualCmd);
	}

	@Test
	public void singleQuoteContainSingleQuote() throws ShellException,
			AbstractApplicationException {
		String cmdLine = "app 'a'c''";

		Vector<String> args = new Vector<String>();
		args.add("ac");

		SequenceCommand expectedCmd = buildSeqCommand(APPNAME, args, "", "");
		SequenceCommand actualCmd = (SequenceCommand) parser
				.parseCommandLine(cmdLine);

		compareCommands(expectedCmd, actualCmd);
	}

	@Test
	public void singleQuoteContainBackQuote() throws ShellException,
			AbstractApplicationException {
		String cmdLine = "app 'a`bc`'";

		Vector<String> args = new Vector<String>();
		args.add("a`bc`");

		SequenceCommand expectedCmd = buildSeqCommand(APPNAME, args, "", "");
		SequenceCommand actualCmd = (SequenceCommand) parser
				.parseCommandLine(cmdLine);

		compareCommands(expectedCmd, actualCmd);
	}

	@Test
	public void singleQuoteAfterText() throws ShellException,
			AbstractApplicationException {
		String cmdLine = "app ab'c'";

		Vector<String> args = new Vector<String>();
		args.add(TEXT1);

		SequenceCommand expectedCmd = buildSeqCommand(APPNAME, args, "", "");
		SequenceCommand actualCmd = (SequenceCommand) parser
				.parseCommandLine(cmdLine);

		compareCommands(expectedCmd, actualCmd);
	}

	@Test
	public void singleQuoteBeforeText() throws ShellException,
			AbstractApplicationException {
		String cmdLine = "app 'a'bc";

		Vector<String> args = new Vector<String>();
		args.add(TEXT1);

		SequenceCommand expectedCmd = buildSeqCommand(APPNAME, args, "", "");
		SequenceCommand actualCmd = (SequenceCommand) parser
				.parseCommandLine(cmdLine);

		compareCommands(expectedCmd, actualCmd);
	}

	@Test
	public void singleQuoteMany() throws ShellException,
			AbstractApplicationException {
		String cmdLine = "app 'a' 'b''c'";

		Vector<String> args = new Vector<String>();
		args.add("a");
		args.add("bc");

		SequenceCommand expectedCmd = buildSeqCommand(APPNAME, args, "", "");
		SequenceCommand actualCmd = (SequenceCommand) parser
				.parseCommandLine(cmdLine);

		compareCommands(expectedCmd, actualCmd);
	}

	// double quotes
	@Test
	public void doubleQuote() throws ShellException,
			AbstractApplicationException {
		String cmdLine = "app \"arg\"";

		Vector<String> args = new Vector<String>();
		args.add("arg");

		SequenceCommand expectedCmd = buildSeqCommand(APPNAME, args, "", "");
		SequenceCommand actualCmd = (SequenceCommand) parser
				.parseCommandLine(cmdLine);

		compareCommands(expectedCmd, actualCmd);
	}

	@Test
	public void doubleQuoteContainSingleQuote() throws ShellException,
			AbstractApplicationException {
		String cmdLine = "app \"a'bc'\"";

		Vector<String> args = new Vector<String>();
		args.add("a'bc'");

		SequenceCommand expectedCmd = buildSeqCommand(APPNAME, args, "", "");
		SequenceCommand actualCmd = (SequenceCommand) parser
				.parseCommandLine(cmdLine);

		compareCommands(expectedCmd, actualCmd);
	}

	public void doubleQuoteContainDoubleQuote() throws ShellException,
			AbstractApplicationException {
		String cmdLine = "app \"a\"bc\"\"";

		Vector<String> args = new Vector<String>();
		args.add(TEXT1);

		SequenceCommand expectedCmd = buildSeqCommand(APPNAME, args, "", "");
		SequenceCommand actualCmd = (SequenceCommand) parser
				.parseCommandLine(cmdLine);

		compareCommands(expectedCmd, actualCmd);
	}

	public void doubleQuoteContainBackQuote() throws ShellException,
			AbstractApplicationException {
		String cmdLine = "app \"a`echo bc`";

		Vector<String> args = new Vector<String>();
		args.add(TEXT1);

		SequenceCommand expectedCmd = buildSeqCommand(APPNAME, args, "", "");
		SequenceCommand actualCmd = (SequenceCommand) parser
				.parseCommandLine(cmdLine);

		compareCommands(expectedCmd, actualCmd);
	}

	public void doubleQuoteAfterText() throws ShellException,
			AbstractApplicationException {
		String cmdLine = "app ab\"c\"";

		Vector<String> args = new Vector<String>();
		args.add(TEXT1);

		SequenceCommand expectedCmd = buildSeqCommand(APPNAME, args, "", "");
		SequenceCommand actualCmd = (SequenceCommand) parser
				.parseCommandLine(cmdLine);

		compareCommands(expectedCmd, actualCmd);
	}

	public void doubleQuoteBeforeText() throws ShellException,
			AbstractApplicationException {
		String cmdLine = "app \"a\"bc";

		Vector<String> args = new Vector<String>();
		args.add(TEXT1);

		SequenceCommand expectedCmd = buildSeqCommand(APPNAME, args, "", "");
		SequenceCommand actualCmd = (SequenceCommand) parser
				.parseCommandLine(cmdLine);

		compareCommands(expectedCmd, actualCmd);
	}

	public void doubleQuoteMany() throws ShellException,
			AbstractApplicationException {
		String cmdLine = "app \"a\" \"b\" \"c\"";

		Vector<String> args = new Vector<String>();
		args.add("a");
		args.add("b");
		args.add("c");

		SequenceCommand expectedCmd = buildSeqCommand(APPNAME, args, "", "");
		SequenceCommand actualCmd = (SequenceCommand) parser
				.parseCommandLine(cmdLine);

		compareCommands(expectedCmd, actualCmd);
	}

	// back quotes
	@Test
	public void backQuote() throws ShellException, AbstractApplicationException {
		String cmdLine = "app `echo arg`";

		Vector<String> args = new Vector<String>();
		args.add("arg");

		SequenceCommand expectedCmd = buildSeqCommand(APPNAME, args, "", "");
		SequenceCommand actualCmd = (SequenceCommand) parser
				.parseCommandLine(cmdLine);

		compareCommands(expectedCmd, actualCmd);
	}

	@Test
	public void backQuoteInDoubleQuote() throws ShellException,
			AbstractApplicationException {
		String cmdLine = "app \"a `echo b` c\"";

		Vector<String> args = new Vector<String>();
		args.add("a b c");

		SequenceCommand expectedCmd = buildSeqCommand(APPNAME, args, "", "");
		SequenceCommand actualCmd = (SequenceCommand) parser
				.parseCommandLine(cmdLine);

		compareCommands(expectedCmd, actualCmd);
	}

	@Test
	public void backQuoteAfterText() throws ShellException,
			AbstractApplicationException {
		String cmdLine = "app `echo a`bc";

		Vector<String> args = new Vector<String>();
		args.add(TEXT1);

		SequenceCommand expectedCmd = buildSeqCommand(APPNAME, args, "", "");
		SequenceCommand actualCmd = (SequenceCommand) parser
				.parseCommandLine(cmdLine);

		compareCommands(expectedCmd, actualCmd);
	}

	@Test
	public void backQuoteBeforeText() throws ShellException,
			AbstractApplicationException {
		String cmdLine = "app ab`echo c`";

		Vector<String> args = new Vector<String>();
		args.add(TEXT1);

		SequenceCommand expectedCmd = buildSeqCommand(APPNAME, args, "", "");
		SequenceCommand actualCmd = (SequenceCommand) parser
				.parseCommandLine(cmdLine);

		compareCommands(expectedCmd, actualCmd);
	}

	@Test
	public void backQuoteMany() throws ShellException,
			AbstractApplicationException {
		String cmdLine = "app `echo c` `echo d`";

		Vector<String> args = new Vector<String>();
		args.add("c");
		args.add("d");

		SequenceCommand expectedCmd = buildSeqCommand(APPNAME, args, "", "");
		SequenceCommand actualCmd = (SequenceCommand) parser
				.parseCommandLine(cmdLine);

		compareCommands(expectedCmd, actualCmd);
	}

	@Test
	public void backQuoteManyInDoubleQuote() throws ShellException,
			AbstractApplicationException {
		String cmdLine = "app \"ab`echo c``echo d`ef\"";

		Vector<String> args = new Vector<String>();
		args.add("abcdef");

		SequenceCommand expectedCmd = buildSeqCommand(APPNAME, args, "", "");
		SequenceCommand actualCmd = (SequenceCommand) parser
				.parseCommandLine(cmdLine);

		compareCommands(expectedCmd, actualCmd);
	}

	//IO redirection
	@Test
	public void reIoNormal() throws ShellException,
			AbstractApplicationException {
		String cmdLine = "app <a.txt >b.txt";

		Vector<String> args = new Vector<String>();

		SequenceCommand expectedCmd = buildSeqCommand(APPNAME, args, "b.txt", "a.txt");
		SequenceCommand actualCmd = (SequenceCommand) parser
				.parseCommandLine(cmdLine);

		compareCommands(expectedCmd, actualCmd);
	}
	
	@Test
	public void reIoNoSpace() throws ShellException,
			AbstractApplicationException {
		String cmdLine = "app<a.txt>b.txt";

		Vector<String> args = new Vector<String>();

		SequenceCommand expectedCmd = buildSeqCommand(APPNAME, args, "b.txt", "a.txt");
		SequenceCommand actualCmd = (SequenceCommand) parser
				.parseCommandLine(cmdLine);

		compareCommands(expectedCmd, actualCmd);
	}
	
	@Test
	public void reIoBeforeAppName() throws ShellException,
			AbstractApplicationException {
		String cmdLine = "<a.txt >b.txt app";

		Vector<String> args = new Vector<String>();

		SequenceCommand expectedCmd = buildSeqCommand(APPNAME, args, "b.txt", "a.txt");
		SequenceCommand actualCmd = (SequenceCommand) parser
				.parseCommandLine(cmdLine);

		compareCommands(expectedCmd, actualCmd);
	}
	
	@Test
	public void reIoCmdSubstitute() throws ShellException,
			AbstractApplicationException {
		String cmdLine = "app <a.txt >`echo b.txt`";

		Vector<String> args = new Vector<String>();

		SequenceCommand expectedCmd = buildSeqCommand(APPNAME, args, "b.txt", "a.txt");
		SequenceCommand actualCmd = (SequenceCommand) parser
				.parseCommandLine(cmdLine);

		compareCommands(expectedCmd, actualCmd);
	}
	
	@Test (expected = Exception.class)
	public void reIoCmdSubstituteInvalidApp() throws ShellException,
			AbstractApplicationException {
		String cmdLine = "app <a.txt >`ech b.txt`";

		Vector<String> args = new Vector<String>();

		SequenceCommand expectedCmd = buildSeqCommand(APPNAME, args, "b.txt", "a.txt");
		SequenceCommand actualCmd = (SequenceCommand) parser
				.parseCommandLine(cmdLine);

		compareCommands(expectedCmd, actualCmd);
	}
	
	@Test
	public void reIoQuote() throws ShellException,
			AbstractApplicationException {
		String cmdLine = "app <a.txt >'b.txt'";

		Vector<String> args = new Vector<String>();

		SequenceCommand expectedCmd = buildSeqCommand(APPNAME, args, "b.txt", "a.txt");
		SequenceCommand actualCmd = (SequenceCommand) parser
				.parseCommandLine(cmdLine);

		compareCommands(expectedCmd, actualCmd);
	}
	
	// private helper methods

	private void compareCommands(SequenceCommand cmd1, SequenceCommand cmd2) {
		assertEquals(cmd1.getCommandSize(), cmd2.getCommandSize());
		int cmdSize = cmd1.getCommandSize();
		for (int i = 0; i < cmdSize; i++) {
			PipeCommand pipeCmd1 = (PipeCommand) cmd1.getCommand(i);
			PipeCommand pipeCmd2 = (PipeCommand) cmd2.getCommand(i);
			compareCommands(pipeCmd1, pipeCmd2);
		}
	}

	private void compareCommands(PipeCommand cmd1, PipeCommand cmd2) {
		assertEquals(cmd1.getCommandSize(), cmd2.getCommandSize());
		int cmdSize = cmd1.getCommandSize();
		for (int i = 0; i < cmdSize; i++) {
			CallCommand callCmd1 = (CallCommand) cmd1.getCommand(i);
			CallCommand callCmd2 = (CallCommand) cmd2.getCommand(i);
			compareCommands(callCmd1, callCmd2);
		}
	}

	private void compareCommands(CallCommand cmd1, CallCommand cmd2) {
		assertEquals(cmd1.getAppName(), cmd2.getAppName());
		assertEquals(cmd1.getArgs().size(), cmd2.getArgs().size());
		assertEquals(cmd1.getInputFile(), cmd2.getInputFile());
		assertEquals(cmd1.getOutputFile(), cmd2.getOutputFile());

		int argSize = cmd1.getArgs().size();
		Vector<String> args1 = cmd1.getArgs();
		Vector<String> args2 = cmd2.getArgs();
		for (int i = 0; i < argSize; i++) {
			assertEquals(args1.get(i), args2.get(i));
		}
	}

	private SequenceCommand buildSeqCommand(String appName,
			Vector<String> args, String outputFile, String inputFile) {
		SequenceCommand seqCmd = new SequenceCommand();
		PipeCommand pipeCmd = new PipeCommand();
		seqCmd.addCommand(pipeCmd);
		CallCommand callCmd = new CallCommand(appName, inputFile, outputFile,
				args);
		pipeCmd.addCommand(callCmd);
		return seqCmd;
	}

}
