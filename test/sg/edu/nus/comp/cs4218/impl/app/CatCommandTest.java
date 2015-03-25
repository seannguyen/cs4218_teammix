package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

public class CatCommandTest {
	private CatCommand catCommand;
	private InputStream stdin;
	private OutputStream stdout;
	private final static String NOFILEMSG = "No such file or directory";
	private final static String CONTENT = "1. CS4218 Shell is a command interpreter that provides a set of tools (applications):\n2. cd, pwd, ls, cat, echo, head, tail, grep, sed, find and wc.\n3. Apart from that, CS4218 Shell is a language for calling and combining these application.\n4. The language supports quoting of input data, semicolon operator for calling sequences of applications, command substitution and piping for connecting applications\' inputs and outputs, IO-redirection to load and save data processed by applications from/to files.\n5. More details can be found in \"Project Description.pdf\" in IVLE.\n6. Prerequisites\n7. CS4218 Shell requires the following versions of software:\n8. JDK 7\n9. Eclipse 4.3\n10. JUnit 4\n11. Compiler compliance level must be <= 1.7\n12. END-OF-FILE\n";
	private final static String CONTENTNEWLINE = "1. CS4218 Shell is a command interpreter that provides a set of tools (applications):" + Configurations.NEWLINE + "2. cd, pwd, ls, cat, echo, head, tail, grep, sed, find and wc." + Configurations.NEWLINE + "3. Apart from that, CS4218 Shell is a language for calling and combining these application." + Configurations.NEWLINE + "4. The language supports quoting of input data, semicolon operator for calling sequences of applications, command substitution and piping for connecting applications\' inputs and outputs, IO-redirection to load and save data processed by applications from/to files." + Configurations.NEWLINE + "5. More details can be found in \"Project Description.pdf\" in IVLE." + Configurations.NEWLINE + "6. Prerequisites" + Configurations.NEWLINE + "7. CS4218 Shell requires the following versions of software:" + Configurations.NEWLINE + "8. JDK 7" + Configurations.NEWLINE + "9. Eclipse 4.3" + Configurations.NEWLINE + "10. JUnit 4" + Configurations.NEWLINE + "11. Compiler compliance level must be <= 1.7" + Configurations.NEWLINE + "12. END-OF-FILE" + Configurations.NEWLINE + "";
	private final static String FOLDERTEST = "FolderTest";
	private final static String FOLDERTESTHIDE = ".FolderTestHide";
	private final static String FILE = "textFile1.txt";
	private final static String FILEEMPTY = "textFile2.txt";
	private final static String FILEHIDE = ".textFile3.txt";
	private final static String FILEHIDEEMPTY = ".textFile4.txt";
	private final static Path PATH = Paths.get(FOLDERTEST);
	private final static Path PATHHIDE = Paths.get(FOLDERTESTHIDE);
	private final static Path PATHTOFILE = Paths.get(FILE);
	private final static Path PATHTOFILEEMPTY = Paths.get(FILEEMPTY);
	private final static Path PATHTOFILEHIDE = Paths.get(FILEHIDE);
	private final static Path PATHTOFILEHIDEEMPTY = Paths.get(FILEHIDEEMPTY);
	private final static Path PATHTOFOLDERFILE = Paths.get(FOLDERTEST + File.separator + FILE);
	private final static Path PATHTOFOLDERFILEEMPTY = Paths.get(FOLDERTEST + File.separator + FILEEMPTY);
	private final static Path PATHTOFOLDERFILEHIDE = Paths.get(FOLDERTEST + File.separator + FILEHIDE);
	private final static Path PATHTOFOLDERFILEHIDEEMPTY = Paths.get(FOLDERTEST + File.separator + FILEHIDEEMPTY);
	private final static Path PATHTOHIDDENFOLDERFILE = Paths.get(FOLDERTESTHIDE + File.separator + FILE);
	private final static Path PATHTOHIDDENFOLDERFILEEMPTY = Paths.get(FOLDERTESTHIDE + File.separator + FILEEMPTY);
	private final static Path PATHTOHIDDENFOLDERFILEHIDE = Paths.get(FOLDERTESTHIDE + File.separator + FILEHIDE);
	private final static Path PATHTOHIDDENFOLDERFILEHIDEEMPTY = Paths.get(FOLDERTESTHIDE + File.separator + FILEHIDEEMPTY);
	private final static Path[] FILESTOCREATE = {PATHTOFILE,PATHTOFILEEMPTY, PATHTOFILEHIDE, PATHTOFILEHIDEEMPTY, PATHTOFOLDERFILE, PATHTOFOLDERFILEEMPTY,
        PATHTOFOLDERFILEHIDE, PATHTOFOLDERFILEHIDEEMPTY,PATHTOHIDDENFOLDERFILE, PATHTOHIDDENFOLDERFILEEMPTY, PATHTOHIDDENFOLDERFILEHIDE, PATHTOHIDDENFOLDERFILEHIDEEMPTY};
	private final File workingDir = new File(System.getProperty("user.dir"));
	
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
    @BeforeClass
    public static void setUpBeforeClass() throws IOException {
        String[] arrayOfFiles = { PATHTOFILE.toString(), PATHTOFILEHIDE.toString(),
        PATHTOFOLDERFILE.toString(), PATHTOFOLDERFILEHIDE.toString(),
        PATHTOHIDDENFOLDERFILE.toString(),
        PATHTOHIDDENFOLDERFILEHIDE.toString() };
        Files.createDirectories(PATH);
        Files.createDirectories(PATHHIDE);
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        for(int i = 0; i < FILESTOCREATE.length; i++) {
            try {
                Files.createFile(FILESTOCREATE[i]);
            } catch (FileAlreadyExistsException e) {
                System.err.println("File already exists: " + e.getMessage());
            }   
        }
       
        for (int i = 0; i < arrayOfFiles.length; i++) {
            try {
                File file = new File(arrayOfFiles[i]);
                file.setWritable(true);
                fileWriter = new FileWriter(file.getAbsoluteFile());
                bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(CONTENT);
                bufferedWriter.flush();
                fileWriter.flush();
                bufferedWriter.close();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
	
	@Before
	public void setUp() throws Exception {
		catCommand = new CatCommand();		
		stdout = new ByteArrayOutputStream();
	}
	
	@After
	public void tearDown() throws Exception {
		catCommand = null;
		stdout = null;
		stdin = null;
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws IOException {
	  try {
		Files.delete(PATHTOFILE);
		Files.delete(PATHTOFILEEMPTY);
		Files.delete(PATHTOFILEHIDE);
		Files.delete(PATHTOFILEHIDEEMPTY);
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
        //e.printStackTrace(); Ingore becos windows cannot delete
	  }
	}

	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Cat a normal text file with contents in it
	 * 
	 * @throw CatException
	 */
	@Test
	public void testCatFile() throws CatException {
		String[] args = {FILE};
		catCommand.run(args, stdin, stdout);
		assertEquals(CONTENTNEWLINE, stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Cat a text file with nothing in it
	 * 
	 * @throw CatException
	 */
	@Test
	public void testCatFileEmpty() throws CatException {
		String[] args = {FILEEMPTY};
		catCommand.run(args, stdin, stdout);
		assertEquals("", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Cat a hidden text file with contents
	 * 
	 * @throw CatException
	 */
	@Test
	public void testCatFileHidden() throws CatException {
		String[] args = {FILEHIDE};
		catCommand.run(args, stdin, stdout);
		assertEquals(CONTENTNEWLINE, stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Cat a hidden text file nothing in it
	 * 
	 * @throw CatException
	 */
	@Test
	public void testCatFileHiddenEmpty() throws CatException {
		String[] args = {FILEHIDEEMPTY};
		catCommand.run(args, stdin, stdout);
		assertEquals("", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Cat a file in a folder
	 * 
	 * @throw CatException
	 */
	@Test
	public void testCatFolderFile() throws CatException {
		String[] args = {FOLDERTEST + File.separator + FILE};
		catCommand.run(args, stdin, stdout);
		assertEquals(CONTENTNEWLINE, stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Cat a file(nothing in it) in a folder
	 * 
	 * @throw CatException
	 */
	@Test
	public void testCatFolderFileEmpty() throws CatException {
		String[] args = {FOLDERTEST + File.separator + FILEEMPTY};
		catCommand.run(args, stdin, stdout);
		assertEquals("", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Cat a hidden file in a folder
	 * 
	 * @throw CatException
	 */
	@Test
	public void testCatFolderFileHidden() throws CatException {
		String[] args = {FOLDERTEST + File.separator + FILEHIDE};
		catCommand.run(args, stdin, stdout);
		assertEquals(CONTENTNEWLINE, stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Cat a hidden file(nothing in it) in a folder
	 * 
	 * @throw CatException
	 */
	@Test
	public void testCatFolderFileHiddenEmpty() throws CatException {
		String[] args = {FOLDERTEST + File.separator + FILEHIDEEMPTY};
		catCommand.run(args, stdin, stdout);
		assertEquals("", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Cat a file in a hidden folder
	 * 
	 * @throw CatException
	 */
	@Test
	public void testCatHiddenFolderFile() throws CatException {
		String[] args = {FOLDERTESTHIDE + File.separator + FILE};
		catCommand.run(args, stdin, stdout);
		assertEquals(CONTENTNEWLINE, stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Cat a file(nothing in it) in a hidden folder
	 * 
	 * @throw CatException
	 */
	@Test
	public void testCatHiddenFolderFileEmpty() throws CatException {
		String[] args = {FOLDERTESTHIDE + File.separator + FILEEMPTY};
		catCommand.run(args, stdin, stdout);
		assertEquals("", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Cat a hidden file in a hidden folder
	 * 
	 * @throw CatException
	 */
	@Test
	public void testCatHiddenFolderFileHidden() throws CatException {
		String[] args = {FOLDERTESTHIDE + File.separator + FILEHIDE};
		catCommand.run(args, stdin, stdout);
		assertEquals(CONTENTNEWLINE, stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Cat a hidden file(nothing in it) in a hidden folder
	 * 
	 * @throw CatException
	 */
	@Test
	public void testCatHiddenFolderFileHiddenEmpty() throws CatException {
		String[] args = {FOLDERTESTHIDE + File.separator + FILEHIDEEMPTY};
		catCommand.run(args, stdin, stdout);
		assertEquals("", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Cat a file that does not exist
	 * 
	 * @throw CatException
	 */
	@Test
	public void testCatNoSuchFile() throws CatException {
		expectedEx.expect(CatException.class);
		expectedEx.expectMessage("No such file or directory");
		String[] args = {"NoSuchFile.txt"};
		catCommand.run(args, stdin, stdout);		
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Cat a directory that does not exist
	 * 
	 * @throw CatException
	 */
	@Test
	public void testCatNoSuchDirectory() throws CatException {
		expectedEx.expect(CatException.class);
		expectedEx.expectMessage("No such file or directory");
		String[] args = {"NoSuchFile"};		
		catCommand.run(args, stdin, stdout);
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Cat a directory
	 * 
	 * @throw CatException
	 */
	@Test
	public void testCatDirectory() throws CatException {
		expectedEx.expect(CatException.class);
		expectedEx.expectMessage("Is a directory");
		String[] args = {FOLDERTEST};
		catCommand.run(args, stdin, stdout);
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Cat a hidden file that does not exist
	 * 
	 * @throw CatException
	 */
	@Test
	public void testCatNoSuchHiddenFile() throws CatException {
		expectedEx.expect(CatException.class);
		expectedEx.expectMessage("No such file or directory");
		String[] args = {".NoSuchFile.txt"};
		catCommand.run(args, stdin, stdout);					
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Cat a hidden directory
	 * 
	 * @throw CatException
	 */
	@Test
	public void testCatHiddenDirectory() throws CatException {
		expectedEx.expect(CatException.class);
		expectedEx.expectMessage("Is a directory");
		String[] args = {FOLDERTESTHIDE};
		catCommand.run(args, stdin, stdout);
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Cat a hidden folder that does not exist
	 * 
	 * @throw CatException
	 */
	@Test
	public void testCatNoSuchHiddenDirectory() throws CatException {
		expectedEx.expect(CatException.class);
		expectedEx.expectMessage("No such file or directory");
		String[] args = {".NoSuchFile"};
		catCommand.run(args, stdin, stdout);		
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Cat 2 files
	 * 
	 * @throw CatException
	 */
	@Test
	public void testCatTwoFiles() throws CatException {
		String[] args = {FILE, FILEHIDE};
		catCommand.run(args, stdin, stdout);
		assertEquals(CONTENTNEWLINE + CONTENTNEWLINE, stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Cat 3 files
	 * 
	 * @throw CatException
	 */
	@Test
	public void testCatThreeFiles() throws CatException {
		String[] args = {FILE, FILEEMPTY, FILEHIDE};
		catCommand.run(args, stdin, stdout);
		assertEquals(CONTENTNEWLINE + CONTENTNEWLINE, stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * cat zero args
	 * 
	 * @throw CatException
	 */
	@Test
	public void testCatNoFile() throws CatException {
		expectedEx.expect(CatException.class);
		expectedEx.expectMessage("Null stdin");
		String[] args = {};
		catCommand.run(args, stdin, stdout);		
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Cat a empty string
	 * 
	 * @throw CatException
	 */
	@Test
	public void testCatEmptyArg() throws CatException {
		expectedEx.expect(CatException.class);
		expectedEx.expectMessage("Null argument(s)");
		String[] args = {""};
		catCommand.run(args, stdin, stdout);
	}
	
	/**
	 * Test helper method getAbsolutePath
	 * input normal file
	 * 
	 * @throw CatException
	 */
	@Test
	public void testGetAbsolutePath() throws CatException {		
		String result = catCommand.getAbsolutePath(FILE);
		assertEquals(workingDir.getAbsolutePath() + File.separator + FILE ,result);
	}
	
	/**
	 * Test helper method getAbsolutePath
	 * input normal folder
	 * 
	 * @throw CatException
	 */
	@Test
	public void testGetAbsolutePathInFolder() throws CatException {		
		String result = catCommand.getAbsolutePath(FOLDERTEST + File.separator + FILE);
		assertEquals(workingDir.getAbsolutePath() + File.separator + FOLDERTEST + File.separator + FILE ,result);
	}
	
	/**
	 * Test helper method getAbsolutePath
	 * input hidden file in folder
	 * 
	 * @throw CatException
	 */
	@Test
	public void testGetAbsolutePathInFolderHiddenFile() throws CatException {		
		String result = catCommand.getAbsolutePath(FOLDERTEST + File.separator + FILEHIDE);
		assertEquals(workingDir.getAbsolutePath() + File.separator + FOLDERTEST + File.separator + FILEHIDE ,result);
	}
	
	/**
	 * Test helper method getAbsolutePath
	 * input file in hidden folder
	 * 
	 * @throw CatException
	 */
	@Test
	public void testGetAbsolutePathInHiddenFolder() throws CatException {		
		String result = catCommand.getAbsolutePath(FOLDERTESTHIDE + File.separator + FILE);
		assertEquals(workingDir.getAbsolutePath() + File.separator + FOLDERTESTHIDE + File.separator + FILE ,result);
	}
	
	/**
	 * Test helper method getAbsolutePath
	 * input hidden file in hidden folder
	 * 
	 * @throw CatException
	 */
	@Test
	public void testGetAbsolutePathInHiddenFolderHiddenFile() throws CatException {		
		String result = catCommand.getAbsolutePath(FOLDERTESTHIDE + File.separator + FILEHIDEEMPTY);
		assertEquals(workingDir.getAbsolutePath() + File.separator + FOLDERTESTHIDE + File.separator + FILEHIDEEMPTY ,result);
	}
	
	/**
	 * Test helper method doesFileExist
	 * input file that exist
	 * 
	 * @throw CatException
	 */
	@Test
	public void testDoesFileExist() throws CatException {
		File file = new File(FILE);
		Boolean result = catCommand.doesFileExist(file);
		assertTrue(result);
	}
	
	/**
	 * Test helper method doesFileExist
	 * input hidden file that exist
	 * 
	 * @throw CatException
	 */
	@Test
	public void testDoesFileExistHidden() throws CatException {
		File file = new File(FILEHIDEEMPTY);
		Boolean result = catCommand.doesFileExist(file);
		assertTrue(result);
	}
	
	/**
	 * Test helper method doesFileExist
	 * input file that does not exist
	 * 
	 * @throw CatException
	 */
	@Test
	public void testDoesFileExistNoSuchFile() throws CatException {
		File file = new File("NoSuchFile.txt");
		Boolean result = catCommand.doesFileExist(file);
		assertFalse(result);
	}
	
	/**
	 * Test helper method doesFileExist
	 * input folder that exist
	 * 
	 * @throw CatException
	 */
	@Test
	public void testDoesFileExistFolder() throws CatException {
		File file = new File(FOLDERTEST);
		Boolean result = catCommand.doesFileExist(file);
		assertFalse(result);
	}
	
	/**
	 * Test helper method isDirectory
	 * input a folder
	 * 
	 * @throw CatException
	 */
	@Test
	public void testIsDirectory() throws CatException {
		File file = new File(FOLDERTEST);
		Boolean result = catCommand.isDirectory(file);
		assertTrue(result);
	}
	
	/**
	 * Test helper method isDirectory
	 * input hidden folder
	 * 
	 * @throw CatException
	 */
	@Test
	public void testIsDirectoryHidden() throws CatException {
		File file = new File(FOLDERTESTHIDE);
		Boolean result = catCommand.isDirectory(file);
		assertTrue(result);
	}
	
	/**
	 * Test helper method isDirectory
	 * input folder that does not exist
	 * 
	 * @throw CatException
	 */
	@Test
	public void testIsDirectoryNoSuchDirectory() throws CatException {
		File file = new File("NoSuchFolder");
		Boolean result = catCommand.isDirectory(file);
		assertFalse(result);
	}
	
	/**
	 * Test helper method processInputStream
	 * input words to stdin
	 * 
	 * @throw CatException
	 */
	@Test
	public void testProcessInputStreamWords() throws CatException {
		stdin = new ByteArrayInputStream("Hello World\n".getBytes());
		catCommand.processInputStream(stdin, stdout);
		assertEquals("Hello World" + Configurations.NEWLINE, stdout.toString());
	}
	
	/**
	 * Test helper method processInputStream
	 * input lines of words to stdin
	 * 
	 * @throw CatException
	 */
	@Test
	public void testprocessInputStreamLines() throws CatException {
		stdin = new ByteArrayInputStream("Hello World\nHello\tEveryBody\nHello CS4218\n".getBytes());
		catCommand.processInputStream(stdin, stdout);
		assertEquals("Hello World\nHello\tEveryBody\nHello CS4218\n", stdout.toString());
	}
	
	/**
	 * Test helper method processInputStream
	 * input empty stdin
	 * 
	 * @throw CatException
	 */
	@Test
	public void testprocessInputStreamEmpty() throws CatException {
		stdin = new ByteArrayInputStream("".getBytes());
		catCommand.processInputStream(stdin, stdout);
		assertEquals("", stdout.toString());
	}
}
