package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
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

import sg.edu.nus.comp.cs4218.Configurations;
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.exception.WcException;
import sg.edu.nus.comp.cs4218.exception.WcException;

public class WcCommandTest {
	private WcCommand wcCommand;
	private InputStream stdin;
	private OutputStream stdout;
	final private static String WORD = "-w";
	final private static String CHAR = "-m";
	final private static String LINE = "-l";
	final private static String TAB = "\t";
	final private static String NEWLINE = Configurations.NEWLINE;
	final private static String NOFILEMSG = "No such file or directory";
	final private static String INCORRECTARGMSG = "Incorrect argument(s)";
	final private static String CONTENT_1 = "1. CS4218 Shell is a command interpreter that provides a set of tools (applications):\n2. cd, pwd, ls, cat, echo, tail, tail, grep, sed, find and wc.\n3. Apart from that, CS4218 Shell is a language for calling and combining these application.\n4. The language supports quoting of input data, semicolon operator for calling sequences of applications, command substitution and piping for connecting applications\' inputs and outputs, IO-redirection to load and save data processed by applications from/to files.\n5. More details can be found in \"Project Description.pdf\" in IVLE.\n6. Prerequisites\n7. CS4218 Shell requires the following versions of software:\n8. JDK 7\n9. Eclipse 4.3\n10. JUnit 4\n11. Compiler compliance level must be <= 1.7\n12. END-OF-FILE\n";
	final private static String CONTENT_2 = "6. Prerequisites\n7. CS4218 Shell requires the following versions of software:\n8. JDK 7\n9. Eclipse 4.3\n10. JUnit 4\n11. Compiler compliance level must be <= 1.7\n12. END-OF-FILE\n";
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
	final private static Path[] FILESTOCREATE = {PATHTOFILEEMPTY, PATHTOFILESHORTHIDE, PATHTOFILESHORT, PATHTOFILE,
	    PATHTOFILEHIDE, PATHTOFILEHIDEEMPTY, PATHTOFOLDERFILE, PATHTOFOLDERFILEEMPTY, PATHTOFOLDERFILEHIDE, PATHTOFOLDERFILEHIDEEMPTY,
	    PATHTOHIDDENFOLDERFILE, PATHTOHIDDENFOLDERFILEEMPTY, PATHTOHIDDENFOLDERFILEHIDE, PATHTOHIDDENFOLDERFILEHIDEEMPTY};
	final private File workingDir = new File(System.getProperty("user.dir"));
	final private static String TWOFILESALLOPT = "12" + TAB + "119" + TAB + "748" + TAB + FILE + NEWLINE + "12" + TAB + "119" + TAB + "748" + TAB + FILEHIDE + NEWLINE + "24" + TAB + "238" + TAB + "1496" + TAB + "total";
	final private static String RESULTSALLOPT = "100" + TAB + "200" + TAB + "300" + TAB + "total";
	
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		String[] arrayOfFiles = {PATHTOFILE.toString(), PATHTOFILEHIDE.toString(), PATHTOFOLDERFILE.toString(), PATHTOFOLDERFILEHIDE.toString(), PATHTOHIDDENFOLDERFILE.toString(), PATHTOHIDDENFOLDERFILEHIDE.toString()};
		String[] arrayOfShortFiles = {PATHTOFILESHORT.toString(), PATHTOFILESHORTHIDE.toString()};
		Files.createDirectories(PATH);
		Files.createDirectories(PATHHIDE);
		for(int i = 0; i < FILESTOCREATE.length; i++) {
          try {
                Files.createFile(FILESTOCREATE[i]);
            } catch (FileAlreadyExistsException e) {
                System.err.println("File already exists: " + e.getMessage());
            }   
        }
		for(int i = 0; i < arrayOfFiles.length; i++) {
			try {
			    File file = new File(arrayOfFiles[i]);
				FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				bufferedWriter.write(CONTENT_1);
				bufferedWriter.close();
			} catch (IOException e) {
	            e.printStackTrace();
	        }
		}	
			
