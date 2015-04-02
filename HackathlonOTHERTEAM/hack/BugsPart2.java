package hack;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;

import logic.ShellLogic;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.app.AppFind;
import sg.edu.nus.comp.cs4218.impl.app.AppSed;
import sg.edu.nus.comp.cs4218.impl.app.AppWc;

public class BugsPart2 {

	ShellLogic shell;
	private static ByteArrayOutputStream stdout;
	AppSed sed;
	AppFind find;
	AppWc wc;
	private final String TEST_DIR = "testResources";
	private final String fileSeparator = System.getProperty("file.separator");
	private final String newline = System.lineSeparator();

	@Before
	public void setUp() throws Exception {
		shell = new ShellLogic();
		stdout = new ByteArrayOutputStream();
		sed = new AppSed();
		find = new AppFind();
		wc = new AppWc();
		Environment.currentDirectory = System.getProperty("user.dir")
				+ fileSeparator + TEST_DIR + fileSeparator;
		;
	}

	@After
	public void tearDown() throws Exception {
		Environment.currentDirectory = System.getProperty("user.dir");
	}

	/*
	 * This can only reproduce by running MainLogic
	 * create a folder having the name "echo"
	 * try to type in [*cho abc], this should result a command [echo abc] and out put [abc]
	 * The reason is it always add a "\" at the beginning of each file
	 */
	@Test
	public void testFileNameByGlobbing() throws Exception {
		String cmdline = "`echo *cho` abc";
		shell.parseAndEvaluate(cmdline, stdout);
		assertEquals("abc" + newline, stdout.toString());
	}
	
	/*
	 * throw wrong message. Expected the command substitution to be successfully executed.
	 * So the invalid app name now is just "abc" not "`echo abc`".
	 */
	@Test
	public void testBackQuote() throws Exception {
		String cmdline = "`echo abc`";
		try {
			shell.parseAndEvaluate(cmdline, stdout);
		} catch (ShellException e) {
			assertEquals("shell: abc : command not found" + newline, stdout.toString());
		}
	}
	
	/*
	 * Incorrect quote processing
	 * white space was added unexpectedly
	 * 
	 * [a"bc"] should become [abc] not [a bc]
	 */
	@Test
	public void testDoubleQuoteAfterText() throws Exception {
		String cmdline = "echo a\"bc\"";
		shell.parseAndEvaluate(cmdline, stdout);
		assertEquals("abc" + newline, stdout.toString());
	}

	/*
	 * This even more weird than the double quote.
	 * all the text in front of the quote disappear
	 * [abc'abc'] expected to become [abcabc] but actually get only [abc] 
	 */
	@Test
	public void testSingleQuoteAfterText() throws Exception {
		String cmdline = "echo abc'abc'";
		shell.parseAndEvaluate(cmdline, stdout);
		assertEquals("abcabc" + newline, stdout.toString());
	}
	
	/*
	 * quote still need to be perform for file name in IO Redirection
	 * So [<'a.txt'] will be the same as [<a.txt]
	 * And the same thing for Command Substitution and Globbing in IO file name
	 */
	@Test
	public void testQuoteInIoRedirection() throws Exception {
		String cmdline = "echo abc >a.txt; cat <'a.txt'";
		shell.parseAndEvaluate(cmdline, stdout);
		assertEquals("abc" + newline, stdout.toString());
	}
	
}
