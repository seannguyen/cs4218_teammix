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
	   * Find application without *(asterisk) in the pattern should only printout
	   * that exact filename if it present and not all the file that contains the
	   * substring of the given filename Also tested with CYGWIN terminal
	   * 
	   * BUG_ID: testFindAppWithoutAsteriskInPattern() fix location in:
	   * FindCommand.java line number 96
	   */
	  @Test
	  public void testFindAppWithoutAsteriskInPattern() throws ShellException,
	      AbstractApplicationException {
	    String expected = "." + File.separator + "a.txt" + System.lineSeparator();
	    cmd = "find -name a.txt";
	    shell.parseAndEvaluate(cmd, stdout);
	    assertEquals(expected, stdout.toString());
	  }

	  @Test
	  public void testFindWithExactFileNamePattern()
	      throws AbstractApplicationException, ShellException {
	    cmd = "find test-files-basic -name One.txt";
	    shell.parseAndEvaluate(cmd, stdout);
	    String expected = "." + File.separator + "test-files-basic"
	        + File.separator + "One.txt" + Configurations.NEWLINE;
	    assertEquals(expected, stdout.toString());
	  }

	  @Test
	  public void testFindWithExactFolderNamePattern()
	      throws AbstractApplicationException, ShellException {
	    cmd = "find test-files-basic -name NormalFolder";
	    shell.parseAndEvaluate(cmd, stdout);
	    String expected = "." + File.separator + "test-files-basic"
	        + File.separator + "NormalFolder" + Configurations.NEWLINE;
	    assertEquals(expected, stdout.toString());
	  }

	  /**
	   * When changing directory using ".." in path, going up the directory tree
	   * again leads to the wrong folder. In the given test, after all the cd
	   * commands, the current directory should be test-files-ef1 where
	   * 5callop.txt133 is. May be a problem only with Windows
	   *
	   * BUG_ID: testCdRelativePathFromOneLevelAbove() fix location in:
	   * FindCommand.java line number 106
	   */
	  @Test
	  public void testCdRelativePathFromOneLevelAbove()
	      throws AbstractApplicationException, ShellException {
	    cmd = "cd test-files-ef1/oyster1337/mussel7715/../mussel7715/; cd ..; cd ..; cat 5callop.txt133";
	    shell.parseAndEvaluate(cmd, stdout);
	    assertEquals(
	        "Scallops live in all the world's oceans." + System.lineSeparator(),
	        stdout.toString());
	  }

	  @Test
	  public void testCdRelativePathFromOneLevelAbove2()
	      throws AbstractApplicationException, ShellException {
	    cmd = "cd test-files-basic/NormalFolder/../.HideFolder/; cd ..; cat One.txt";
	    shell.parseAndEvaluate(cmd, stdout);
	    assertEquals("This is one.txt" + Configurations.NEWLINE
	        + "Not two.txt or three.txt" + Configurations.NEWLINE
	        + "Day 1 was a long day." + Configurations.NEWLINE
	        + "Day 2 was a short day." + Configurations.NEWLINE
	        + "Day 56 was great." + System.lineSeparator(), stdout.toString());
	  }
	  

	  /**
	   * Only a directory should be given to a ls application. File name pattern is
	   * not included according to project specification. Ref spec page 9, Section
	   * Ls
	   * 
	   * BUG_ID: testLsWithPattern() fix location in: LScommand(), line 134
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

	  @Test
	  public void testLsWithPattern2() {
	    cmd = "ls *.md";
	    try {
	      shell.parseAndEvaluate(cmd, stdout);
	      fail();
	    } catch (ShellException | AbstractApplicationException e) {
	      // pass
	    }
	  }
	  
	  /**
	   * In find, the asterisk should not be treated as a wildcard when in the
	   * middle of the file name/type
	   * 
	   * 
	   * BUG_ID: testFindAppAsteriskInMiddleOfName() fix location in: FindCommand(),
	   * line 97 to 99
	   */
	  @Test
	  public void testFindAppAsteriskInMiddleOfName() throws ShellException,
	      AbstractApplicationException {
	    cmd = "find -name 5*.txt";
	    shell.parseAndEvaluate(cmd, stdout);
	    assertEquals(System.lineSeparator(), stdout.toString());
	  }
	  
	  @Test
	  public void testFindAppAsteriskInMiddleOfName2() throws ShellException,
	      AbstractApplicationException {
	    cmd = "find -name O*e.txt";
	    shell.parseAndEvaluate(cmd, stdout);
	    assertEquals(System.lineSeparator(), stdout.toString());
	  }
	  
	  @Test
	  public void testFindAppAsteriskInMiddleOfName3() throws ShellException,
	      AbstractApplicationException {
	    cmd = "find -name a.t*t";
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

	/**
	 * The last filename in a text file containing filenames is always not detected
	 * as a file. In this case, filenames.txt contains filenames a.txt b.txt and c.txt.
	 * c.txt is not found. NOTE this also applies if the filenames.txt only contain
	 * 1 filename.
	 * 
	 * BUG_ID:  testGrepWithFileNamesReadFromCatInSubCmd()
	 * fix location at: GrepCommand.java, added condition at line 54
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
	 * 
	 * BUG_ID: testEchoAppWithoutArgument()
	 * fix location at: EchoCommand.java, added condition at line 54	 
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
	 * Java exceptions should not be printed in stack traces. 
	 * All the Java exception has to be wrapped into either Shell or
	 * AbstractApplciationException.
	 *  
	 * Also discussed in forum on 31/3/2015: exception not wrapped in shell or 
	 * application exception
	 * 
	 * BUG_ID: testNumberLargerThanIntegerValue()
	 * fix location at: HeadCommand.java, added condition at line 54 & 71
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
	
	@Test
	public void testNumberLargerThanIntegerValueMultipleFilesForHead() {
		cmd = "head -n 10000000000 a.txt b.txt";
		try {
			shell.parseAndEvaluate(cmd, stdout);
			fail();
		} catch (ShellException | AbstractApplicationException e) {
			// pass
		}

		cmd = "head -n m a.txt b.txt";
		try {
			shell.parseAndEvaluate(cmd, stdout);
			fail();
		} catch (AbstractApplicationException | ShellException e) {
			//pass
		}
	}
	
	/**
	 * Java exceptions should not be printed in stack traces. 
	 * All the Java exception has to be wrapped into either Shell or
	 * AbstractApplciationException.
	 *  
	 * Also discussed in forum on 31/3/2015: exception not wrapped in shell or 
	 * application exception
	 * 
	 * Discovered and corrected at HeadCommand and TailCommand
	 * 
	 * BUG_ID: testNumberLargerThanIntegerValue()
	 * fix location at: TailCommand.java, added condition at line 56 & 71
	 */
	@Test
	public void testNumberLargerThanIntegerValueForTail() {
		cmd = "tail -n 10000000000 a.txt";
		try {
			shell.parseAndEvaluate(cmd, stdout);
			fail();
		} catch (ShellException | AbstractApplicationException e) {
			// pass
		}

		cmd = "tail -n m a.txt";
		try {
			shell.parseAndEvaluate(cmd, stdout);
			fail();
		} catch (AbstractApplicationException | ShellException e) {
			//pass
		}
	}
	
	@Test
	public void testNumberLargerThanIntegerValueMultipleFilesForTail() {
		cmd = "head -n 10000000000 a.txt b.txt";
		try {
			shell.parseAndEvaluate(cmd, stdout);
			fail();
		} catch (ShellException | AbstractApplicationException e) {
			// pass
		}

		cmd = "head -n m a.txt b.txt";
		try {
			shell.parseAndEvaluate(cmd, stdout);
			fail();
		} catch (AbstractApplicationException | ShellException e) {
			//pass
		}
	}
}
