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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CdException;
import sg.edu.nus.comp.cs4218.exception.LsException;
import sg.edu.nus.comp.cs4218.exception.PwdException;

public class PwdCommandTest {
	final private static String FOLDER = "folderTemp";
	final private static String FILE = "tempFile.txt";
	final private static Path PATH = Paths.get(FOLDER);
	final private static Path PATHFILE = Paths.get(FILE);
	private final File workingDir = new File(System.getProperty("user.dir"));
	private PwdCommand pwdCommand;
	private InputStream stdin;
	private OutputStream stdout;
	PrintStream printStream;

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		Files.createDirectories(PATH);
		try {
			Files.createFile(PATHFILE);
		} catch (FileAlreadyExistsException e) {
			System.err.println("File already exists: " + e.getMessage());
		}
	}

	@Before
	public void setUp() {
		pwdCommand = new PwdCommand();
		stdout = new java.io.ByteArrayOutputStream();
		printStream = new PrintStream(stdout);
		Environment.currentDirectory = workingDir.getAbsolutePath();
	}

	@After
	public void tearDown() throws IOException {
		pwdCommand = null;
		stdout.close();
		printStream.close();
		Environment.currentDirectory = workingDir.getAbsolutePath();
	}

	@AfterClass
	public static void tearDownAfterClass() throws IOException {
		Files.delete(PATH);
		Files.delete(PATHFILE);
	}

	/**
	 * Tests the helper verifyDirectory function Checks if the given directory
	 * is a valid directory Input an invalid directory
	 * 
	 * @throws PwdException
	 */
	@Test
	public void testVerifyDirectoryInvalid() throws PwdException {
		expectedEx.expect(PwdException.class);
		expectedEx.expectMessage("Cannot find working directory");
		pwdCommand.verifyDirectory("FOLDERNOTCREATED");
	}

	/**
	 * Tests the helper verifyDirectory function Checks if the given directory
	 * is a valid directory Input an null
	 * 
	 * @throws PwdException
	 */
	@Test
	public void testVerifyDirectoryNull() throws PwdException {
		expectedEx.expect(PwdException.class);
		expectedEx.expectMessage("Cannot find working directory");
		pwdCommand.verifyDirectory(null);
	}

	/**
	 * Tests the helper verifyDirectory function Checks if the given directory
	 * is a valid directory Input an empty string
	 * 
	 * @throws PwdException
	 */
	@Test
	public void testVerifyDirectoryEmptyString() throws PwdException {
		expectedEx.expect(PwdException.class);
		expectedEx.expectMessage("Cannot find working directory");
		pwdCommand.verifyDirectory("");
	}

	/**
	 * Tests the helper verifyDirectory function Checks if the given directory
	 * is a valid directory Input an valid relative directory
	 * 
	 * @throws PwdException
	 */
	@Test
	public void testVerifyDirectoryValid() throws PwdException {
		assertEquals(FOLDER, pwdCommand.verifyDirectory(FOLDER));
	}

	/**
	 * Tests the helper verifyDirectory function Checks if the given directory
	 * is a valid directory Input an text file relative directory
	 * 
	 * @throws PwdException
	 */
	@Test
	public void testVerifyDirectoryNonFolder() throws PwdException {
		expectedEx.expect(PwdException.class);
		expectedEx.expectMessage("Cannot find working directory");
		assertEquals(FOLDER, pwdCommand.verifyDirectory(FILE));
	}

	/**
	 * Tests the helper verifyDirectory function Checks if the given directory
	 * is a valid directory Input an absolute valid directory path
	 * 
	 * @throws PwdException
	 */
	@Test
	public void testVerifyDirectoryValidAbsolutePath() throws PwdException {
		assertEquals(System.getProperty("user.dir"),
				pwdCommand.verifyDirectory(System.getProperty("user.dir")));
	}

	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * input zero arg expected working directory
	 * 
	 * @throw CdException
	 */
	@Test
	public void pwdZeroArgs() throws PwdException {
		String args[] = {};
		pwdCommand.run(args, stdin, stdout);
		assertEquals(Environment.currentDirectory + System.lineSeparator(), stdout.toString());
	}

	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * input one arg expected working directory
	 * 
	 * @throw CdException
	 */
	@Test
	public void pwdOneArg() throws PwdException {
		expectedEx.expect(PwdException.class);
		expectedEx.expectMessage("Invalid Arguments");
		String args[] = { FOLDER };
		pwdCommand.run(args, stdin, stdout);
	}

	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * input five arg expected working directory
	 * 
	 * @throw CdException
	 */
	@Test
	public void pwdFiveArg() throws PwdException {
		expectedEx.expect(PwdException.class);
		expectedEx.expectMessage("Invalid Arguments");
		String args[] = { FOLDER, FOLDER, FOLDER, FOLDER, FOLDER };
		pwdCommand.run(args, stdin, stdout);
	}
}
