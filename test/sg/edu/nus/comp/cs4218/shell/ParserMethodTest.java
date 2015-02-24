package sg.edu.nus.comp.cs4218.shell;

import static org.junit.Assert.*;

import java.util.Vector;

import org.junit.After;
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

public class ParserMethodTest {

Parser parser;
	
	@BeforeClass
	public static void intiEnvironment() {
		//initialize apps in environment
		Environment.nameAppMaps.put(Configurations.APPNAME_CD, new CdCommand());
		Environment.nameAppMaps.put(Configurations.APPNAME_LS, new LsCommand());
		Environment.nameAppMaps.put(Configurations.APPNAME_ECHO, new EchoCommand());
		Environment.nameAppMaps.put(Configurations.APPNAME_CAT, new CatCommand());
		Environment.nameAppMaps.put(Configurations.APPNAME_HEAD, new HeadCommand());
		Environment.nameAppMaps.put(Configurations.APPNAME_TAIL, new TailCommand());
		Environment.nameAppMaps.put(Configurations.APPNAME_PWD, new PwdCommand());
		Environment.nameAppMaps.put(Configurations.APPNAME_FIND, new FindCommand());
		Environment.nameAppMaps.put(Configurations.APPNAME_WC, new WcCommand());
	}
	
	@Before
	public void setUp() throws Exception {
		parser = new Parser();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	//Test split line
	@Test
	public void SplitLineSimple() throws ShellException {
		String input = "app arg1 arg2";
		Vector<String> actual = parser.splitLine(input);
		Vector<String> expected = new Vector<String>();
		expected.add("app");
		expected.add("arg1");
		expected.add("arg2");
		compareVectorString(expected, actual);
	}
	
	@Test
	public void SplitLineNullInput() throws ShellException {
		String input = null;
		Vector<String> actual = parser.splitLine(input);
		Vector<String> expected = new Vector<String>();
		compareVectorString(expected, actual);
	}

	//Test Substitute Command
	@Test
	public void SubstituteCommand() throws ShellException, AbstractApplicationException {
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
	public void SubstituteCommandMulTiple() throws ShellException, AbstractApplicationException {
		Vector<String> input = new Vector<String>();
		input.add("`echo abc``echo def`");
		Vector<String> actual = parser.substituteCommand(input);
		Vector<String> expected = new Vector<String>();
		expected.add("`abc``def`");
		compareVectorString(expected, actual);
	}
	
	@Test
	public void SubstituteCommandInDoubleQuote() throws ShellException, AbstractApplicationException {
		Vector<String> input = new Vector<String>();
		input.add("\"`echo abc`\"");
		Vector<String> actual = parser.substituteCommand(input);
		Vector<String> expected = new Vector<String>();
		expected.add("\"`abc`\"");
		compareVectorString(expected, actual);
	}
	
	@Test(expected = Exception.class) 
	public void SubstituteCommandIncompleteQuote() throws ShellException, AbstractApplicationException {
		Vector<String> input = new Vector<String>();
		input.add("\"abc `echo \"def\"");
		parser.removeQuoteTokens(input);
	}
	
	//Test Remove quote tokens
	@Test
	public void removeQuoteTokens() throws ShellException, AbstractApplicationException {
		Vector<String> input = new Vector<String>();
		input.add("\"abc\"");
		input.add("'abc'");
		input.add("`abc`");
		Vector<String> actual = parser.removeQuoteTokens(input);
		Vector<String> expected = new Vector<String>();
		expected.add("abc");
		expected.add("abc");
		expected.add("abc");
		compareVectorString(expected, actual);
	}
	
	@Test
	public void removeQuoteTokensNestedQuotes() throws ShellException, AbstractApplicationException {
		Vector<String> input = new Vector<String>();
		input.add("\"abc `def`\"");
		Vector<String> actual = parser.removeQuoteTokens(input);
		Vector<String> expected = new Vector<String>();
		expected.add("abc def");
		compareVectorString(expected, actual);
	}
	
	@Test (expected = Exception.class) 
	public void removeQuoteTokensIncompleteQuote() throws ShellException, AbstractApplicationException {
		Vector<String> input = new Vector<String>();
		input.add("\"abc");
		parser.removeQuoteTokens(input);
	}
	
	//PRIVATE HELPER METHODS
	private void compareVectorString(Vector<String> list1, Vector<String> list2) {
		assertEquals(list1.size(), list2.size());
		for (int i = 0; i < list1.size(); i++) {
			assertEquals(list1.get(i), list2.get(i));
		}
	}
}
















