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

public class ParserSupportMethodTest {

	private static final String APPNAME = "app";
	private static final String INPUT = "<a.txt";
	private static final String INPUTFILE = "a.txt";
	private static final String OUTPUT = ">b.txt";
	private static final String OUTPUTFILE = "b.txt";
	
	Parser parser;

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
	// Test split line
	@Test
	public void splitLineSimple() throws ShellException {
		String input = "app arg1 arg2";
		Vector<String> actual = parser.splitLine(input);
		Vector<String> expected = new Vector<String>();
		expected.add("app");
		expected.add("arg1");
		expected.add("arg2");
		compareVectorString(expected, actual);
	}

	@Test
	public void splitLineNullInput() throws ShellException {
		String input = null;
		Vector<String> actual = parser.splitLine(input);
		Vector<String> expected = new Vector<String>();
		compareVectorString(expected, actual);
	}

	// Test Substitute Command
	@Test
	public void substituteCommand() throws ShellException,
			AbstractApplicationException {
		Vector<String> input = new Vector<String>();
		input.add("app");
		input.add("`echo abc`");
		Vector<String> actual = parser.substituteCommand(input);
		Vector<String> expected = new Vector<String>();
		expected.add("app");
		expected.add("`abc`");
		compareVectorString(expected, actual);
	}

	@Test
	public void substituteCommandMulTiple() throws ShellException,
			AbstractApplicationException {
		Vector<String> input = new Vector<String>();
		input.add("`echo abc``echo def`");
		Vector<String> actual = parser.substituteCommand(input);
		Vector<String> expected = new Vector<String>();
		expected.add("`abc``def`");
		compareVectorString(expected, actual);
	}

	@Test
	public void substituteCommandInDoubleQuote() throws ShellException,
			AbstractApplicationException {
		Vector<String> input = new Vector<String>();
		input.add("\"`echo abc`\"");
		Vector<String> actual = parser.substituteCommand(input);
		Vector<String> expected = new Vector<String>();
		expected.add("\"`abc`\"");
		compareVectorString(expected, actual);
	}

	@Test(expected = Exception.class)
	public void substituteCommandIncompleteQuote() throws ShellException,
			AbstractApplicationException {
		Vector<String> input = new Vector<String>();
		input.add("\"abc `echo \"def\"");
		parser.removeQuoteTokens(input);
	}

	// Test Remove quote tokens
	@Test
	public void removeQuoteTokensSingle() throws ShellException,
			AbstractApplicationException {
		Vector<String> input = new Vector<String>();
		input.add("'abc'");
		Vector<String> actual = parser.removeQuoteTokens(input);
		Vector<String> expected = new Vector<String>();
		expected.add("abc");
		compareVectorString(expected, actual);
	}
	
	@Test
	public void removeQuoteTokensDouble() throws ShellException,
			AbstractApplicationException {
		Vector<String> input = new Vector<String>();
		input.add("\"abc\"");
		Vector<String> actual = parser.removeQuoteTokens(input);
		Vector<String> expected = new Vector<String>();
		expected.add("abc");
		compareVectorString(expected, actual);
	}
	
	@Test
	public void removeQuoteTokensBack() throws ShellException,
			AbstractApplicationException {
		Vector<String> input = new Vector<String>();
		input.add("`abc`");
		Vector<String> actual = parser.removeQuoteTokens(input);
		Vector<String> expected = new Vector<String>();
		expected.add("abc");
		compareVectorString(expected, actual);
	}
	
	@Test
	public void removeQuoteTokensBackInDoubleQuote() throws ShellException,
			AbstractApplicationException {
		Vector<String> input = new Vector<String>();
		input.add("\"front`abc` back\"");
		Vector<String> actual = parser.removeQuoteTokens(input);
		Vector<String> expected = new Vector<String>();
		expected.add("frontabc back");
		compareVectorString(expected, actual);
	}
	
	@Test
	public void removeQuoteTokensNested() throws ShellException,
			AbstractApplicationException {
		Vector<String> input = new Vector<String>();
		input.add("\"front'abc' back\"");
		input.add("\"\"args2\"\"");
		input.add("'front \"arg\"back'");
		input.add("`front \"arg\" 'a'back`");
		Vector<String> actual = parser.removeQuoteTokens(input);
		Vector<String> expected = new Vector<String>();
		expected.add("front'abc' back");
		expected.add("args2");
		expected.add("front \"arg\"back");
		expected.add("front \"arg\" 'a'back");
		compareVectorString(expected, actual);
	}

	//Test IO Redirection
	@Test
	public void ioRedirectionSimple() throws ShellException,
			AbstractApplicationException {
		Vector<String> input = new Vector<String>();
		input.add(APPNAME);
		input.add(INPUT);
		input.add(OUTPUT);
		Vector<String> actual = parser.getIoRedirectories(input);
		Vector<String> expected = new Vector<String>();
		expected.add(INPUTFILE);
		expected.add(OUTPUTFILE);
		compareVectorString(expected, actual);
	}
	
	@Test
	public void ioRedirectionInputOnly() throws ShellException,
			AbstractApplicationException {
		Vector<String> input = new Vector<String>();
		input.add(APPNAME);
		input.add(INPUT);
		Vector<String> actual = parser.getIoRedirectories(input);
		Vector<String> expected = new Vector<String>();
		expected.add(INPUTFILE);
		expected.add("");
		compareVectorString(expected, actual);
	}
	
	@Test
	public void ioRedirectionOutputOnly() throws ShellException,
			AbstractApplicationException {
		Vector<String> input = new Vector<String>();
		input.add(APPNAME);
		input.add(OUTPUT);
		Vector<String> actual = parser.getIoRedirectories(input);
		Vector<String> expected = new Vector<String>();
		expected.add("");
		expected.add(OUTPUTFILE);
		compareVectorString(expected, actual);
	}
	
	@Test
	public void ioRedirectioEmptyInput() throws ShellException,
			AbstractApplicationException {
		Vector<String> input = new Vector<String>();
		input.add("");
		Vector<String> actual = parser.getIoRedirectories(input);
		Vector<String> expected = new Vector<String>();
		expected.add("");
		expected.add("");
		compareVectorString(expected, actual);
	}
	
	@Test
	public void ioRedirectioNoAppName() throws ShellException,
			AbstractApplicationException {
		Vector<String> input = new Vector<String>();
		input.add("");
		input.add("");
		Vector<String> actual = parser.getIoRedirectories(input);
		Vector<String> expected = new Vector<String>();
		expected.add("");
		expected.add("");
		compareVectorString(expected, actual);
	}

	@Test (expected = Exception.class)
	public void ioRedirectioNullInput() throws ShellException,
			AbstractApplicationException {
		Vector<String> input = null;
		Vector<String> actual = parser.getIoRedirectories(input);
		Vector<String> expected = new Vector<String>();
		expected.add("");
		expected.add("");
		compareVectorString(expected, actual);
	}
	
	@Test(expected = Exception.class)
	public void removeQuoteTokensIncompleteQuote() throws ShellException,
			AbstractApplicationException {
		Vector<String> input = new Vector<String>();
		input.add("\"abc");
		parser.removeQuoteTokens(input);
	}

	//Test IO Redirection
	
	
	// PRIVATE HELPER METHODS
	private void compareVectorString(Vector<String> list1, Vector<String> list2) {
		assertEquals(list1.size(), list2.size());
		for (int i = 0; i < list1.size(); i++) {
			assertEquals(list1.get(i), list2.get(i));
		}
	}
}
