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

public class FindCommandTest {
	private FindCommand findCommand;
	final private static String FOLDERPARENT = "folderParentTemp";
	final private static String FOLDERSUB = "folderSubTemp";
	final private static String FOLDERSUB2 = "folderSub2Temp";
	final private static String FILE = "tempFile.txt";
	final private static String FILESUB1 = "tempFileSub1.txt";
	final private static String FILESUB2 = "tempFileSub2.txt";
	final private static Path PATHSUB = Paths.get(FOLDERPARENT + File.separator
			+ FOLDERSUB);
	final private static Path PATHSUB2 = Paths.get(FOLDERPARENT
			+ File.separator + FOLDERSUB2);
	final private static Path PATHTOFILE = Paths.get(FOLDERPARENT
			+ File.separator + FILE);
	final private static Path PATHTOFILESUB = Paths.get(FOLDERPARENT
			+ File.separator + FOLDERSUB + File.separator + FILESUB1);
	final private static Path PATHTOFILESUB2 = Paths.get(FOLDERPARENT
			+ File.separator + FOLDERSUB2 + File.separator + FILESUB2);
	private final File workingDir = new File(System.getProperty("user.dir"));
	private InputStream stdin;
	private OutputStream stdout;
	PrintStream printStream;

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		Files.createDirectories(PATHSUB);
		Files.createDirectories(PATHSUB2);
		try {
			Files.createFile(PATHTOFILE);
			Files.createFile(PATHTOFILESUB);
			Files.createFile(PATHTOFILESUB2);
		} catch (FileAlreadyExistsException e) {
			System.err.println("File already exists: " + e.getMessage());
		}
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
		Files.delete(PATHTOFILE);
		Files.delete(PATHTOFILESUB);
		Files.delete(PATHTOFILESUB2);
		Files.delete(PATHSUB);
		Files.delete(PATHSUB2);
		Files.delete(Paths.get(FOLDERPARENT));
	}

	/**
	 * Tests the helper method for getting the file from directory which matches
	 * pattern No cross boundaries
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetFilesInRestricted() throws IOException {
		Vector<String> results = findCommand.getFilesFromPattern(FOLDERPARENT,
				"*");
		assertEquals(results.size(), 3);
		assertEquals(results.get(0), FOLDERPARENT + File.separator
				+ FOLDERSUB2);
		assertEquals(results.get(1), FOLDERPARENT + File.separator
				+ FOLDERSUB);
		assertEquals(results.get(2), FOLDERPARENT + File.separator
				+ FILE);
	}

	/**
	 * Tests the helper method for getting the file from directory which matches
	 * pattern cross boundaries
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetFilesWithPatternRestricted() throws IOException {
		Vector<String> results = findCommand.getFilesFromPattern(FOLDERPARENT,
				"*Sub*");
		assertEquals(results.size(), 2);
		assertEquals(results.get(0), FOLDERPARENT + File.separator
				+ FOLDERSUB2);
		assertEquals(results.get(1), FOLDERPARENT + File.separator
				+ FOLDERSUB);

	}

	/**
	 * Tests the helper method for getting the file from directory which matches
	 * pattern cross boundaries
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetFilesCrossBoundries() throws IOException {
		Vector<String> results = findCommand.getFilesFromPattern(FOLDERPARENT,
				"**");
		assertEquals(results.size(), 5);
		assertEquals(results.get(0), FOLDERPARENT + File.separator
				+ FOLDERSUB2);
		assertEquals(results.get(1), FOLDERPARENT + File.separator
				+ FOLDERSUB2 + File.separator + FILESUB2);
		assertEquals(results.get(2), FOLDERPARENT + File.separator
				+ FOLDERSUB);
		assertEquals(results.get(3), FOLDERPARENT + File.separator
				+ FOLDERSUB + File.separator + FILESUB1);
		assertEquals(results.get(4), FOLDERPARENT + File.separator
				+ FILE);
	}

	/**
	 * Tests the helper method for getting the file from directory which matches
	 * pattern cross boundaries
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetFilesWithPatternCrossBoundries() throws IOException {
		Vector<String> results = findCommand.getFilesFromPattern(FOLDERPARENT,
				"**.txt");
		assertEquals(results.size(), 3);
		assertEquals(results.get(0), FOLDERPARENT + File.separator
				+ FOLDERSUB2 + File.separator + FILESUB2);
		assertEquals(results.get(1), FOLDERPARENT + File.separator
				+ FOLDERSUB + File.separator + FILESUB1);
		assertEquals(results.get(2), FOLDERPARENT + File.separator
				+ FILE);
	}

	/**
	 * Tests the helper method for checking "-name" Wrong input
	 */
	@Test
	public void testCheckNameArgWrong() throws FindException {
		expectedEx.expect(FindException.class);
		expectedEx.expectMessage("Missing -name");
		findCommand.checkNameArg("-sdsad");
	}

	/**
	 * Tests the helper method for checking "-name" null input
	 */
	@Test
	public void testCheckNameArgNull() throws FindException {
		expectedEx.expect(FindException.class);
		expectedEx.expectMessage("Missing -name");
		findCommand.checkNameArg(null);
	}

	/**
	 * Tests the helper method for checking "-name" valid input
	 */
	@Test
	public void testCheckNameArgValid() throws FindException {
		findCommand.checkNameArg("-name");
	}

	/**
	 * Tests the helper method for checking error code error 1
	 */
	@Test
	public void testErrorStatus1() throws FindException {
		expectedEx.expect(FindException.class);
		expectedEx.expectMessage(Configurations.MESSGE_E_MISSF);
		findCommand.checkErrorStatus(1);
	}

	/**
	 * Tests the helper method for checking error code error 2
	 */
	@Test
	public void testErrorStatus2() throws FindException {
		expectedEx.expect(FindException.class);
		expectedEx.expectMessage("Invalid Directory");
		findCommand.checkErrorStatus(2);
	}

	/**
	 * Tests the helper method for checking error code error any number other
	 * than 1 and 2
	 */
	@Test
	public void testErrorStatusRandom() throws FindException {
		findCommand.checkErrorStatus(3);
		findCommand.checkErrorStatus(10);
		findCommand.checkErrorStatus(-1);
	}

	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * invalid args size 5 (more than 3)
	 * 
	 * @throw FindException
	 */
	@Test
	public void testFiveArgs() throws FindException {
		expectedEx.expect(FindException.class);
		expectedEx.expectMessage("Invalid Arguments");
		String args[] = { FOLDERPARENT, "src", "sg", "", "-name" };
		findCommand.run(args, stdin, stdout);
	}

	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * invalid args size 1 less than 2
	 * 
	 * @throw FindException
	 */
	@Test
	public void testOneArgs() throws FindException {
		expectedEx.expect(FindException.class);
		expectedEx.expectMessage("Invalid Arguments");
		String args[] = { "-name" };
		findCommand.run(args, stdin, stdout);
	}

	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * invalid args missing -name
	 * 
	 * @throw FindException
	 */
	@Test
	public void testMissingNameArg() throws FindException {
		expectedEx.expect(FindException.class);
		expectedEx.expectMessage("Missing -name");
		String args[] = { "FOLDERPARENT", "*" };
		findCommand.run(args, stdin, stdout);
	}

	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Valid restricted search args {targetDirectory, -name, *}
	 * 
	 * @throw FindException
	 */
	@Test
	public void testValidOneStar() throws FindException {
		String args[] = { FOLDERPARENT, "-name", "*" };
		findCommand.run(args, stdin, stdout);
		assertEquals("." + File.separator + FOLDERSUB2 + Configurations.NEWLINE + 
		    "." + File.separator + FOLDERSUB2 + File.separator + FILESUB2 + Configurations.NEWLINE +
			"." + File.separator + FOLDERSUB + Configurations.NEWLINE +
			"." + File.separator + FOLDERSUB + File.separator + FILESUB1 + Configurations.NEWLINE +
			"." + File.separator + FILE + Configurations.NEWLINE, stdout.toString());
	}

	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Valid restricted search args {targetDirectory, -name, *.txt}
	 * 
	 * @throw FindException
	 */
	@Test
	public void testValidRestrictedTxt() throws FindException {
		String args[] = { FOLDERPARENT, "-name", "*.txt" };
		findCommand.run(args, stdin, stdout);
		assertEquals("."+ File.separator + FOLDERSUB2 + File.separator + FILESUB2 + Configurations.NEWLINE +
		    "." + File.separator + FOLDERSUB + File.separator + FILESUB1 + Configurations.NEWLINE +
		    "." + File.separator + FILE + Configurations.NEWLINE, stdout.toString());
	}

	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Valid restricted search args {targetDirectory, -name, *}
	 * 
	 * @throw FindException
	 */
	@Test
	public void testValidCrossBounderies() throws FindException {
		String args[] = { FOLDERPARENT, "-name", "**" };
		findCommand.run(args, stdin, stdout);
		assertEquals("." + File.separator + FOLDERSUB2
				+ Configurations.NEWLINE + "." + File.separator
				+ FOLDERSUB2 + File.separator + FILESUB2
				+ Configurations.NEWLINE + "." + File.separator
				+ FOLDERSUB + Configurations.NEWLINE + "."
				+ File.separator + FOLDERSUB + File.separator
				+ FILESUB1 + Configurations.NEWLINE + "."
				+ File.separator + FILE + Configurations.NEWLINE,
				stdout.toString());
	}

	/**
     * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
     * Valid single exact txt file without target directory args("-name", FILESUB )
     * 
     * @throw FindException
     */
    @Test
    public void testValidRecursiveChild() throws FindException {
        String args[] = {"-name", FILESUB1 };
        findCommand.run(args, stdin, stdout);
        assertEquals("." + File.separator + FOLDERPARENT + File.separator +
                 FOLDERSUB + File.separator + FILESUB1 + Configurations.NEWLINE ,
                stdout.toString());
    }
    
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Valid restricted search args {targetDirectory, -name, *.txt}
	 * 
	 * @throw FindException
	 */
	@Test
	public void testValidCrossBounderiesTxt() throws FindException {
		String args[] = { FOLDERPARENT, "-name", "**.txt" };
		findCommand.run(args, stdin, stdout);
		assertEquals("." + File.separator + FOLDERSUB2 + File.separator
				+ FILESUB2 + Configurations.NEWLINE + "."
				+ File.separator + FOLDERSUB + File.separator
				+ FILESUB1 + Configurations.NEWLINE + "."
				+ File.separator + FILE + Configurations.NEWLINE,
				stdout.toString());
	}
}
