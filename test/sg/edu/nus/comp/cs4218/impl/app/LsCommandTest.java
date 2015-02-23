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

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import sg.edu.nus.comp.cs4218.exception.LsException;

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
   */
  @Test
  public void testGetFiles() throws IOException {
    List<File> files = lsCommand.getFiles(new File(FOLDERPARENT));
    assertEquals(files.size(), 4);
  }

  /**
   * Tests helper convert list of files to string
   */
  @Test
  public void testConvertFilesToString() {
    List<File> files = lsCommand.getFiles(new File(FOLDERPARENT));
    String converted = lsCommand.convertFilesToString(files);
    assertTrue(
        "The correct files/folders were not printed",
        converted.contains(FOLDERCHILD + File.separator)
            && converted.contains(FILE));
  }

  /**
   * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
   * list files with 2 args (more than 1)
   * 
   * @throws LsException
   */
  @Test
  public void testTwoArgs() throws LsException {
    expectedEx.expect(LsException.class);
    expectedEx.expectMessage("Invalid arguments");
    String args[] = { "src", "test" };
    lsCommand.run(args, stdin, stdout);
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
    lsCommand.run(args, stdin, stdout);
    assertEquals(stdout.toString(), FOLDERCHILD + File.separator + "\t" + FILE
        + "\t");
  }

  /**
   * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
   * list files at target directory
   * 
   * @throws LsException
   */
  @Test
  public void testTargetDirectory() throws LsException {
    String args[] = {FOLDERPARENT + File.separator + FOLDERCHILD};
    lsCommand.run(args, stdin, stdout);
    assertEquals(stdout.toString(), FILECHILD + "\t");
  }

}
