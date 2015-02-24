package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import sg.edu.nus.comp.cs4218.exception.TailException;

public class TailCommandTest {
	private TailCommand tailCommand;
	private InputStream stdin;
	private OutputStream stdout;
	final private static String NOFILEMSG = "No such file or directory";
	final private static String INCORRECTARGMSG = "Incorrect argument(s)";
	final private static String CONTENT_1 = "1. CS4218 Shell is a command interpreter that provides a set of tools (applications):\n2. cd, pwd, ls, cat, echo, tail, tail, grep, sed, find and wc.\n3. Apart from that, CS4218 Shell is a language for calling and combining these application.\n4. The language supports quoting of input data, semicolon operator for calling sequences of applications, command substitution and piping for connecting applications\' inputs and outputs, IO-redirection to load and save data processed by applications from/to files.\n5. More details can be found in \"Project Description.pdf\" in IVLE.\n6. Prerequisites\n7. CS4218 Shell requires the following versions of software:\n8. JDK 7\n9. Eclipse 4.3\n10. JUnit 4\n11. Compiler compliance level must be <= 1.7\n12. END-OF-FILE\n";
	final private static String CONTENT_2 = "1. CS4218 Shell is a command interpreter that provides a set of tools (applications):\n2. cd, pwd, ls, cat, echo, tail, tail, grep, sed, find and wc.\n3. Apart from that, CS4218 Shell is a language for calling and combining these application.\n4. The language supports quoting of input data, semicolon operator for calling sequences of applications, command substitution and piping for connecting applications\' inputs and outputs, IO-redirection to load and save data processed by applications from/to files.\n5. More details can be found in \"Project Description.pdf\" in IVLE.\n";
	final private static String RESULT_10 = "3. Apart from that, CS4218 Shell is a language for calling and combining these application.\n4. The language supports quoting of input data, semicolon operator for calling sequences of applications, command substitution and piping for connecting applications\' inputs and outputs, IO-redirection to load and save data processed by applications from/to files.\n5. More details can be found in \"Project Description.pdf\" in IVLE.\n6. Prerequisites\n7. CS4218 Shell requires the following versions of software:\n8. JDK 7\n9. Eclipse 4.3\n10. JUnit 4\n11. Compiler compliance level must be <= 1.7\n12. END-OF-FILE\n";
	final private static String RESULT_1 = "12. END-OF-FILE\n";
	final private static String RESULT_11 = "2. cd, pwd, ls, cat, echo, tail, tail, grep, sed, find and wc.\n3. Apart from that, CS4218 Shell is a language for calling and combining these application.\n4. The language supports quoting of input data, semicolon operator for calling sequences of applications, command substitution and piping for connecting applications\' inputs and outputs, IO-redirection to load and save data processed by applications from/to files.\n5. More details can be found in \"Project Description.pdf\" in IVLE.\n6. Prerequisites\n7. CS4218 Shell requires the following versions of software:\n8. JDK 7\n9. Eclipse 4.3\n10. JUnit 4\n11. Compiler compliance level must be <= 1.7\n12. END-OF-FILE\n";
	final private static String FOLDERTEST = "folderTest";
	final private static String FOLDERTESTHIDE = ".FolderTestHide";
	final private static String FILE = "textFile1.txt"; 
	final private static String FILEEMPTY = "textFile2.txt"; 
	final private static String FILEHIDE = ".textFile3.txt"; 
	final private static String FILEHIDEEMPTY = ".textFile4.txt"; 
	final private static String FILESHORT = "textFile5.txt"; 
	final private static String FILESHORTHIDE = ".textFile6.txt"; 
	final private static Path PATH = Paths.get(FOLDERTEST);
	final private static Path PATHHIDE = Paths.get(FOLDERTESTHIDE);
	final private static Path PATHTOFILE = Paths.get(FILE);
	final private static Path PATHTOFILEEMPTY = Paths.get(FILEEMPTY);
	final private static Path PATHTOFILEHIDE = Paths.get(FILEHIDE);
	final private static Path PATHTOFILESHORT = Paths.get(FILESHORT);
	final private static Path PATHTOFILESHORTHIDE = Paths.get(FILESHORTHIDE);
	final private static Path PATHTOFILEHIDEEMPTY = Paths.get(FILEHIDEEMPTY);
	final private static Path PATHTOFOLDERFILE = Paths.get(FOLDERTEST + File.separator + FILE);
	final private static Path PATHTOFOLDERFILEEMPTY = Paths.get(FOLDERTEST + File.separator + FILEEMPTY);
	final private static Path PATHTOFOLDERFILEHIDE = Paths.get(FOLDERTEST + File.separator + FILEHIDE);
	final private static Path PATHTOFOLDERFILEHIDEEMPTY = Paths.get(FOLDERTEST + File.separator + FILEHIDEEMPTY);
	final private static Path PATHTOHIDDENFOLDERFILE = Paths.get(FOLDERTESTHIDE + File.separator + FILE);
	final private static Path PATHTOHIDDENFOLDERFILEEMPTY = Paths.get(FOLDERTESTHIDE + File.separator + FILEEMPTY);
	final private static Path PATHTOHIDDENFOLDERFILEHIDE = Paths.get(FOLDERTESTHIDE + File.separator + FILEHIDE);
	final private static Path PATHTOHIDDENFOLDERFILEHIDEEMPTY = Paths.get(FOLDERTESTHIDE + File.separator + FILEHIDEEMPTY);
	private final File workingDir = new File(System.getProperty("user.dir"));
	
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		String[] arrayOfFiles = {PATHTOFILE.toString(), PATHTOFILEHIDE.toString(), PATHTOFOLDERFILE.toString(), PATHTOFOLDERFILEHIDE.toString(), PATHTOHIDDENFOLDERFILE.toString(), PATHTOHIDDENFOLDERFILEHIDE.toString()};
		String[] arrayOfShortFiles = {PATHTOFILESHORT.toString(), PATHTOFILESHORTHIDE.toString()};
		Files.createDirectories(PATH);
		Files.createDirectories(PATHHIDE);
		try {
			Files.createFile(PATHTOFILE);
			Files.createFile(PATHTOFILEEMPTY);
			Files.createFile(PATHTOFILEHIDE);
			Files.createFile(PATHTOFILEHIDEEMPTY);
			Files.createFile(PATHTOFILESHORT);
			Files.createFile(PATHTOFILESHORTHIDE);
			Files.createFile(PATHTOFOLDERFILE);
			Files.createFile(PATHTOFOLDERFILEEMPTY);
			Files.createFile(PATHTOFOLDERFILEHIDE);
			Files.createFile(PATHTOFOLDERFILEHIDEEMPTY);
			Files.createFile(PATHTOHIDDENFOLDERFILE);
			Files.createFile(PATHTOHIDDENFOLDERFILEEMPTY);
			Files.createFile(PATHTOHIDDENFOLDERFILEHIDE);
			Files.createFile(PATHTOHIDDENFOLDERFILEHIDEEMPTY);
			
			for(int i = 0; i < arrayOfFiles.length; i++) {
				File file = new File(arrayOfFiles[i]);
				FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				bufferedWriter.write(CONTENT_1);
				bufferedWriter.close();
			}	
			
			for(int i = 0; i < arrayOfShortFiles.length; i++) {
				File file = new File(arrayOfShortFiles[i]);
				FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				bufferedWriter.write(CONTENT_2);
				bufferedWriter.close();
			}
		} catch (FileAlreadyExistsException e) {
			System.err.println("File already exists: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Before
	public void setUp() throws Exception {
		tailCommand = new TailCommand();		
		stdout = new java.io.ByteArrayOutputStream();		
	}
	
	@After
	public void tearDown() throws Exception {
		tailCommand = null;
		stdout = null;
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws IOException {
		Files.delete(PATHTOFILE);
		Files.delete(PATHTOFILEEMPTY);
		Files.delete(PATHTOFILEHIDE);
		Files.delete(PATHTOFILEHIDEEMPTY);
		Files.delete(PATHTOFILESHORT);
		Files.delete(PATHTOFILESHORTHIDE);
		Files.delete(PATHTOFOLDERFILE);
		Files.delete(PATHTOFOLDERFILEEMPTY);
		Files.delete(PATHTOFOLDERFILEHIDE);
		Files.delete(PATHTOFOLDERFILEHIDEEMPTY);
		Files.delete(PATHTOHIDDENFOLDERFILE);
		Files.delete(PATHTOHIDDENFOLDERFILEEMPTY);
		Files.delete(PATHTOHIDDENFOLDERFILEHIDE);
		Files.delete(PATHTOHIDDENFOLDERFILEHIDEEMPTY);
		Files.delete(PATH);
		Files.delete(PATHHIDE);
	}

	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Tail a File and display last 10 lines
	 * 
	 * @throw TailException
	 */
	@Test
	public void testTailFile() throws TailException {
		String[] args = {FILE};
		tailCommand.run(args, stdin, stdout);
		assertEquals(RESULT_10, stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Tail a empty File 
	 * 
	 * @throw TailException
	 */
	@Test
	public void testTailFileEmpty() throws TailException {
		String[] args = {FILEEMPTY};
		tailCommand.run(args, stdin, stdout);
		assertEquals("", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Tail a Hidden File and display last 10 lines
	 * 
	 * @throw TailException
	 */
	@Test
	public void testTailFileHidden() throws TailException {
		String[] args = {FILEHIDE};
		tailCommand.run(args, stdin, stdout);
		assertEquals(RESULT_10, stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Tail a hidden empty File
	 * 
	 * @throw TailException
	 */
	@Test
	public void testTailFileHiddenEmpty() throws TailException {
		String[] args = {FILEHIDEEMPTY};
		tailCommand.run(args, stdin, stdout);
		assertEquals("", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Tail a File in folder and display last 10 lines
	 * 
	 * @throw TailException
	 */
	@Test
	public void testTailFolderFile() throws TailException {
		String[] args = {FOLDERTEST + File.separator + FILE};
		tailCommand.run(args, stdin, stdout);
		assertEquals(RESULT_10, stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Tail a empty File in folder
	 * 
	 * @throw TailException
	 */
	@Test
	public void testTailFolderFileEmpty() throws TailException {
		String[] args = {FOLDERTEST + File.separator + FILEEMPTY};
		tailCommand.run(args, stdin, stdout);
		assertEquals("", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Tail a hidden File in folder and display last 10 lines
	 * 
	 * @throw TailException
	 */
	@Test
	public void testTailFolderFileHidden() throws TailException {
		String[] args = {FOLDERTEST + File.separator + FILEHIDE};
		tailCommand.run(args, stdin, stdout);
		assertEquals(RESULT_10, stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Tail a hidden empty File in folder
	 * 
	 * @throw TailException
	 */
	@Test
	public void testTailFolderFileHiddenEmpty() throws TailException {
		String[] args = {FOLDERTEST + File.separator + FILEHIDEEMPTY};
		tailCommand.run(args, stdin, stdout);
		assertEquals("", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Tail a File in hidden folder and display last 10 lines
	 * 
	 * @throw TailException
	 */
	@Test
	public void testTailHiddenFolderFile() throws TailException {
		String[] args = {FOLDERTESTHIDE + File.separator + FILE};
		tailCommand.run(args, stdin, stdout);
		assertEquals(RESULT_10, stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Tail a empty File in hidden folder
	 * 
	 * @throw TailException
	 */
	@Test
	public void testTailHiddenFolderFileEmpty() throws TailException {
		String[] args = {FOLDERTESTHIDE + File.separator + FILEEMPTY};
		tailCommand.run(args, stdin, stdout);
		assertEquals("", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Tail a hidden file in hidden folder and display last 10 lines
	 * 
	 * @throw TailException
	 */
	@Test
	public void testTailHiddenFolderFileHidden() throws TailException {
		String[] args = {FOLDERTESTHIDE + File.separator + FILEHIDE};
		tailCommand.run(args, stdin, stdout);
		assertEquals(RESULT_10, stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Tail a short File 
	 * 
	 * @throw TailException
	 */
	@Test
	public void testTailShortFile() throws TailException {
		String[] args = {FILESHORT};
		tailCommand.run(args, stdin, stdout);
		assertEquals(CONTENT_2, stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Tail short hidden file
	 * 
	 * @throw TailException
	 */
	@Test
	public void testTailShortFileHide() throws TailException {
		String[] args = {FILESHORTHIDE};
		tailCommand.run(args, stdin, stdout);
		assertEquals(CONTENT_2, stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Tail a hidden empty File in hidden folder
	 * 
	 * @throw TailException
	 */
	@Test
	public void testTailHiddenFolderFileHiddenEmpty() throws TailException {
		String[] args = {FOLDERTESTHIDE + File.separator + FILEHIDEEMPTY};
		tailCommand.run(args, stdin, stdout);
		assertEquals("", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Tail a File and display only 1 last line
	 * 
	 * @throw TailException
	 */
	@Test
	public void testTailDisplayOneLine() throws TailException {
		String[] args = {"-n", "1", FILE};
		tailCommand.run(args, stdin, stdout);
		assertEquals(RESULT_1, stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Tail a File and display 0 lines
	 * 
	 * @throw TailException
	 */
	@Test
	public void testTailDisplayZeroLine() throws TailException {
		String[] args = {"-n", "0", FILE};
		tailCommand.run(args, stdin, stdout);
		assertEquals("", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Tail a File and display negative number of lines
	 * 
	 * @throw TailException
	 */
	@Test
	public void testTailDisplayNegativeLine() throws TailException {
		expectedEx.expect(TailException.class);
		expectedEx.expectMessage("illegal line count -- ");
		String[] args = {"-n", "-1", FILE};
		tailCommand.run(args, stdin, stdout);
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Tail a File and display last 11 lines
	 * 
	 * @throw TailException
	 */
	@Test
	public void testTailDisplay11Lines() throws TailException {
		String[] args = {"-n", "11", FILE};
		tailCommand.run(args, stdin, stdout);
		assertEquals(RESULT_11, stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Tail a File and display last 100 lines
	 * 
	 * @throw TailException
	 */
	@Test
	public void testTailDisplayOverMaxLines() throws TailException {
		String[] args = {"-n", "100", FILE};
		tailCommand.run(args, stdin, stdout);
		assertEquals(CONTENT_1, stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Tail a File with wrong option
	 * 
	 * @throw TailException
	 */
	@Test
	public void testTailIllegalOption() throws TailException {
		expectedEx.expect(TailException.class);
		expectedEx.expectMessage(INCORRECTARGMSG);
		String[] args = {"-z", "10", FILE};
		tailCommand.run(args, stdin, stdout);
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Tail a File with invlaid args
	 * 
	 * @throw TailException
	 */
	@Test
	public void testTailInvalidArguments() throws TailException {
		expectedEx.expect(TailException.class);
		expectedEx.expectMessage(INCORRECTARGMSG);
		String[] args = {"-z", FILEEMPTY, FILE};
		tailCommand.run(args, stdin, stdout);
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Tail a File that does not exist with valid args
	 * 
	 * @throw TailException
	 */
	@Test
	public void testTailInvalidFileWithArgs() throws TailException {
		expectedEx.expect(TailException.class);
		expectedEx.expectMessage(NOFILEMSG);
		String[] args = {"-n", "5", "NoSuchFile.txt"};
		tailCommand.run(args, stdin, stdout);
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Tail a empty File and display last 100 lines
	 * 
	 * @throw TailException
	 */
	@Test
	public void testTailDisplayOverMaxEmptyFile() throws TailException {
		String[] args = {"-n", "100", FILEEMPTY};
		tailCommand.run(args, stdin, stdout);
		assertEquals("", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Tail a file that does not exist
	 * 
	 * @throw TailException
	 */
	@Test
	public void testTailNoSuchFile() throws TailException {
		expectedEx.expect(TailException.class);
		expectedEx.expectMessage(NOFILEMSG);
		String[] args = {"NoSuchFile.txt"};
		tailCommand.run(args, stdin, stdout);
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Tail a directory that does not exist
	 * 
	 * @throw TailException
	 */
	@Test
	public void testTailNoSuchDirectory() throws TailException {
		expectedEx.expect(TailException.class);
		expectedEx.expectMessage(NOFILEMSG);
		String[] args = {"NoSuchFile"};
		tailCommand.run(args, stdin, stdout);
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Tail a directory
	 * 
	 * @throw TailException
	 */
	@Test
	public void testTailDirectory() throws TailException {
		expectedEx.expect(TailException.class);
		expectedEx.expectMessage("Is a directory");
		String[] args = {FOLDERTEST};
		tailCommand.run(args, stdin, stdout);
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Tail a hidden File that does not exist
	 * 
	 * @throw TailException
	 */
	@Test
	public void testTailNoSuchHiddenFile() throws TailException {
		expectedEx.expect(TailException.class);
		expectedEx.expectMessage(NOFILEMSG);
		String[] args = {".NoSuchFile.txt"};
		tailCommand.run(args, stdin, stdout);
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Tail a hidden directory
	 * 
	 * @throw TailException
	 */
	@Test
	public void testTailHiddenDirectory() throws TailException {
		expectedEx.expect(TailException.class);
		expectedEx.expectMessage("Is a directory");
		String[] args = {FOLDERTESTHIDE};
		tailCommand.run(args, stdin, stdout);
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Tail a hidden directory that does not exist
	 * 
	 * @throw TailException
	 */
	@Test
	public void testTailNoSuchHiddenDirectory() throws TailException {
		expectedEx.expect(TailException.class);
		expectedEx.expectMessage(NOFILEMSG);
		String[] args = {".NoSuchFile"};
		tailCommand.run(args, stdin, stdout);
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Tail a 2 Files
	 * 
	 * @throw TailException
	 */
	@Test
	public void testTailTwoFiles() throws TailException {
		expectedEx.expect(TailException.class);
		expectedEx.expectMessage(INCORRECTARGMSG);
		String[] args = {FILE, FILEHIDE};
		tailCommand.run(args, stdin, stdout);		
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Tail a 3 Files
	 * 
	 * @throw TailException
	 */
	@Test
	public void testTailThreeFiles() throws TailException {
		expectedEx.expect(TailException.class);
		expectedEx.expectMessage(INCORRECTARGMSG);
		String[] args = {FILE, FILEEMPTY, FILEHIDE};
		tailCommand.run(args, stdin, stdout);
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Tail no args
	 * 
	 * @throw TailException
	 */
	@Test
	public void testTailNoFile() throws TailException {
		expectedEx.expect(TailException.class);
		expectedEx.expectMessage(INCORRECTARGMSG);
		String[] args = {};
		tailCommand.run(args, stdin, stdout);
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Tail a empty string
	 * 
	 * @throw TailException
	 */
	@Test
	public void testTailEmptyArg() throws TailException {
		expectedEx.expect(TailException.class);
		expectedEx.expectMessage("Null argument(s)");
		String[] args = {""};
		tailCommand.run(args, stdin, stdout);
	}
	
	/**
	 * Test helper method getAbsolutePath
	 * input file
	 * 
	 * @throw TailException
	 */
	@Test
	public void testGetAbsolutePath() throws TailException {		
		String result = tailCommand.getAbsolutePath(FILE);
		assertEquals(workingDir.getAbsolutePath() + File.separator + FILE ,result);
	}
	
	/**
	 * Test helper method getAbsolutePath
	 * input file in folder
	 * 
	 * @throw TailException
	 */
	@Test
	public void testGetAbsolutePathInFolder() throws TailException {		
		String result = tailCommand.getAbsolutePath(FOLDERTEST + File.separator + FILE);
		assertEquals(workingDir.getAbsolutePath() + File.separator + FOLDERTEST + File.separator + FILE ,result);
	}
	
	/**
	 * Test helper method getAbsolutePath
	 * input hidden file in folder
	 * 
	 * @throw TailException
	 */
	@Test
	public void testGetAbsolutePathInFolderHiddenFile() throws TailException {		
		String result = tailCommand.getAbsolutePath(FOLDERTEST + File.separator + FILEHIDE);
		assertEquals(workingDir.getAbsolutePath() + File.separator + FOLDERTEST + File.separator + FILEHIDE ,result);
	}
	
	/**
	 * Test helper method getAbsolutePath
	 * input file in hidden folder
	 * 
	 * @throw TailException
	 */
	@Test
	public void testGetAbsolutePathInHiddenFolder() throws TailException {		
		String result = tailCommand.getAbsolutePath(FOLDERTESTHIDE + File.separator + FILE);
		assertEquals(workingDir.getAbsolutePath() + File.separator + FOLDERTESTHIDE + File.separator + FILE ,result);
	}
	
	/**
	 * Test helper method getAbsolutePath
	 * input hidden file in hidden folder
	 * 
	 * @throw TailException
	 */
	@Test
	public void testGetAbsolutePathInHiddenFolderHiddenFile() throws TailException {		
		String result = tailCommand.getAbsolutePath(FOLDERTESTHIDE + File.separator + FILEHIDEEMPTY);
		assertEquals(workingDir.getAbsolutePath() + File.separator + FOLDERTESTHIDE + File.separator + FILEHIDEEMPTY ,result);
	}
	
	/**
	 * Test helper method doesFileExist
	 * input file 
	 * 
	 * @throw TailException
	 */
	@Test
	public void testDoesFileExist() throws TailException {
		File file = new File(FILE);
		Boolean result = tailCommand.doesFileExist(file);
		assertTrue(result);
	}
	
	/**
	 * Test helper method doesFileExist
	 * input empty file 
	 * 
	 * @throw TailException
	 */
	@Test
	public void testDoesFileExistHidden() throws TailException {
		File file = new File(FILEHIDEEMPTY);
		Boolean result = tailCommand.doesFileExist(file);
		assertTrue(result);
	}
	
	/**
	 * Test helper method doesFileExist
	 * input file that does not exist 
	 * 
	 * @throw TailException
	 */
	@Test
	public void testDoesFileExistNoSuchFile() throws TailException {
		File file = new File("NoSuchFile.txt");
		Boolean result = tailCommand.doesFileExist(file);
		assertFalse(result);
	}
	
	/**
	 * Test helper method doesFileExist
	 * input folder 
	 * 
	 * @throw TailException
	 */
	@Test
	public void testDoesFileExistFolder() throws TailException {
		File file = new File(FOLDERTEST);
		Boolean result = tailCommand.doesFileExist(file);
		assertFalse(result);
	}
	
	/**
	 * Test helper method isDirectory
	 * input folder
	 * 
	 * @throw TailException
	 */
	@Test
	public void testIsDirectory() throws TailException {
		File file = new File(FOLDERTEST);
		Boolean result = tailCommand.isDirectory(file);
		assertTrue(result);
	}
	
	/**
	 * Test helper method doesFileExist
	 * input hidden folder
	 * 
	 * @throw TailException
	 */
	@Test
	public void testIsDirectoryHidden() throws TailException {
		File file = new File(FOLDERTESTHIDE);
		Boolean result = tailCommand.isDirectory(file);
		assertTrue(result);
	}
	
	/**
	 * Test helper method doesFileExist
	 * input folder does not exist
	 * 
	 * @throw TailException
	 */
	@Test
	public void testIsDirectoryNoSuchDirectory() throws TailException {
		File file = new File("NoSuchFolder");
		Boolean result = tailCommand.isDirectory(file);
		assertFalse(result);
	}
}
