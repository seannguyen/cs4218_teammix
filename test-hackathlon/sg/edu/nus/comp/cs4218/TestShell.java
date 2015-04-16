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

public class TestShell {
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
	 * The shell suppose to print out the changed directory instead of
	 * printing out the previous directory.  
	 * Also discussed in forum on 26/03/2015: "Cd and Substitute Command" 
	 */
	@Test
	public void testChangeDirectoryAndPrintPwdInSubCommand() 
			throws AbstractApplicationException, ShellException{
		String currDir = Environment.currentDirectory + File.separator + 
				"test-files-basic";
		cmd = "cd test-files-basic ; echo `pwd`";
		shell.parseAndEvaluate(cmd, stdout);
		assertEquals(currDir, stdout.toString());
	}
	
	/**
	 * The single quote within the double quote which is in a
	 * sub-command should be printed without any error since the 
	 * single quote is within a pair of double quote.
	 */
	@Test
	public void testSingleQuoteWithinDoubleQuoteWhichIsInSubCmd()
			throws ShellException, AbstractApplicationException {
		cmd = "echo `echo \"hell'o\"`";
		shell.parseAndEvaluate(cmd, stdout);
		assertEquals("hell'o", stdout.toString());
	}
	
	/**
	 * An application with capital letter should be considered as invalid.
	 * Also discussed in forum on 16/01/2015: "Character Case handling"
	 */
	@Test(expected = ShellException.class)
	public void testAppNameForCaseSensitive() 
			throws ShellException, AbstractApplicationException {
		cmd = "echO hello";
		shell.parseAndEvaluate(cmd, stdout);
	}
	
	/**
	 * Java exceptions should not be printed in stack traces. 
	 * All the Java exception has to be wrapped into either Shell or
	 * AbstractApplciationException.
	 *  
	 * Also discussed in forum on 31/3/2015: exception not wrapped in shell or 
	 * application exception
	 */
	@Test
	public void testNumberLargerThanIntegerValue() {
		cmd = "head -n 10000000000 a.txt";
		try {
			shell.parseAndEvaluate(cmd, stdout);
			fail();
		} catch (ShellException | AbstractApplicationException e) {
			// pass
		}

		cmd = "head -n m a.txt";
		try {
			shell.parseAndEvaluate(cmd, stdout);
			fail();
		} catch (AbstractApplicationException | ShellException e) {
			//pass
		}
	}

	/**
	 * The asterisk in quotes is part of the argument and should not be taken as globbing
	 * Ref. spec page 7, Section Globbing Syntax
	 */
	@Test
	public void testGlobbingAsteriskInQuotesForGrep() 
			throws AbstractApplicationException, ShellException {
		cmd = "grep '.*' a.txt";
		shell.parseAndEvaluate(cmd, stdout);
		assertEquals("this is a" + System.lineSeparator(), stdout.toString());
	}
	
	/**
	 * Command starting with semicolon should throw error in shell
	 * Also tested with CYGWIN terminal
	 */
	@Test(expected = ShellException.class)
	public void testCommandStartingWithSemiColon() 
			throws ShellException, AbstractApplicationException {
		cmd = ";echo hello";
		shell.parseAndEvaluate(cmd, stdout);
	}
	
	/**
	 * The last filename in a text file containing filenames is always not detected
	 * as a file. In this case, filenames.txt contains filenames a.txt b.txt and c.txt.
	 * c.txt is not found. NOTE this also applies if the filenames.txt only contain
	 * 1 filename.
	 */
	@Test
	public void testGrepWithFileNamesReadFromCatInSubCmd() 
			throws ShellException, AbstractApplicationException {
		cmd = "grep a `cat test-hackathlon" + File.separator + "filenames.txt`";
		String expected = "a.txt:this is a" + System.lineSeparator() 
				+ "b.txt:<a.txt" + System.lineSeparator() + "c.txt:echo abc" 
				+ System.lineSeparator();
		shell.parseAndEvaluate(cmd, stdout);
		assertEquals(expected, stdout.toString());
	}

		/**
	 * This bug is caused by NullPointerExeption in EchoCommand.java line 54.
	 * This bug is caused by NullPointerExeption.
	 * Calling echo application without arguments given should 
	 * either print an empty line or throw Shell or Application exception
	 */
	@Test
	public void testEchoAppWithoutArgument() {
		cmd = "echo";
		try {
			shell.parseAndEvaluate(cmd, stdout);
			assertEquals(System.lineSeparator(), stdout.toString());
		} catch (ShellException | AbstractApplicationException e) {
			// catch exception
		}
	}
	
	/**
	 * Echo app should not read from stdin even when no args is given
	 * Either print empty line or throw Echo exception
	 * Also discussed in forum on 13/2/2015: IO-redirection files
	 * Ref spec page 10, Section Echo
	 */
	@Test
	public void testEchoAppWithStdin() 
			throws ShellException, AbstractApplicationException {
		cmd = "echo < a.txt";
		try{
			shell.parseAndEvaluate(cmd, stdout);
			assertEquals(System.lineSeparator(), stdout.toString());
		} catch (ShellException | AbstractApplicationException e) {
			// catch exception
		}

		stdout.reset();
		
		cmd = "echo a | echo";
		try{
			shell.parseAndEvaluate(cmd, stdout);
			assertEquals(System.lineSeparator(), stdout.toString());
		} catch (ShellException | AbstractApplicationException e) {
			// catch exception
		}
	}

