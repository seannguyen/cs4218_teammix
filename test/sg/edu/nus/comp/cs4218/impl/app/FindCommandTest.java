package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Vector;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import sg.edu.nus.comp.cs4218.Configurations;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CdException;
import sg.edu.nus.comp.cs4218.exception.FindException;
import sg.edu.nus.comp.cs4218.exception.LsException;
import sg.edu.nus.comp.cs4218.exception.SedException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public class FindCommandTest {
	private FindCommand findCommand;
	private final File workingDir = new File(System.getProperty("user.dir"));
	private final String TESTFOLDER = "test-files-basic";
	private InputStream stdin;
	private OutputStream stdout;
	PrintStream printStream;

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
	}

	@Before
	public void setUp() throws Exception {
		findCommand = new FindCommand();
		stdout = new java.io.ByteArrayOutputStream();
		printStream = new PrintStream(stdout);
		Environment.currentDirectory = workingDir.getAbsolutePath();
	}

	@After 
	public void tearDown() throws Exception {
		findCommand = null;
		Environment.currentDirectory = workingDir.getAbsolutePath();
		stdout.close();
		printStream.close();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws IOException {
	  
	}

	@Test
	public void testFindWithExactFileNamePattern() throws FindException {
	  String[] args = { TESTFOLDER, "-name", "One.txt" };
	  findCommand.run(args, null, stdout);
	  String expected = "." + File.separator + "test-files-basic" + File.separator + "One.txt" + Configurations.NEWLINE;
	  assertEquals(expected, stdout.toString());
	}
	
	@Test
    public void testFindAllTxtFiles() throws FindException {
      String[] args = { TESTFOLDER, "-name", "*.txt" };
      findCommand.run(args, null, stdout);
      String expected = "." + File.separator + "test-files-basic" + File.separator + ".HideFolder" + File.separator + ".textFile3.txt" + Configurations.NEWLINE +
          "." + File.separator + "test-files-basic" + File.separator + ".HideFolder" + File.separator + ".textFile4.txt" + Configurations.NEWLINE +
          "." + File.separator + "test-files-basic" + File.separator + ".HideFolder" + File.separator + "textFile1.txt" + Configurations.NEWLINE +
          "." + File.separator + "test-files-basic" + File.separator + ".HideFolder" + File.separator + "textFile2.txt" + Configurations.NEWLINE +
          "." + File.separator + "test-files-basic" + File.separator + ".Two.txt" + Configurations.NEWLINE +
          "." + File.separator + "test-files-basic" + File.separator + "NormalFolder" + File.separator + "Normal.txt" + Configurations.NEWLINE +
          "." + File.separator + "test-files-basic" + File.separator + "One.txt" + Configurations.NEWLINE;
      assertEquals(expected, stdout.toString());
    }
	
	@Test
    public void testFindAllExactFileButStarInMiddleNotTreatedAsWildcard() throws FindException {
      String[] args = { TESTFOLDER, "-name", "O*e.txt" };
      findCommand.run(args, null, stdout);
      String expected = Configurations.NEWLINE;
      assertEquals(expected, stdout.toString());
    }
	
	@Test
    public void testFindFolder() throws FindException {
      String[] args = { TESTFOLDER, "-name", "NormalFolder" };
      findCommand.run(args, null, stdout);
      String expected = "." + File.separator + "test-files-basic" + File.separator + "NormalFolder" + Configurations.NEWLINE;
      assertEquals(expected, stdout.toString());
    }
	
	@Test (expected = FindException.class)
    public void testFindMissingNameArgu() throws FindException {
      String[] args = { TESTFOLDER, "NormalFolder" };
      findCommand.run(args, null, stdout);
    }
	
	@Test (expected = FindException.class)
    public void testFindNameArguAtWrongPlace() throws FindException {
      String[] args = { TESTFOLDER, "NormalFolder", "-name" };
      findCommand.run(args, null, stdout);
    }
	
	@Test 
    public void testFindFilesStartingWith() throws FindException {
      String[] args = { TESTFOLDER, "-name", ".text*" };
      findCommand.run(args, null, stdout);
      String expected = "." + File.separator + "test-files-basic" + File.separator + ".HideFolder" + File.separator + ".textFile3.txt"  +Configurations.NEWLINE+ 
          "." + File.separator + "test-files-basic" + File.separator + ".HideFolder" + File.separator + ".textFile4.txt" +Configurations.NEWLINE;
      assertEquals(expected, stdout.toString());
    }
	
    @Test (expected = FindException.class)
    public void testHelperException1Check() throws FindException {
      findCommand.checkErrorStatus(1);
    }
      
    @Test (expected = FindException.class)
    public void testHelperException2Check() throws FindException {
      findCommand.checkErrorStatus(2);
    }
}
