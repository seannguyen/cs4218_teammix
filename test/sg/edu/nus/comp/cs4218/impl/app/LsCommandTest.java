package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
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
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import sg.edu.nus.comp.cs4218.Configurations;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.LsException;
import sg.edu.nus.comp.cs4218.exception.SedException;

public class LsCommandTest {
	private LsCommand lsCommand;
	final private static String FOLDERPARENT = "folderParentTemp";
	final private static String FOLDERCHILD = "folderChildTemp";
	final private static String FOLDERCHILDDOT = ".folderChildTemp";
	final private static String FILE = "tempFile.txt";
	final private static String FILECHILD = "tempFileCHILD.txt";
	final private static String FILEDOT = ".tempFile.txt";
	final private static Path PATH = Paths.get(FOLDERPARENT + File.separator
			+ FOLDERCHILD);
	final private static Path PATHDOT = Paths.get(FOLDERPARENT + File.separator
			+ FOLDERCHILDDOT);
	final private static Path PATHTOFILE = Paths.get(FOLDERPARENT
			+ File.separator + FILE);
	final private static Path PATHTOFILECHILD = Paths.get(FOLDERPARENT
			+ File.separator + FOLDERCHILD + File.separator + FILECHILD);
	final private static Path PATHTOFILEDOT = Paths.get(FOLDERPARENT
			+ File.separator + FILEDOT);
	private final File workingDir = new File(System.getProperty("user.dir"));
	private InputStream stdin;
	private OutputStream stdout;
	PrintStream printStream;

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		Files.createDirectories(PATH);
		Files.createDirectories(PATHDOT);
		try {
			Files.createFile(PATHTOFILE);
			Files.createFile(PATHTOFILEDOT);
			Files.createFile(PATHTOFILECHILD);
		} catch (FileAlreadyExistsException e) {
			System.err.println("File already exists: " + e.getMessage());
		}
	}

	@Before
	public void setUp() throws Exception {
		lsCommand = new LsCommand();
		stdout = new java.io.ByteArrayOutputStream();
		printStream = new PrintStream(stdout);
		Environment.currentDirectory = workingDir.getAbsolutePath();
	}

	@After
	public void tearDown() {
		Environment.currentDirectory = System.getProperty("user.dir");
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws IOException {
		Files.delete(PATHTOFILE);
		Files.delete(PATHTOFILEDOT);
		Files.delete(PATHTOFILECHILD);
		Files.delete(PATH);
		Files.delete(PATHDOT);
		Files.delete(Paths.get(FOLDERPARENT));
	}

	/**
	 * Tests the helper method for getting the file list
	 * 
	 * @throws IOException
	 * @throws LsException 
	 */
	@Test
	public void testGetFiles() throws IOException, LsException {
		List<File> files = lsCommand.getFiles(new File(FOLDERPARENT));
		assertEquals(files.size(), 4);
	}

	/**
	 * Tests helper convert list of files to string
	 * @throws LsException 
	 */
	@Test
	public void testConvertFilesToString() throws LsException {
		List<File> files = lsCommand.getFiles(new File(FOLDERPARENT));
		String converted = lsCommand.convertFilesToString(files);
		assertTrue(
				"The correct files/folders were not printed",
				converted.contains(FOLDERCHILD + File.separator)
						&& converted.contains(FILE));
	}

	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * list files with 2 txt args (more than 1) one of with a . infront
     */
	@Test (expected = LsException.class)
	public void testTwoArgs() throws LsException {
		String args[] = { "test-files-basic" + System.getProperty("file.separator") 
		    + "One.txt", "test-files-basic"+ System.getProperty("file.separator") 
		    + ".Two.txt" };
		String expected = "One.txt\t";
		
		stdout = new ByteArrayOutputStream();
		lsCommand.run(args, stdin, stdout);
		Assert.assertEquals(expected + System.lineSeparator(), stdout.toString());
	}
	
	/**
     * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
     * list files with 2 txt args 2 folder args (more than 1) 2 files with . infront
     */
    @Test (expected = LsException.class)
    public void testFourArgs() throws LsException {
        String args[] = { "test-files-basic" + System.getProperty("file.separator") 
            + "One.txt", "test-files-basic"+ System.getProperty("file.separator") 
            + ".Two.txt" , "test-files-basic"+ System.getProperty("file.separator") 
            + ".FolderTestHide", "test-files-basic"+ System.getProperty("file.separator") 
            + "NormalFolder"};
        String expected = "One.txt\t" +System.lineSeparator()+ Configurations.NEWLINE +
                ".FolderTestHide: Does not exist" + Configurations.NEWLINE + Configurations.NEWLINE
            + "NormalFolder:" + System.lineSeparator()
            + "Normal.txt\t";
        
        stdout = new ByteArrayOutputStream();
        lsCommand.run(args, stdin, stdout);
        Assert.assertEquals(expected + System.lineSeparator(), stdout.toString());
    }

	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * list files with 0 args
	 * 
	 * @throws LsException
	 */
	@Test
	public void testZeroArg() throws LsException {
		String args[] = {};
		Environment.currentDirectory = FOLDERPARENT + File.separator
            + FOLDERCHILD;
		lsCommand.run(args, stdin, stdout);
		assertEquals(FILECHILD  + "\t" + System.lineSeparator(), stdout.toString());
	}

	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * list files at target directory
	 * 
	 * @throws LsException
	 */
	@Test
	public void testTargetDirectory() throws LsException {
		String args[] = { FOLDERPARENT + File.separator + FOLDERCHILD };
		lsCommand.run(args, stdin, stdout);
		assertEquals(FILECHILD + "\t" + System.lineSeparator(), stdout.toString());
	}

	 /**
     * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
     * list files at missing directory file
     * 
     * @throws LsException
     */
    @Test(expected = LsException.class)
    public void testMissingDirectory() throws LsException {
        String args[] = { "Foo" };
        lsCommand.run(args, stdin, stdout);
    }
    
    /**
     * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
     * list files at non directory file
     * 
     * @throws LsException
     */
    @Test(expected = LsException.class)
    public void testNonDirectory() throws LsException {
        String args[] = { "test-files-basic" + File.separator + "One.txt"};
        lsCommand.run(args, stdin, stdout);
    }
    
    /**
     * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
     * list files at . directory file
     * 
     * @throws LsException
     */
    @Test
    public void testDotDirectory() throws LsException {
        String args[] = { "test-files-basic" + File.separator + ".HideFolder"};
        lsCommand.run(args, stdin, stdout);
        String expected = "textFile1.txt\ttextFile2.txt\t";
        Assert.assertEquals(expected + System.lineSeparator(), stdout.toString());
    }
	
}
