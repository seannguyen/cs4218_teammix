package sg.edu.nus.comp.cs4218;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.*;
import sg.edu.nus.comp.cs4218.shell.SimpleShell;

public class HackathlonFix {

	
	private static Shell shell; 
	private static String cmd;
	private static ByteArrayOutputStream stdout; 
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		shell = new SimpleShell();
		stdout  = new ByteArrayOutputStream();
	}

	@Before
	public void setUp() throws Exception {
		stdout.reset();
		Environment.currentDirectory = System.getProperty("user.dir");
	}
	
	/**
	 * Find application without *(asterisk) in the pattern should only
	 * printout that exact filename if it present and not all the file
	 * that contains the substring of the given filename
	 * Also tested with CYGWIN terminal
	 * 
	 * BUG_ID:  testFindAppWithoutAsteriskInPattern()
	 * fix location in: FindCommand.java line number 96 
	 */
	@Test
	public void testFindAppWithoutAsteriskInPattern() 
			throws ShellException, AbstractApplicationException {
		String expected = "." + File.separator +"a.txt" + System.lineSeparator();
		cmd = "find -name a.txt";
		shell.parseAndEvaluate(cmd, stdout);
		assertEquals(expected, stdout.toString());
	}
	
	/**
	 * When changing directory using ".." in path, going up the 
	 * directory tree again leads to the wrong folder.
	 * In the given test, after all the cd commands, the current 
	 * directory should be test-files-ef1 where 5callop.txt133 is.
	 * May be a problem only with Windows
	 *
	 * BUG_ID:  testCdRelativePathFromOneLevelAbove()
	 * fix location in: FindCommand.java line number 106
	 */
	@Test
	public void testCdRelativePathFromOneLevelAbove() throws AbstractApplicationException, ShellException {
		cmd = "cd test-files-ef1/oyster1337/mussel7715/../mussel7715/; cd ..; cd ..; cat 5callop.txt133";
		shell.parseAndEvaluate(cmd, stdout);
		assertEquals("Scallops live in all the world's oceans." + System.lineSeparator(), stdout.toString());
	}

	/**
	 * Only a directory should be given to a ls application.
	 * File name pattern is not included according to project specification.
	 * Ref spec page 9, Section Ls
	 * 
	 * BUG_ID:  testLsWithPattern()
	 * fix location in: LScommand(), line 134
	 */
	@Test
	public void testLsWithPattern() {
		cmd = "ls *.txt";
		try {
			shell.parseAndEvaluate(cmd, stdout);
			fail();
		} catch (ShellException | AbstractApplicationException e) {
			// pass
		}
	}
	
	/**
	 * In find, the asterisk should not be treated as a wildcard
	 * when in the middle of the file name/type
	 * 
	 * 
	 * BUG_ID:  testFindAppAsteriskInMiddleOfName() 
	 * fix location in: FindCommand(), line 97 to 99
	 */
	@Test
	public void testFindAppAsteriskInMiddleOfName() 
			throws ShellException, AbstractApplicationException {
		cmd = "find -name 5*.txt";
		shell.parseAndEvaluate(cmd, stdout);
		assertEquals(System.lineSeparator(), stdout.toString());
	}
	
	/**
	 * An application with capital letter should be considered as invalid.
	 * Also discussed in forum on 16/01/2015: "Character Case handling"
	 * 
	 * BUG_ID:  testAppNameForCaseSensitive()
	 * fix location in: Parser, remove line 146
	 */
	@Test(expected = ShellException.class)
	public void testAppNameForCaseSensitive() 
			throws ShellException, AbstractApplicationException {
		cmd = "echO hello";
		shell.parseAndEvaluate(cmd, stdout);
	}
	
	/**
	 * An application with capital letter should be considered as invalid.
	 * Also discussed in forum on 16/01/2015: "Character Case handling"
	 * 
	 * BUG_ID:  testAppNameForCaseSensitive()
	 * fix location in: Parser, remove line 146
	 */
	@Test(expected = ShellException.class)
	public void testAppNameForCaseSensitiveInCmdSubstitude() 
			throws ShellException, AbstractApplicationException {
		cmd = "`echo 'eCHo'` hello";
		shell.parseAndEvaluate(cmd, stdout);
	}
	
	/**
	 * An application with capital letter should be considered as invalid.
	 * Also discussed in forum on 16/01/2015: "Character Case handling"
	 * 
	 * BUG_ID:  testAppNameForCaseSensitive()
	 * fix location in: Parser, remove line 146
	 */
	@Test(expected = ShellException.class)
	public void testAppNameForCaseSensitiveInQuote() 
			throws ShellException, AbstractApplicationException {
		cmd = "'ECho' hello";
		shell.parseAndEvaluate(cmd, stdout);
	}
	
	/** 
	 * Command starting with semicolon should throw error in shell
	 * Also tested with CYGWIN terminal
	 * 
	 * BUG_ID:  testCommandStartingWithSemiColon()
	 * fix location in: Parser, added condition at line 409
	 */
	@Test(expected = ShellException.class)
	public void testCommandStartingWithSemiColon() 
			throws ShellException, AbstractApplicationException {
		cmd = ";echo hello";
		shell.parseAndEvaluate(cmd, stdout);
	}
	
	/** 
	 * Command starting with semicolon should throw error in shell
	 * Also tested with CYGWIN terminal
	 * 
	 * BUG_ID:  testCommandStartingWithSemiColon()
	 * fix location in: Parser, added condition at line 409
	 */
	@Test(expected = ShellException.class)
	public void testCommandStartingWithPipe() 
			throws ShellException, AbstractApplicationException {
		cmd = "|echo hello";
		shell.parseAndEvaluate(cmd, stdout);
	}
	
	/** 
	 * Command starting with semicolon should throw error in shell
	 * Also tested with CYGWIN terminal
	 * 
	 * BUG_ID:  testCommandStartingWithSemiColon()
	 * fix location in: Parser, added condition at line 409
	 */
	@Test(expected = ShellException.class)
	public void testCommandEmptyPipe() 
			throws ShellException, AbstractApplicationException {
		cmd = "echo hello ||";
		shell.parseAndEvaluate(cmd, stdout);
	}
	
	/** 
	 * Command starting with semicolon should throw error in shell
	 * Also tested with CYGWIN terminal
	 * 
	 * BUG_ID:  testCommandStartingWithSemiColon()
	 * fix location in: Parser, added condition at line 409
	 */
	@Test(expected = ShellException.class)
	public void testCommandEmptySequenceCmd() 
			throws ShellException, AbstractApplicationException {
		cmd = "echo hello ;;";
		shell.parseAndEvaluate(cmd, stdout);
	}


}