	/**
	 * An echo and pwd aplication's output should be printed on stdout 
	 * and ended by new line character.
	 * Ref. spec page 10, Section Echo Description
	 * Ref. spec page 9, Section Pwd Description
	 */
	@Test
	public void testApplicationEndingWithNewline() 
			throws ShellException, AbstractApplicationException {
		// echo app
		String expected = "hello" + System.lineSeparator() 
						  + "world" + System.lineSeparator();
		cmd = "echo hello ; echo world";
		shell.parseAndEvaluate(cmd, stdout);
		assertEquals(expected, stdout.toString());
		
		// pwd app
		expected = Environment.currentDirectory + System.lineSeparator()
				+ "hello" + System.lineSeparator();
		stdout.reset();
		cmd = "pwd; echo hello";
		shell.parseAndEvaluate(cmd, stdout);
		assertEquals(expected, stdout.toString());
	}
	
	/**
	 * Grep command should only print "filenames.txt" and not the entire 
	 * files and directory list
	 */
	@Test
	public void testPipeCommandWithLsFollowedByGrep() 
			throws ShellException, AbstractApplicationException{
		cmd = "ls | grep a.txt";
		shell.parseAndEvaluate(cmd, stdout);
		assertEquals("a.txt", stdout.toString());
	}
	
	/**
	 * In the grep application, regex patterns with special characters do not work.
	 * For example: "[^0-9]*" is a valid regular expression
	 * that matches a string of 0 or more non-numerical characters
	 */
	@Test
	public void testGrepRegexPatterns() 
			throws AbstractApplicationException, ShellException {
		cmd = "grep '[^0-9]*' a.txt";
		shell.parseAndEvaluate(cmd, stdout);
		assertEquals("this is a" + System.lineSeparator(), stdout.toString());
	}
	
	/**
	 * Find application without *(asterisk) in the pattern should only
	 * printout that exact filename if it present and not all the file
	 * that contains the substring of the given filename
	 * Also tested with CYGWIN terminal
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
	 * In find, the asterisk should not be treated as a wildcard
	 * when in the middle of the file name/type
	 */
	@Test
	public void testFindAppAsteriskInMiddleOfName() 
			throws ShellException, AbstractApplicationException {
		cmd = "find -name 5*.txt";
		shell.parseAndEvaluate(cmd, stdout);
		assertEquals(System.lineSeparator(), stdout.toString());
	}
	
	/**
	 * When changing directory using ".." in path, going up the 
	 * directory tree again leads to the wrong folder.
	 * In the given test, after all the cd commands, the current 
	 * directory should be test-files-ef1 where 5callop.txt133 is.
	 * May be a problem only with Windows
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
	 * Ls should only take one path as input
	 * Ref spec page 9, Section Ls
	 */
	@Test(expected = LsException.class)
	public void testLsMultiplePaths() throws AbstractApplicationException, ShellException{
		cmd = "ls test-tdd test-files-basic";
		shell.parseAndEvaluate(cmd, stdout);
	}
	
	/**
	 * Some applications should only accept one file name by specifications
	 * This applies to: Head, Tail, Sed
	 */
	@Test
	public void testHeadTailMultipleFiles(){
		cmd = "head a.txt b.txt";
		try {
			shell.parseAndEvaluate(cmd, stdout);
			fail();
		} catch (AbstractApplicationException | ShellException e) {
			//pass
		}
		
		cmd = "tail a.txt b.txt";
		try {
			shell.parseAndEvaluate(cmd, stdout);
			fail();
		} catch (AbstractApplicationException | ShellException e) {
			//pass
		}
		
		cmd = "sed 's,a,q,g' a.txt b.txt";
		try {
			shell.parseAndEvaluate(cmd, stdout);
			fail();
		} catch (AbstractApplicationException | ShellException e) {
			//pass
		}
	}
	
	/**
	 * Sed command should replace the first space with an empty string
	 */
	@Test
	public void testSedEmptyReplacement() throws AbstractApplicationException, ShellException{
		String expected = "thisis a" + System.lineSeparator();
		cmd = "sed 's, ,,' a.txt";
		shell.parseAndEvaluate(cmd, stdout);
		assertEquals(expected, stdout.toString());
	}
	
	/**
	 * Wc should count the number of newline characters, not number of lines
	 * In a.txt, the line does not have a newline character at the end of
	 * the file, so the count should be 0
	 */
	@Test
	public void testWcNewlineCount() throws AbstractApplicationException, ShellException{
		String expected = "0\ta.txt" + System.lineSeparator();
		cmd = "wc -l a.txt";
		shell.parseAndEvaluate(cmd, stdout);
		assertEquals(expected, stdout.toString());
	}

}
