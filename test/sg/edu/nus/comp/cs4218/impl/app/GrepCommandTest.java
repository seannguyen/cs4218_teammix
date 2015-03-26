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
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.GrepException;

public class GrepCommandTest {
	private GrepCommand grepCommand;
	private InputStream stdin;
	private OutputStream stdout;
	private final static String NOFILEMSG = "grep: %1$s: No such file or directory" + Configurations.NEWLINE;
	private final static String ISDIRECTORYMSG = "grep: %1$s: Is a directory" + Configurations.NEWLINE;
	private final static String PATTERN_CS4218 = "CS4218";
	private final static String PATTERN_PDF = "pdf";
	private final static String CONTENT1 = "1. CS4218 Shell is a command interpreter that provides a set of tools (applications):" + Configurations.NEWLINE + "2. cd, pwd, ls, cat, echo, head, tail, grep, sed, find and wc." + Configurations.NEWLINE + "3. Apart from that, CS4218 Shell is a language for calling and combining these application." + Configurations.NEWLINE + "4. The language supports quoting of input data, semicolon operator for calling sequences of applications, command substitution and piping for connecting applications\' inputs and outputs, IO-redirection to load and save data processed by applications from/to files." + Configurations.NEWLINE + "5. More details can be found in \"Project Description.pdf\" in IVLE." + Configurations.NEWLINE + "6. Prerequisites" + Configurations.NEWLINE + "7. CS4218 Shell requires the following versions of software:" + Configurations.NEWLINE + "8. JDK 7" + Configurations.NEWLINE + "9. Eclipse 4.3" + Configurations.NEWLINE + "10. JUnit 4" + Configurations.NEWLINE + "11. Compiler compliance level must be <= 1.7" + Configurations.NEWLINE + "12. END-OF-FILE" + Configurations.NEWLINE;
	private final static String CONTENT2 = "1. CS4218 Shell is a command interpreter that provides a set of tools (applications):" + Configurations.NEWLINE + "2. cd, pwd, ls, cat, echo, head, tail, grep, sed, find and wc." + Configurations.NEWLINE + "3. Apart from that, CS4218 Shell is a language for calling and combining these application." + Configurations.NEWLINE + "4. The language supports quoting of input data, semicolon operator for calling sequences of applications, command substitution and piping for connecting applications\' inputs and outputs, IO-redirection to load and save data processed by applications from/to files." + Configurations.NEWLINE + "5. More details can be found in \"Project Description.pdf\" in IVLE." + Configurations.NEWLINE;
	private final static String FILE1_RESULT_PATTERN_CS4218 = "%1$s1. CS4218 Shell is a command interpreter that provides a set of tools (applications):\n%1$s3. Apart from that, CS4218 Shell is a language for calling and combining these application.\n%1$s7. CS4218 Shell requires the following versions of software:\n";
	private final static String FILE2_RESULT_PATTERN_CS4218 = "%1$s1. CS4218 Shell is a command interpreter that provides a set of tools (applications):\n%1$s3. Apart from that, CS4218 Shell is a language for calling and combining these application.\n";
	private final static String FILE1_RESULT_PATTERN_PDF = "%1$s5. More details can be found in \"Project Description.pdf\" in IVLE." + Configurations.NEWLINE;
	private final static String FILE2_RESULT_PATTERN_PDF = "%1$s5. More details can be found in \"Project Description.pdf\" in IVLE." + Configurations.NEWLINE;
	private final static String FOLDERTEST = "FolderTest";
	private final static String FOLDERTESTHIDE = ".FolderTestHide";
	private final static String FILE1 = "textFile1a.txt";
	private final static String FILE2 = "textFile1b.txt";
	private final static String FILEEMPTY = "textFile2.txt";
	private final static String FILEHIDE = ".textFile3.txt";
	private final static String FILEHIDEEMPTY = ".textFile4.txt";
	private final static Path PATH = Paths.get(FOLDERTEST);
	private final static Path PATHHIDE = Paths.get(FOLDERTESTHIDE);
	private final static Path PATHTOFILE1 = Paths.get(FILE1);
	private final static Path PATHTOFILE2 = Paths.get(FILE2);
	private final static Path PATHTOFILEEMPTY = Paths.get(FILEEMPTY);
	private final static Path PATHTOFILEHIDE = Paths.get(FILEHIDE);
	private final static Path PATHTOFILEHIDEEMPTY = Paths.get(FILEHIDEEMPTY);
	private final static Path PATHTOFOLDERFILE1 = Paths.get(FOLDERTEST + File.separator + FILE1);
	private final static Path PATHTOFOLDERFILE2 = Paths.get(FOLDERTEST + File.separator + FILE2);
	private final static Path PATHTOFOLDERFILEEMPTY = Paths.get(FOLDERTEST + File.separator + FILEEMPTY);
	private final static Path PATHTOFOLDERFILEHIDE = Paths.get(FOLDERTEST + File.separator + FILEHIDE);
	private final static Path PATHTOFOLDERFILEHIDEEMPTY = Paths.get(FOLDERTEST + File.separator + FILEHIDEEMPTY);
	private final static Path PATHTOHIDDENFOLDERFILE = Paths.get(FOLDERTESTHIDE + File.separator + FILE1);
	private final static Path PATHTOHIDDENFOLDERFILEEMPTY = Paths.get(FOLDERTESTHIDE + File.separator + FILEEMPTY);
	private final static Path PATHTOHIDDENFOLDERFILEHIDE = Paths.get(FOLDERTESTHIDE + File.separator + FILEHIDE);
	private final static Path PATHTOHIDDENFOLDERFILEHIDEEMPTY = Paths.get(FOLDERTESTHIDE + File.separator + FILEHIDEEMPTY);
	private final static Path[] FILESTOCREATE = {PATHTOFILE1, PATHTOFILE2, PATHTOFILEEMPTY, PATHTOFILEHIDE, PATHTOFILEHIDEEMPTY, PATHTOFOLDERFILE1, PATHTOFOLDERFILEEMPTY,
        PATHTOFOLDERFILEHIDE, PATHTOFOLDERFILEHIDEEMPTY,PATHTOHIDDENFOLDERFILE, PATHTOHIDDENFOLDERFILEEMPTY, PATHTOHIDDENFOLDERFILEHIDE, PATHTOHIDDENFOLDERFILEHIDEEMPTY};
	private final File workingDir = new File(System.getProperty("user.dir"));
	
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
    @BeforeClass
    public static void setUpBeforeClass() throws IOException {
        String[] arrayOfFiles = { PATHTOFILE1.toString(), PATHTOFILEHIDE.toString(),
        PATHTOFOLDERFILE1.toString(), PATHTOFOLDERFILEHIDE.toString(),
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
                bufferedWriter.write(CONTENT1);
                bufferedWriter.flush();
                fileWriter.flush();
                bufferedWriter.close();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        try {
            File file = new File(PATHTOFILE2.toString());
            file.setWritable(true);
            fileWriter = new FileWriter(file.getAbsoluteFile());
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(CONTENT2);            
            bufferedWriter.flush();
            fileWriter.flush();
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        try {
            File file = new File(PATHTOFOLDERFILE2.toString());
            file.setWritable(true);
            fileWriter = new FileWriter(file.getAbsoluteFile());
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(CONTENT2);            
            bufferedWriter.flush();
            fileWriter.flush();
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }       
    }
	
	@Before
	public void setUp() throws Exception {
		grepCommand = new GrepCommand();		
		stdout = new ByteArrayOutputStream();
		Environment.currentDirectory = System.getProperty("user.dir");
	}
	
	@After
	public void tearDown() throws Exception {
		grepCommand = null;
		stdout = null;
		stdin = null;
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws IOException {
	  try {
		Files.delete(PATHTOFILE1);
		Files.delete(PATHTOFILE2);;
		Files.delete(PATHTOFILEEMPTY);
		Files.delete(PATHTOFILEHIDE);
		Files.delete(PATHTOFILEHIDEEMPTY);
		Files.delete(PATHTOFOLDERFILE1);
		Files.delete(PATHTOFOLDERFILE2);
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
        //e.printStackTrace(); Ignore because windows cannot delete
	  }
	}

	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Grep a normal text file with contents in it
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGrepFile() throws GrepException {
		String pattern = PATTERN_CS4218;
		String[] args = {pattern, FILE1};
		grepCommand.run(args, stdin, stdout);
		assertEquals(String.format(FILE1_RESULT_PATTERN_CS4218, "", "", ""), stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Grep two text files with content in them
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGrepTwoFiles1() throws GrepException {
		String pattern = PATTERN_CS4218;
		String[] args = {pattern, FILE1, FILE2};
		grepCommand.run(args, stdin, stdout);
		assertEquals(String.format(FILE1_RESULT_PATTERN_CS4218, FILE1 + ":") + String.format(FILE2_RESULT_PATTERN_CS4218, FILE2 + ":"), stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Grep two text files with content in them
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGrepTwoFiles2() throws GrepException {
		String pattern = PATTERN_PDF;
		String[] args = {pattern, FILE1, FILE2};
		grepCommand.run(args, stdin, stdout);
		assertEquals(String.format(FILE1_RESULT_PATTERN_PDF, FILE1 + ":") + String.format(FILE2_RESULT_PATTERN_PDF, FILE2 + ":"), stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Grep three text files with one file that does not exist
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGrepThreeFilesWithOneDoesNotExist() throws GrepException {
		String pattern = PATTERN_PDF;
		String fileNameNotExist = "NoSuchFile.txt";
		String[] args = {pattern, FILE1, fileNameNotExist, FILE2};
		grepCommand.run(args, stdin, stdout);
		assertEquals(String.format(FILE1_RESULT_PATTERN_PDF, FILE1 + ":") + String.format(NOFILEMSG, fileNameNotExist) + String.format(FILE2_RESULT_PATTERN_PDF, FILE2 + ":"), stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Grep three text files with one valid folder
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGrepThreeFilesWithOneValidFolder() throws GrepException {
		String pattern = PATTERN_PDF;		
		String[] args = {pattern, FILE1, FOLDERTEST, FILE2};
		grepCommand.run(args, stdin, stdout);
		assertEquals(String.format(FILE1_RESULT_PATTERN_PDF, FILE1 + ":") + String.format(ISDIRECTORYMSG, FOLDERTEST) + String.format(FILE2_RESULT_PATTERN_PDF, FILE2 + ":"), stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Grep pattern without file
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGrepPatternWithoutFile() throws GrepException {
		String pattern = PATTERN_PDF;		
		String[] args = {pattern};
		expectedEx.expect(GrepException.class);
		expectedEx.expectMessage("Null stdin");
		grepCommand.run(args, stdin, stdout);
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Grep file without pattern
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGrepFileWithoutPattern() throws GrepException {				
		String[] args = {FILE1};
		expectedEx.expect(GrepException.class);
		expectedEx.expectMessage("Null stdin");
		grepCommand.run(args, stdin, stdout);		
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Grep files without pattern
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGrepFilesWithoutPattern() throws GrepException {				
		String[] args = {FILE1, FILE2, FILE1};		
		grepCommand.run(args, stdin, stdout);
		assertEquals("", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Grep four files where file does not exist and is a directory are included
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGrepFourFilesWithFileNotExistAndIsDirectory() throws GrepException {				
		String pattern = "pdf";
		String fileNameNotExist = "NoSuchFile.txt";
		String[] args = {pattern, FILE1, fileNameNotExist, FOLDERTEST, FILE2};		
		grepCommand.run(args, stdin, stdout);
		assertEquals(String.format(FILE1_RESULT_PATTERN_PDF, FILE1 + ":") + String.format(NOFILEMSG, fileNameNotExist) + String.format(ISDIRECTORYMSG, FOLDERTEST) + String.format(FILE2_RESULT_PATTERN_PDF, FILE2 + ":"), stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Grep a text file with nothing in it
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGrepFileEmpty() throws GrepException {
		String pattern = PATTERN_CS4218;
		String[] args = {pattern, FILEEMPTY};
		grepCommand.run(args, stdin, stdout);
		assertEquals("", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Grep a hidden text file with contents
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGrepFileHidden() throws GrepException {
		String pattern = PATTERN_CS4218;
		String[] args = {pattern, FILEHIDE};
		grepCommand.run(args, stdin, stdout);
		assertEquals(String.format(FILE1_RESULT_PATTERN_CS4218, "", "", ""), stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Grep a hidden text file nothing in it
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGrepFileHiddenEmpty() throws GrepException {
		String pattern = PATTERN_CS4218;
		String[] args = {pattern, FILEHIDEEMPTY};
		grepCommand.run(args, stdin, stdout);
		assertEquals("", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * grep a file in a folder
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGrepFolderFile() throws GrepException {
		String pattern = PATTERN_CS4218;
		String[] args = {pattern, FOLDERTEST + File.separator + FILE1};
		grepCommand.run(args, stdin, stdout);
		assertEquals(String.format(FILE1_RESULT_PATTERN_CS4218, "", "", ""), stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * grep files in folders
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGrepFolderFiles() throws GrepException {
		String pattern = PATTERN_CS4218;
		String[] args = {pattern, FOLDERTEST + File.separator + FILE1, FOLDERTEST + File.separator + FILE2};
		grepCommand.run(args, stdin, stdout);
		assertEquals(String.format(FILE1_RESULT_PATTERN_CS4218, FOLDERTEST + File.separator + FILE1 + ":") + String.format(FILE2_RESULT_PATTERN_CS4218, FOLDERTEST + File.separator + FILE2 + ":"), stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * grep a file(nothing in it) in a folder
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGrepFolderFileEmpty() throws GrepException {
		String pattern = PATTERN_CS4218;
		String[] args = {pattern, FOLDERTEST + File.separator + FILEEMPTY};
		grepCommand.run(args, stdin, stdout);
		assertEquals("", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Grep files(nothing in it) in folders
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGrepFolderFilesEmpty() throws GrepException {
		String pattern = PATTERN_CS4218;
		String[] args = {pattern, FOLDERTEST + File.separator + FILEEMPTY, FOLDERTEST + File.separator + FILEEMPTY};
		grepCommand.run(args, stdin, stdout);
		assertEquals("", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Grep a hidden file in a folder
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGrepFolderFileHidden() throws GrepException {
		String pattern = PATTERN_CS4218;
		String[] args = {pattern, FOLDERTEST + File.separator + FILEHIDE};
		grepCommand.run(args, stdin, stdout);
		assertEquals(String.format(FILE1_RESULT_PATTERN_CS4218, "", "", ""), stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Grep a hidden file(nothing in it) in a folder
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGrepFolderFileHiddenEmpty() throws GrepException {
		String pattern = PATTERN_CS4218;
		String[] args = {pattern, FOLDERTEST + File.separator + FILEHIDEEMPTY};
		grepCommand.run(args, stdin, stdout);
		assertEquals("", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Grep a file in a hidden folder
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGrepHiddenFolderFile() throws GrepException {
		String pattern = PATTERN_CS4218;
		String[] args = {pattern, FOLDERTESTHIDE + File.separator + FILE1};
		grepCommand.run(args, stdin, stdout);
		assertEquals(String.format(FILE1_RESULT_PATTERN_CS4218, "", "", ""), stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Grep a file(nothing in it) in a hidden folder
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGrepHiddenFolderFileEmpty() throws GrepException {
		String pattern = PATTERN_CS4218;
		String[] args = {pattern, FOLDERTESTHIDE + File.separator + FILEEMPTY};
		grepCommand.run(args, stdin, stdout);
		assertEquals("", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Grep a hidden file in a hidden folder
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGrepHiddenFolderFileHidden() throws GrepException {
		String pattern = PATTERN_CS4218;
		String[] args = {pattern, FOLDERTESTHIDE + File.separator + FILEHIDE};
		grepCommand.run(args, stdin, stdout);
		assertEquals(String.format(FILE1_RESULT_PATTERN_CS4218, "", "", ""), stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Grep a hidden file(nothing in it) in a hidden folder
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGrepHiddenFolderFileHiddenEmpty() throws GrepException {
		String pattern = PATTERN_CS4218;
		String[] args = {pattern, FOLDERTESTHIDE + File.separator + FILEHIDEEMPTY};
		grepCommand.run(args, stdin, stdout);
		assertEquals("", stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Grep a file that does not exist
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGrepNoSuchFile() throws GrepException {
		String pattern = PATTERN_CS4218;
		String fileNameNotExist = "NoSuchFile.txt";
		expectedEx.expect(GrepException.class);
		expectedEx.expectMessage(String.format(NOFILEMSG, fileNameNotExist));
		String[] args = {pattern, fileNameNotExist};
		grepCommand.run(args, stdin, stdout);		
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Grep a directory that does not exist
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGrepNoSuchDirectory() throws GrepException {
		String pattern = PATTERN_CS4218;
		String folderNameNotExist = "NoSuchFile";
		expectedEx.expect(GrepException.class);
		expectedEx.expectMessage(String.format(NOFILEMSG, folderNameNotExist));
		String[] args = {pattern, folderNameNotExist};		
		grepCommand.run(args, stdin, stdout);
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Grep a directory
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGrepDirectory() throws GrepException {
		String pattern = PATTERN_CS4218;
		expectedEx.expect(GrepException.class);
		expectedEx.expectMessage(String.format(ISDIRECTORYMSG, FOLDERTEST));
		String[] args = {pattern, FOLDERTEST};
		grepCommand.run(args, stdin, stdout);
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Grep a hidden file that does not exist
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGrepNoSuchHiddenFile() throws GrepException {
		String pattern = PATTERN_CS4218;
		String fileNameNotExist = ".NoSuchFile.txt";
		expectedEx.expect(GrepException.class);
		expectedEx.expectMessage(String.format(NOFILEMSG, fileNameNotExist));
		String[] args = {pattern, fileNameNotExist};
		grepCommand.run(args, stdin, stdout);					
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Grep a hidden directory
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGrepHiddenDirectory() throws GrepException {
		String pattern = PATTERN_CS4218;
		expectedEx.expect(GrepException.class);
		expectedEx.expectMessage(String.format(ISDIRECTORYMSG, FOLDERTESTHIDE));
		String[] args = {pattern, FOLDERTESTHIDE};
		grepCommand.run(args, stdin, stdout);
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Grep a hidden folder that does not exist
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGrepNoSuchHiddenDirectory() throws GrepException {
		String pattern = PATTERN_CS4218;
		String folderNameNotExist = ".NoSuchFile";
		expectedEx.expect(GrepException.class);
		expectedEx.expectMessage(String.format(NOFILEMSG, folderNameNotExist));
		String[] args = {pattern, folderNameNotExist};
		grepCommand.run(args, stdin, stdout);		
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Grep 2 files
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGrepTwoFilesWithOneHidden() throws GrepException {
		String pattern = PATTERN_CS4218;
		String[] args = {pattern, FILE1, FILEHIDE};
		grepCommand.run(args, stdin, stdout);
		assertEquals(String.format(FILE1_RESULT_PATTERN_CS4218, FILE1 + ":") + String.format(FILE1_RESULT_PATTERN_CS4218, FILEHIDE + ":"), stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Grep 3 files
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGrepThreeFiles() throws GrepException {
		String pattern = PATTERN_CS4218;
		String[] args = {pattern, FILE1, FILEEMPTY, FILE2};
		grepCommand.run(args, stdin, stdout);
		assertEquals(String.format(FILE1_RESULT_PATTERN_CS4218, FILE1 + ":") + String.format(FILE2_RESULT_PATTERN_CS4218, FILE2 + ":"), stdout.toString());
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Grep no file
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGrepNoFile() throws GrepException {
		String pattern = PATTERN_CS4218;
		expectedEx.expect(GrepException.class);
		expectedEx.expectMessage("Null stdin");
		String[] args = {pattern};
		grepCommand.run(args, stdin, stdout);		
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Grep a empty string pattern
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGrepEmptyPattern() throws GrepException {
		expectedEx.expect(GrepException.class);
		expectedEx.expectMessage("No pattern.");
		String[] args = {""};
		grepCommand.run(args, stdin, stdout);
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Grep a empty arg
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGrepEmptyArg() throws GrepException {
		expectedEx.expect(GrepException.class);
		expectedEx.expectMessage("No argument.");
		String[] args = {};
		grepCommand.run(args, stdin, stdout);
	}
	
	/**
	 * Test helper method getAbsolutePath
	 * input normal file
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGetAbsolutePath() throws GrepException {		
		String result = grepCommand.getAbsolutePath(FILE1);
		assertEquals(workingDir.getAbsolutePath() + File.separator + FILE1 ,result);
	}
	
	/**
	 * Test helper method getAbsolutePath
	 * input normal folder
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGetAbsolutePathInFolder() throws GrepException {		
		String result = grepCommand.getAbsolutePath(FOLDERTEST + File.separator + FILE1);
		assertEquals(workingDir.getAbsolutePath() + File.separator + FOLDERTEST + File.separator + FILE1 ,result);
	}
	
	/**
	 * Test helper method getAbsolutePath
	 * input hidden file in folder
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGetAbsolutePathInFolderHiddenFile() throws GrepException {		
		String result = grepCommand.getAbsolutePath(FOLDERTEST + File.separator + FILEHIDE);
		assertEquals(workingDir.getAbsolutePath() + File.separator + FOLDERTEST + File.separator + FILEHIDE ,result);
	}
	
	/**
	 * Test helper method getAbsolutePath
	 * input file in hidden folder
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGetAbsolutePathInHiddenFolder() throws GrepException {		
		String result = grepCommand.getAbsolutePath(FOLDERTESTHIDE + File.separator + FILE1);
		assertEquals(workingDir.getAbsolutePath() + File.separator + FOLDERTESTHIDE + File.separator + FILE1 ,result);
	}
	
	/**
	 * Test helper method getAbsolutePath
	 * input hidden file in hidden folder
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testGetAbsolutePathInHiddenFolderHiddenFile() throws GrepException {		
		String result = grepCommand.getAbsolutePath(FOLDERTESTHIDE + File.separator + FILEHIDEEMPTY);
		assertEquals(workingDir.getAbsolutePath() + File.separator + FOLDERTESTHIDE + File.separator + FILEHIDEEMPTY ,result);
	}
	
	/**
	 * Test helper method doesFileExist
	 * input file that exist
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testDoesFileExist() throws GrepException {
		File file = new File(FILE1);
		Boolean result = grepCommand.doesFileExist(file);
		assertTrue(result);
	}
	
	/**
	 * Test helper method doesFileExist
	 * input hidden file that exist
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testDoesFileExistHidden() throws GrepException {
		File file = new File(FILEHIDEEMPTY);
		Boolean result = grepCommand.doesFileExist(file);
		assertTrue(result);
	}
	
	/**
	 * Test helper method doesFileExist
	 * input file that does not exist
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testDoesFileExistNoSuchFile() throws GrepException {
		File file = new File("NoSuchFile.txt");
		Boolean result = grepCommand.doesFileExist(file);
		assertFalse(result);
	}
	
	/**
	 * Test helper method doesFileExist
	 * input folder that exist
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testDoesFileExistFolder() throws GrepException {
		File file = new File(FOLDERTEST);
		Boolean result = grepCommand.doesFileExist(file);
		assertFalse(result);
	}
	
	/**
	 * Test helper method isDirectory
	 * input a folder
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testIsDirectory() throws GrepException {
		File file = new File(FOLDERTEST);
		Boolean result = grepCommand.isDirectory(file);
		assertTrue(result);
	}
	
	/**
	 * Test helper method isDirectory
	 * input hidden folder
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testIsDirectoryHidden() throws GrepException {
		File file = new File(FOLDERTESTHIDE);
		Boolean result = grepCommand.isDirectory(file);
		assertTrue(result);
	}
	
	/**
	 * Test helper method isDirectory
	 * input folder that does not exist
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testIsDirectoryNoSuchDirectory() throws GrepException {
		File file = new File("NoSuchFolder");
		Boolean result = grepCommand.isDirectory(file);
		assertFalse(result);
	}
	
	/**
	 * Test helper method processInputStream
	 * input stdin
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testprocessStdin() throws GrepException {
		String pattern = "abc";
		String input = "1.abc" + Configurations.NEWLINE + "2.Abc" + Configurations.NEWLINE + "3.aBc" + Configurations.NEWLINE + "4.abC" + Configurations.NEWLINE + "5.ABc" + Configurations.NEWLINE + "6.ABC" + Configurations.NEWLINE + "7.abc" + Configurations.NEWLINE;
		stdin = new ByteArrayInputStream(input.getBytes());
		grepCommand.processStdin(pattern, stdin, stdout);
		assertEquals("1.abc" + Configurations.NEWLINE + "7.abc" + Configurations.NEWLINE, stdout.toString());
	}
	
	/**
	 * Test helper method processInputStream
	 * input empty stdin
	 * 
	 * @throw GrepException
	 */
	@Test
	public void testprocessStdinEmpty() throws GrepException {
		String pattern = "";
		stdin = new ByteArrayInputStream("".getBytes());
		grepCommand.processStdin(pattern, stdin, stdout);
		assertEquals("", stdout.toString());
	}

}