		for(int i = 0; i < arrayOfShortFiles.length; i++) {
			try {
		        File file = new File(arrayOfShortFiles[i]);
				FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				bufferedWriter.write(CONTENT_2);
				bufferedWriter.close();
			} catch (IOException e) {
	            e.printStackTrace();
	        }
		}
	}
	
	@Before
	public void setUp() throws Exception {
		wcCommand = new WcCommand();		
		stdout = new java.io.ByteArrayOutputStream();		
	}
	
	@After
	public void tearDown() throws Exception {
		wcCommand = null;
		stdout = null;
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws IOException {
	  try {
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
	  } catch (IOException e) {
        //e.printStackTrace();
      }
	}

	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Wc with one file and no options
	 * 
	 * @throw WcException
	 */
	@Test
	public void testOneFileNoOption() throws WcException {
		String[] args = {FILE};
		wcCommand.run(args, stdin, stdout);
		assertEquals("12" + TAB + "119" + TAB + "748" + TAB + FILE + NEWLINE, stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Wc with two files and no options
	 * 
	 * @throw WcException
	 */
	@Test
	public void testTwoFilesNoOption() throws WcException {
		String[] args = {FILE, FILEHIDE};
		wcCommand.run(args, stdin, stdout);
		assertEquals(TWOFILESALLOPT, stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Wc with three files and no options
	 * 
	 * @throw WcException
	 */
	@Test
	public void testThreeFilesNoOption() throws WcException {
		String[] args = {FILE, FILEEMPTY, FILEHIDE};
		wcCommand.run(args, stdin, stdout);
		assertEquals("12" + TAB + "119" + TAB + "748" + TAB + FILE + NEWLINE + "0" + TAB + "0" + TAB + "0" + TAB + FILEEMPTY + NEWLINE + "12" + TAB + "119" + TAB + "748" + TAB + FILEHIDE + NEWLINE + "24" + TAB + "238" + TAB + "1496" + TAB + "total", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Wc with no file and no options
	 * 
	 * @throw WcException
	 */
	@Test
	public void testNoFileNoOption() throws WcException {
		expectedEx.expect(WcException.class);
		expectedEx.expectMessage("Null stdin");
		String[] args = {};
		wcCommand.run(args, stdin, stdout);
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Wc with no file and with options
	 * 
	 * @throw WcException
	 */
	@Test
	public void testNoFileWithOptions() throws WcException {
		expectedEx.expect(WcException.class);
		expectedEx.expectMessage("Null stdin");
		String[] args = {WORD, CHAR};
		wcCommand.run(args, stdin, stdout);
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Wc with one empty file and no options
	 * 
	 * @throw WcException
	 */
	@Test
	public void testEmptyFileNoOption() throws WcException {
		String[] args = {FILEEMPTY};
		wcCommand.run(args, stdin, stdout);
		assertEquals("0" + TAB + "0" + TAB + "0" + TAB + FILEEMPTY + NEWLINE, stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Wc with null and no options
	 * 
	 * @throw WcException
	 */
	@Test
	public void testNullFileNoOption() throws WcException {
		expectedEx.expect(WcException.class);
		expectedEx.expectMessage("Null argument(s)");
		String[] args = {""};
		wcCommand.run(args, stdin, stdout);
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Wc with one file WORD CHAR LINE
	 * 
	 * @throw WcException
	 */
	@Test
	public void testOneFileWordCharLine() throws WcException {
		String[] args = {WORD, CHAR, LINE, FILE};
		wcCommand.run(args, stdin, stdout);
		assertEquals("12" + TAB + "119" + TAB + "748" + TAB + FILE + NEWLINE, stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Wc with one file CHAR LINE
	 * 
	 * @throw WcException
	 */
	@Test
	public void testOneFileCharLine() throws WcException {
		String[] args = {CHAR, LINE, FILE};
		wcCommand.run(args, stdin, stdout);
		assertEquals("12" + TAB + "748" + TAB + FILE + NEWLINE, stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Wc with one file WORD CHAR
	 * 
	 * @throw WcException
	 */
	@Test
	public void testOneFileWordChar() throws WcException {
		String[] args = {WORD, CHAR, FILE};
		wcCommand.run(args, stdin, stdout);
		assertEquals("119" + TAB + "748" + TAB + FILE + NEWLINE, stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Wc with one file and WORD LINE
	 * 
	 * @throw WcException
	 */
	@Test
	public void testOneFileWordLine() throws WcException {
		String[] args = {WORD, LINE, FILE};
		wcCommand.run(args, stdin, stdout);
		assertEquals("12" + TAB + "119" + TAB + FILE + NEWLINE, stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Wc with one file and WORD
	 * 
	 * @throw WcException
	 */
	@Test
	public void testOneFileWord() throws WcException {
		String[] args = {WORD, FILE};
		wcCommand.run(args, stdin, stdout);
		assertEquals("119" + TAB + FILE + NEWLINE, stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Wc with one file and CHAR
	 * 
	 * @throw WcException
	 */
	@Test
	public void testOneFileChar() throws WcException {
		String[] args = {CHAR, FILE};
		wcCommand.run(args, stdin, stdout);
		assertEquals("748" + TAB + FILE + NEWLINE, stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Wc with one file and LINE
	 * 
	 * @throw WcException
	 */
	@Test
	public void testOneFileLine() throws WcException {
		String[] args = {LINE, FILE};
		wcCommand.run(args, stdin, stdout);
		assertEquals("12" + TAB + FILE + NEWLINE, stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Wc with two file and WORD CHAR LINE
	 * 
	 * @throw WcException
	 */
	@Test
	public void testTwoFilesWordCharLine() throws WcException {
		String[] args = {WORD, CHAR, LINE, FILE, FILEHIDE};
		wcCommand.run(args, stdin, stdout);
		assertEquals(TWOFILESALLOPT, stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Wc with two file and CHAR LINE
	 * 
	 * @throw WcException
	 */
	@Test
	public void testTwoFilesCharLine() throws WcException {
		String[] args = {CHAR, LINE, FILE, FILEHIDE};
		wcCommand.run(args, stdin, stdout);
		assertEquals("12" + TAB + "748" + TAB + FILE + NEWLINE + "12" + TAB + "748" + TAB + FILEHIDE + NEWLINE + "24" + TAB + "1496" + TAB + "total", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Wc with two file and WORD CHAR LINE
	 * 
	 * @throw WcException
	 */
	@Test
	public void testTwoFilesLineWordChar() throws WcException {
		String[] args = {WORD, CHAR, FILE, FILEHIDE};
		wcCommand.run(args, stdin, stdout);
		assertEquals("119" + TAB + "748" + TAB + FILE + NEWLINE + "119" + TAB + "748" + TAB + FILEHIDE + NEWLINE + "238" + TAB + "1496" + TAB + "total", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Wc with two file and WORD LINE
	 * 
	 * @throw WcException
	 */
	@Test
	public void testTwoFilesWordLine() throws WcException {
		String[] args = {WORD, LINE, FILE, FILEHIDE};
		wcCommand.run(args, stdin, stdout);
		assertEquals("12" + TAB + "119" + TAB + FILE + NEWLINE + "12" + TAB + "119" + TAB + FILEHIDE + NEWLINE + "24" + TAB + "238" + TAB + "total", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Wc with two file and WORD
	 * 
	 * @throw WcException
	 */
	@Test
	public void testTwoFilesWord() throws WcException {
		String[] args = {WORD, FILE, FILEHIDE};
		wcCommand.run(args, stdin, stdout);
		assertEquals("119" + TAB + FILE + NEWLINE + "119" + TAB + FILEHIDE + NEWLINE + "238" + TAB + "total", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Wc with two file and CHAR
	 * 
	 * @throw WcException
	 */
	@Test
	public void testTwoFilesChar() throws WcException {
		String[] args = {CHAR, FILE, FILEHIDE};
		wcCommand.run(args, stdin, stdout);
		assertEquals("748" + TAB + FILE + NEWLINE + "748" + TAB + FILEHIDE + NEWLINE + "1496" + TAB + "total", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Wc with two file and LINE
	 * 
	 * @throw WcException
	 */
	@Test
	public void testTwoFilesLine() throws WcException {
		String[] args = {LINE, FILE, FILEHIDE};
		wcCommand.run(args, stdin, stdout);
		assertEquals("12" + TAB + FILE + NEWLINE + "12" + TAB + FILEHIDE + NEWLINE + "24" + TAB + "total", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Wc with three files and WORD CHAR LINE
	 * 
	 * @throw WcException
	 */
	@Test
	public void testThreeFilesWordCharLine() throws WcException {
		String[] args = {WORD, CHAR, LINE, FILE, FILEEMPTY, FILEHIDE};
		wcCommand.run(args, stdin, stdout);
		assertEquals("12" + TAB + "119" + TAB + "748" + TAB + FILE + NEWLINE + "0" + TAB + "0" + TAB + "0" + TAB + FILEEMPTY + NEWLINE + "12" + TAB + "119" + TAB + "748" + TAB + FILEHIDE + NEWLINE + "24" + TAB + "238" + TAB + "1496" + TAB + "total", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Wc with three files and CHAR LINE
	 * 
	 * @throw WcException
	 */
	@Test
	public void testThreeFilesCharLine() throws WcException {
		String[] args = {CHAR, LINE, FILE, FILEEMPTY, FILEHIDE};
		wcCommand.run(args, stdin, stdout);
		assertEquals("12" + TAB + "748" + TAB + FILE + NEWLINE + "0" + TAB + "0" + TAB + FILEEMPTY + NEWLINE + "12" + TAB + "748" + TAB + FILEHIDE + NEWLINE + "24" + TAB + "1496" + TAB + "total", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Wc with three files and WORD CHAR
	 * 
	 * @throw WcException
	 */
	@Test
	public void testThreeFilesWordChar() throws WcException {
		String[] args = {WORD, CHAR, FILE, FILEEMPTY, FILEHIDE};
		wcCommand.run(args, stdin, stdout);
		assertEquals("119" + TAB + "748" + TAB + FILE + NEWLINE + "0" + TAB + "0" + TAB + FILEEMPTY + NEWLINE + "119" + TAB + "748" + TAB + FILEHIDE + NEWLINE + "238" + TAB + "1496" + TAB + "total", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Wc with three files and WORD LINE
	 * 
	 * @throw WcException
	 */
	@Test
	public void testThreeFilesWordLine() throws WcException {
		String[] args = {WORD, LINE, FILE, FILEEMPTY, FILEHIDE};
		wcCommand.run(args, stdin, stdout);
		assertEquals("12" + TAB + "119" + TAB + FILE + NEWLINE + "0" + TAB + "0" + TAB + FILEEMPTY + NEWLINE + "12" + TAB + "119" + TAB + FILEHIDE + NEWLINE + "24" + TAB + "238" + TAB + "total", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Wc with three files and WORD 
	 * 
	 * @throw WcException
	 */
	@Test
	public void testThreeFilesWord() throws WcException {
		String[] args = {WORD, FILE, FILEEMPTY, FILEHIDE};
		wcCommand.run(args, stdin, stdout);
		assertEquals("119" + TAB + FILE + NEWLINE + "0" + TAB + FILEEMPTY + NEWLINE + "119" + TAB + FILEHIDE + NEWLINE + "238" + TAB + "total", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Wc with three files and CHAR
	 * 
	 * @throw WcException
	 */
	@Test
	public void testThreeFilesChar() throws WcException {
		String[] args = {CHAR, FILE, FILEEMPTY, FILEHIDE};
		wcCommand.run(args, stdin, stdout);
		assertEquals("748" + TAB + FILE + NEWLINE + "0" + TAB + FILEEMPTY + NEWLINE + "748" + TAB + FILEHIDE + NEWLINE + "1496" + TAB + "total", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Wc with three files and LINE
	 * 
	 * @throw WcException
	 */
	@Test
	public void testThreeFilesLine() throws WcException {
		String[] args = {LINE, FILE, FILEEMPTY, FILEHIDE};
		wcCommand.run(args, stdin, stdout);
		assertEquals("12" + TAB + FILE + NEWLINE + "0" + TAB + FILEEMPTY + NEWLINE + "12" + TAB + FILEHIDE + NEWLINE + "24" + TAB + "total", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Wc with no File and WORD CHAR LINE
	 * 
	 * @throw WcException
	 */
	@Test
	public void testNoFileWordCharLine() throws WcException {
		expectedEx.expect(WcException.class);
		expectedEx.expectMessage("Null stdin");
		String[] args = {WORD, CHAR, LINE};
		wcCommand.run(args, stdin, stdout);
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Wc with three files and  CHAR LINE repeated
	 * 
	 * @throw WcException
	 */
	@Test
	public void testThreeFilesCharLineRepeats() throws WcException {
		String[] args = {CHAR, LINE, LINE, CHAR, CHAR, LINE, FILE, FILEEMPTY, FILEHIDE};
		wcCommand.run(args, stdin, stdout);
		assertEquals("12" + TAB + "748" + TAB + FILE + NEWLINE + "0" + TAB + "0" + TAB + FILEEMPTY + NEWLINE + "12" + TAB + "748" + TAB + FILEHIDE + NEWLINE + "24" + TAB + "1496" + TAB + "total", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Wc with three files and WORD CHAR LINE repeated
	 * 
	 * @throw WcException
	 */
	@Test
	public void testTwoFilesWordCharLineRepeats() throws WcException {
		String[] args = {WORD, CHAR, LINE, LINE, CHAR, WORD, CHAR, LINE, WORD, FILE, FILEHIDE};
		wcCommand.run(args, stdin, stdout);
		assertEquals(TWOFILESALLOPT, stdout.toString());
	}
	
	/**
	 * Test helper resetCounters
	 * 
	 * @throw WcException
	 */
	public void testResetCounters() throws WcException {		
		wcCommand.wordCount = 10;
		wcCommand.charCount = 10;
		wcCommand.lineCount = 10;
		wcCommand.resetCounters();
		assertEquals(0, wcCommand.wordCount);
		assertEquals(0, wcCommand.charCount);
		assertEquals(0, wcCommand.lineCount);
	}
	
	/**
	 * Test helper resetAllCounters
	 * 
	 * @throw WcException
	 */
	public void testResetAllCounters() throws WcException {
		wcCommand.wordCount = 10;
		wcCommand.charCount = 10;
		wcCommand.lineCount = 10;
		wcCommand.totalLineCount = 10;
		wcCommand.totalWordCount = 10;
		wcCommand.totalCharCount = 10;
		wcCommand.lineFlag = true;
		wcCommand.wordFlag = true;
		wcCommand.charFlag = true;
		wcCommand.resetAllCounters();
		assertEquals(0, wcCommand.wordCount);
		assertEquals(0, wcCommand.charCount);
		assertEquals(0, wcCommand.lineCount);
		assertEquals(0, wcCommand.totalWordCount);
		assertEquals(0, wcCommand.totalCharCount);
		assertEquals(0, wcCommand.totalLineCount);
		assertFalse(wcCommand.lineFlag);
		assertFalse(wcCommand.wordFlag);
		assertFalse(wcCommand.charFlag);
	}
	
	/**
	 * Test helper method getAbsolutePath
	 * input file 
	 * 
	 * @throw WcException
	 */
	@Test
	public void testGetAbsolutePath() throws WcException {		
		String result = wcCommand.getAbsolutePath(FILE);
		assertEquals(workingDir.getAbsolutePath() + File.separator + FILE ,result);
	}
	
	/**
	 * Test helper method getAbsolutePath
	 * input file in folder
	 * 
	 * @throw WcException
	 */
	@Test
	public void testGetAbsolutePathInFolder() throws WcException {		
		String result = wcCommand.getAbsolutePath(FOLDERTEST + File.separator + FILE);
		assertEquals(workingDir.getAbsolutePath() + File.separator + FOLDERTEST + File.separator + FILE ,result);
	}
	
	/**
	 * Test helper method getAbsolutePath
	 * input hidden file in folder
	 * 
	 * @throw WcException
	 */
	@Test
	public void testGetAbsolutePathInFolderHiddenFile() throws WcException {		
		String result = wcCommand.getAbsolutePath(FOLDERTEST + File.separator + FILEHIDE);
		assertEquals(workingDir.getAbsolutePath() + File.separator + FOLDERTEST + File.separator + FILEHIDE ,result);
	}
	
	/**
	 * Test helper method getAbsolutePath
	 * input file in hidden folder
	 * 
	 * @throw WcException
	 */
	@Test
	public void testGetAbsolutePathInHiddenFolder() throws WcException {		
		String result = wcCommand.getAbsolutePath(FOLDERTESTHIDE + File.separator + FILE);
		assertEquals(workingDir.getAbsolutePath() + File.separator + FOLDERTESTHIDE + File.separator + FILE ,result);
	}
	
	/**
	 * Test helper method getAbsolutePath
	 * input hidden file in hidden folder
	 * 
	 * @throw WcException
	 */
	@Test
	public void testGetAbsolutePathInHiddenFolderHiddenFile() throws WcException {		
		String result = wcCommand.getAbsolutePath(FOLDERTESTHIDE + File.separator + FILEHIDEEMPTY);
		assertEquals(workingDir.getAbsolutePath() + File.separator + FOLDERTESTHIDE + File.separator + FILEHIDEEMPTY ,result);
	}
	
	/**
	 * Test helper method doesFileExist
	 * input file 
	 * 
	 * @throw WcException
	 */
	@Test
	public void testDoesFileExist() throws WcException {
		File file = new File(FILE);
		Boolean result = wcCommand.doesFileExist(file);
		assertTrue(result);
	}

	/**
	 * Test helper method doesFileExist
	 * input hidden file 
	 * 
	 * @throw WcException
	 */
	@Test
	public void testDoesFileExistHidden() throws WcException {
		File file = new File(FILEHIDEEMPTY);
		Boolean result = wcCommand.doesFileExist(file);
		assertTrue(result);
	}

	/**
	 * Test helper method doesFileExist
	 * input file that does not exist
	 * 
	 * @throw WcException
	 */
	@Test
	public void testDoesFileExistNoSuchFile() throws WcException {
		File file = new File("NoSuchFile.txt");
		Boolean result = wcCommand.doesFileExist(file);
		assertFalse(result);
	}
	

	/**
	 * Test helper method doesFileExist
	 * input folder
	 * 
	 * @throw WcException
	 */
	@Test
	public void testDoesFileExistFolder() throws WcException {
		File file = new File(FOLDERTEST);
		Boolean result = wcCommand.doesFileExist(file);
		assertFalse(result);
	}
	

	/**
	 * Test helper method isDirectory
	 * input folder
	 * 
	 * @throw WcException
	 */
	@Test
	public void testIsDirectory() throws WcException {
		File file = new File(FOLDERTEST);
		Boolean result = wcCommand.isDirectory(file);
		assertTrue(result);
	}
	

	/**
	 * Test helper method isDirectory
	 * input hidden folder
	 * 
	 * @throw WcException
	 */
	@Test
	public void testIsDirectoryHidden() throws WcException {
		File file = new File(FOLDERTESTHIDE);
		Boolean result = wcCommand.isDirectory(file);
		assertTrue(result);
	}
	
	/**
	 * Test helper method isDirectory
	 * input folder doesn not exist
	 * 
	 * @throw WcException
	 */
	@Test
	public void testIsDirectoryNoSuchDirectory() throws WcException {
		File file = new File("NoSuchFolder");
		Boolean result = wcCommand.isDirectory(file);
		assertFalse(result);
	}
	
	/**
	 * Test helper method processFiles three files
	 * input 3 files
	 * 
	 * @throw WcException
	 */
	@Test
	public void testProcessFilesThreeFiles() throws WcException {
		String[] args = {FILE, FILEEMPTY, FILEHIDE};
		wcCommand.processFiles(args, stdin, stdout);
		assertEquals("12" + TAB + "119" + TAB + "748" + TAB + FILE + NEWLINE + "0" + TAB + "0" + TAB + "0" + TAB + FILEEMPTY + NEWLINE + "12" + TAB + "119" + TAB + "748" + TAB + FILEHIDE + NEWLINE + "24" + TAB + "238" + TAB + "1496" + TAB + "total", stdout.toString());
	}

	/**
	 * Test helper method processFiles three files
	 * input 2 files with no args
	 * 
	 * @throw WcException
	 */
	@Test
	public void testProcessFilesNoArg() throws WcException {
		expectedEx.expect(WcException.class);
		expectedEx.expectMessage("No argument(s)");
		String[] args = {};
		wcCommand.processFiles(args, stdin, stdout);
	}
	
	/**
	 * Test helper method processFiles three files
	 * input directorys
	 * 
	 * @throw WcException
	 */
	@Test
	public void testProcessFilesDirectory() throws WcException {
		expectedEx.expect(WcException.class);
		expectedEx.expectMessage("Is a directory");
		String[] args = {PATH.toString()};
		wcCommand.processFiles(args, stdin, stdout);
	}
	
	/**
	 * Test helper method processFiles three files
	 * input file does not exist
	 * 
	 * @throw WcException
	 */
	@Test
	public void testProcessFilesNoSuchFile() throws WcException {
		expectedEx.expect(WcException.class);
		expectedEx.expectMessage(NOFILEMSG);
		String[] args = {"NoSuchFile.txt"};
		wcCommand.processFiles(args, stdin, stdout);
	}
	
	/**
	 * Test helper method printResults
	 * input false for flags
	 * 
	 * @throw WcException
	 */
	@Test
	public void testPrintResultsAllFalse() throws WcException {
		wcCommand.lineFlag = false;
		wcCommand.wordFlag = false;
		wcCommand.charFlag = false;
		wcCommand.lineCount = 10;
		wcCommand.wordCount = 20;
		wcCommand.charCount = 30;
		wcCommand.printResults(FILE, stdout);
		assertEquals("10" + TAB + "20" + TAB + "30" + TAB + FILE + NEWLINE, stdout.toString());
	}
	
	/**
	 * Test helper method printResults LINE WORD CHAR
	 * 
	 * @throw WcException
	 */
	@Test
	public void testPrintResultsLineWordChar() throws WcException {
		wcCommand.lineFlag = true;
		wcCommand.wordFlag = true;
		wcCommand.charFlag = true;
		wcCommand.lineCount = 10;
		wcCommand.wordCount = 20;
		wcCommand.charCount = 30;
		wcCommand.printResults(FILE, stdout);
		assertEquals("10" + TAB + "20" + TAB + "30" + TAB + FILE + NEWLINE, stdout.toString());
	}
	
	/**
	 * Test helper method printResults LINE WORD
	 * 
	 * @throw WcException
	 */
	@Test
	public void testPrintResultsLineWord() throws WcException {
		wcCommand.lineFlag = true;
		wcCommand.wordFlag = true;
		wcCommand.charFlag = false;
		wcCommand.lineCount = 10;
		wcCommand.wordCount = 20;
		wcCommand.charCount = 30;
		wcCommand.printResults(FILE, stdout);
		assertEquals("10" + TAB + "20" + TAB + FILE + NEWLINE, stdout.toString());
	}
	
	/**
	 * Test helper method printResults LINE CHAR
	 * input false for flags
	 * 
	 * @throw WcException
	 */
	@Test
	public void testPrintResultsLineChar() throws WcException {
		wcCommand.lineFlag = true;
		wcCommand.wordFlag = false;
		wcCommand.charFlag = true;
		wcCommand.lineCount = 10;
		wcCommand.wordCount = 20;
		wcCommand.charCount = 30;
		wcCommand.printResults(FILE, stdout);
		assertEquals("10" + TAB + "30" + TAB + FILE + NEWLINE, stdout.toString());
	}
	
	/**
	 * Test helper method printResults WORD CHAR
	 * input false for flags
	 * 
	 * @throw WcException
	 */
	@Test
	public void testPrintResultsWordChar() throws WcException {
		wcCommand.lineFlag = false;
		wcCommand.wordFlag = true;
		wcCommand.charFlag = true;
		wcCommand.lineCount = 10;
		wcCommand.wordCount = 20;
		wcCommand.charCount = 30;
		wcCommand.printResults(FILE, stdout);
		assertEquals("20" + TAB + "30" + TAB + FILE + NEWLINE, stdout.toString());
	}
	
	/**
	 * Test helper method printResults LINE
	 * 
	 * @throw WcException
	 */
	@Test
	public void testPrintResultsLine() throws WcException {
		wcCommand.lineFlag = true;
		wcCommand.wordFlag = false;
		wcCommand.charFlag = false;
		wcCommand.lineCount = 10;
		wcCommand.wordCount = 20;
		wcCommand.charCount = 30;
		wcCommand.printResults(FILE, stdout);
		assertEquals("10" + TAB + FILE + NEWLINE, stdout.toString());
	}
	
	/**
	 * Test helper method printResults WORD
	 * 
	 * @throw WcException
	 */
	@Test
	public void testPrintResultsWord() throws WcException {
		wcCommand.lineFlag = false;
		wcCommand.wordFlag = true;
		wcCommand.charFlag = false;
		wcCommand.lineCount = 10;
		wcCommand.wordCount = 20;
		wcCommand.charCount = 30;
		wcCommand.printResults(FILE, stdout);
		assertEquals("20" + TAB + FILE + NEWLINE, stdout.toString());
	}
	
	/**
	 * Test helper method printResults CHAR
	 * 
	 * @throw WcException
	 */
	@Test
	public void testPrintResultsChar() throws WcException {
		wcCommand.lineFlag = false;
		wcCommand.wordFlag = false;
		wcCommand.charFlag = true;
		wcCommand.lineCount = 10;
		wcCommand.wordCount = 20;
		wcCommand.charCount = 30;
		wcCommand.printResults(FILE, stdout);
		assertEquals("30" + TAB + FILE + NEWLINE, stdout.toString());
	}
	
	/**
	 * Test helper method printResultsTotal
	 * input false for flags
	 * 
	 * @throw WcException
	 */
	@Test
	public void testPrintTotalResultsAllFalse() throws WcException {
		wcCommand.lineFlag = false;
		wcCommand.wordFlag = false;
		wcCommand.charFlag = false;
		wcCommand.totalLineCount = 100;
		wcCommand.totalWordCount = 200;
		wcCommand.totalCharCount = 300;
		wcCommand.printTotalResults(stdout);
		assertEquals(RESULTSALLOPT, stdout.toString());
	}
	
	/**
	 * Test helper method printResultsTotal LINE WORD CHAR
	 * 
	 * @throw WcException
	 */
	@Test
	public void testPrintTotalResultsLineWordChar() throws WcException {
		wcCommand.lineFlag = true;
		wcCommand.wordFlag = true;
		wcCommand.charFlag = true;
		wcCommand.totalLineCount = 100;
		wcCommand.totalWordCount = 200;
		wcCommand.totalCharCount = 300;
		wcCommand.printTotalResults(stdout);
		assertEquals(RESULTSALLOPT, stdout.toString());
	}
	
	/**
	 * Test helper method printResultsTotal LINE WORD
	 * 
	 * @throw WcException
	 */
	@Test
	public void testPrintTotalResultsLineWord() throws WcException {
		wcCommand.lineFlag = true;
		wcCommand.wordFlag = true;
		wcCommand.charFlag = false;
		wcCommand.totalLineCount = 100;
		wcCommand.totalWordCount = 200;
		wcCommand.totalCharCount = 300;
		wcCommand.printTotalResults(stdout);
		assertEquals("100" + TAB + "200" + TAB + "total", stdout.toString());
	}
	
	/**
	 * Test helper method printResultsTotal LINE CHAR
	 * 
	 * @throw WcException
	 */
	@Test
	public void testPrintTotalResultsLineChar() throws WcException {
		wcCommand.lineFlag = true;
		wcCommand.wordFlag = false;
		wcCommand.charFlag = true;
		wcCommand.totalLineCount = 100;
		wcCommand.totalWordCount = 200;
		wcCommand.totalCharCount = 300;
		wcCommand.printTotalResults(stdout);
		assertEquals("100" + TAB + "300" + TAB + "total", stdout.toString());
	}
	
	/**
	 * Test helper method printResultsTotal WORD CHAR
	 * 
	 * @throw WcException
	 */
	@Test
	public void testPrintTotalResultsWordChar() throws WcException {
		wcCommand.lineFlag = false;
		wcCommand.wordFlag = true;
		wcCommand.charFlag = true;
		wcCommand.totalLineCount = 100;
		wcCommand.totalWordCount = 200;
		wcCommand.totalCharCount = 300;
		wcCommand.printTotalResults(stdout);
		assertEquals("200" + TAB + "300" + TAB + "total", stdout.toString());
	}
	
	/**
	 * Test helper method printResultsTotal LINE
	 * 
	 * @throw WcException
	 */
	@Test
	public void testPrintTotalResultsLine() throws WcException {
		wcCommand.lineFlag = true;
		wcCommand.wordFlag = false;
		wcCommand.charFlag = false;
		wcCommand.totalLineCount = 100;
		wcCommand.totalWordCount = 200;
		wcCommand.totalCharCount = 300;
		wcCommand.printTotalResults(stdout);
		assertEquals("100" + TAB + "total", stdout.toString());
	}
	
	/**
	 * Test helper method printResultsTotal WORD
	 * 
	 * @throw WcException
	 */
	@Test
	public void testPrintTotalResultsWord() throws WcException {
		wcCommand.lineFlag = false;
		wcCommand.wordFlag = true;
		wcCommand.charFlag = false;
		wcCommand.totalLineCount = 100;
		wcCommand.totalWordCount = 200;
		wcCommand.totalCharCount = 300;
		wcCommand.printTotalResults(stdout);
		assertEquals("200" + TAB + "total", stdout.toString());
	}
	
	/**
	 * Test helper method printResultsTotal CHAR
	 * 
	 * @throw WcException
	 */
	@Test
	public void testPrintTotalResultsChar() throws WcException {
		wcCommand.lineFlag = false;
		wcCommand.wordFlag = false;
		wcCommand.charFlag = true;
		wcCommand.totalLineCount = 100;
		wcCommand.totalWordCount = 200;
		wcCommand.totalCharCount = 300;
		wcCommand.printTotalResults(stdout);
		assertEquals("300" + TAB + "total", stdout.toString());
	}
	
	/**
	 * Test helper method processInputStream
	 * input words to stdin
	 * 
	 * @throw WcException
	 */
	@Test
	public void testProcessInputStreamWords() throws WcException {
		stdin = new ByteArrayInputStream("Hello World".getBytes());
		wcCommand.processInputStream(stdin, stdout);
		//2 2 13
		assertEquals(1, wcCommand.lineCount);
		assertEquals(2, wcCommand.wordCount);
		assertEquals(12, wcCommand.charCount);
	}
	
	/**
	 * Test helper method processInputStream
	 * input lines of words to stdin
	 * 
	 * @throw WcException
	 */
	@Test
	public void testprocessInputStreamLines() throws WcException {
		stdin = new ByteArrayInputStream("Hello World\nHello\tEveryBody\nHello CS4218\n".getBytes());
		wcCommand.processInputStream(stdin, stdout);
		assertEquals(3, wcCommand.lineCount);
		assertEquals(6, wcCommand.wordCount);
		assertEquals(41, wcCommand.charCount);
	}
	
	/**
	 * Test helper method processInputStream
	 * input empty stdin
	 * 
	 * @throw WcException
	 */
	@Test
	public void testprocessInputStreamEmpty() throws WcException {
		stdin = new ByteArrayInputStream("".getBytes());
		wcCommand.processInputStream(stdin, stdout);
		assertEquals(0, wcCommand.lineCount);
		assertEquals(0, wcCommand.wordCount);
		assertEquals(0, wcCommand.charCount);
	}
}
