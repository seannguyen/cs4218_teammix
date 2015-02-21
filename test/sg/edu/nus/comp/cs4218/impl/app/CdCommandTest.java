package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CdException;
import sg.edu.nus.comp.cs4218.impl.app.CdCommand;

public class CdCommandTest {
  private CdCommand cdCommand;
  private File fileTemp, folderTemp1, folderTemp2;
  private File workingDir = new File(System.getProperty("user.dir"));
  private InputStream stdin;
  private OutputStream stdout;
  
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();
  
  @Before
  public void setUp() throws Exception {
    fileTemp = File.createTempFile("cd1", "");
    folderTemp1 = Files.createTempDirectory("cdFolderTemp1").toFile();
    folderTemp2 = Files.createTempDirectory(workingDir.toPath(), "cdFolderTemp2").toFile();
    Environment.currentDirectory = workingDir.getAbsolutePath();
  }

  @After
  public void tearDown() throws Exception {
      fileTemp.delete();
      folderTemp1.delete();
      folderTemp2.delete();
      cdCommand = null;
      stdin = null;
      stdout = null;
  }
  
  /**
   *  Testing File changeDirectory(String newDirectory)
   *  newDirectory is null
   */
  @Test
  public void testChangeDirectoryToNull() {
    cdCommand = new CdCommand();
    assertNull(cdCommand.changeDirectory(null));
  }
  
  /**
   *  Testing File changeDirectory(String newDirectory)
   *  newDirectory is empty String
   */
  @Test
  public void testChangeDirectoryToEmptyString() {
    cdCommand = new CdCommand();
    assertNull(cdCommand.changeDirectory(""));
  }

  /** 
   * Test void run(String[] args, InputStream stdin, OutputStream stdout)
   * Test absolute path to non-directory
   */
  @Test
  public void testChangeDirectoryToFile() throws CdException {
    cdCommand = new CdCommand();
    assertNull(cdCommand.changeDirectory(fileTemp.getAbsolutePath()));
    expectedEx.expect(CdException.class);
    expectedEx.expectMessage("Not a directory");
    String args[] = { fileTemp.getAbsolutePath() };
    cdCommand.run(args, stdin, stdout);
  }
  
  /** 
   * Test void run(String[] args, InputStream stdin, OutputStream stdout)
   * Test invalid args size 2 (more than 1)
   */
  @Test
  public void testTwoArgs() throws CdException {
    cdCommand = new CdCommand();
    expectedEx.expect(CdException.class);
    expectedEx.expectMessage("Invalid arguments");
    String args[] = { fileTemp.getAbsolutePath(), "src"};
    cdCommand.run(args, stdin, stdout);
  }
  
  /** 
   * Test void run(String[] args, InputStream stdin, OutputStream stdout)
   * Test invalid args size 5 (more than 1)
   */
  @Test
  public void testFiveArgs() throws CdException {
    cdCommand = new CdCommand();
    expectedEx.expect(CdException.class);
    expectedEx.expectMessage("Invalid arguments");
    String args[] = { fileTemp.getAbsolutePath(), "src", "sg", "", fileTemp.getAbsolutePath()};
    cdCommand.run(args, stdin, stdout);
  }
  
  /** 
   * Test void run(String[] args, InputStream stdin, OutputStream stdout)
   * Test Home ~
   */
  @Test
  public void cdHomeTest() {
      String[] args = {"~"};
      cdCommand = new CdCommand();
      assertEquals(Environment.currentDirectory, System.getProperty("user.dir"));
  }
  
  /** 
   * Test void run(String[] args, InputStream stdin, OutputStream stdout)
   * Test absolute path to folder
   */
  @Test
  public void cdToFullPathFolder() throws CdException {
    cdCommand = new CdCommand();
    String args[] = {folderTemp1.getAbsolutePath()};
    cdCommand.run(args, stdin, stdout);
    assertEquals(Environment.currentDirectory, folderTemp1.getAbsolutePath());
  }
  
  /** 
   * Test void run(String[] args, InputStream stdin, OutputStream stdout)
   * Test relative path to folder
   */
  @Test
  public void cdToRelativePathFolder() throws CdException {
    cdCommand = new CdCommand();
    String relativePath = folderTemp2.getName();
    String args[] = {relativePath};
    cdCommand.run(args, stdin, stdout);
    assertEquals(Environment.currentDirectory, folderTemp2.getAbsolutePath());
  }
  
  /** 
   * Test void run(String[] args, InputStream stdin, OutputStream stdout)
   * Test change directory to parent folder
   */
  @Test
  public void cdToParentFolder() throws CdException {
    cdCommand = new CdCommand();
    String parentPath = workingDir.getParent();
    String args[] = {".."};
    cdCommand.run(args, stdin, stdout);
    assertEquals(Environment.currentDirectory, parentPath);
  }
  
  /** 
   * Test void run(String[] args, InputStream stdin, OutputStream stdout)
   * Test cd null back to user.dir
   */
  @Test
  public void cdNullTest() throws CdException {
      String[] args = {};
      cdCommand = new CdCommand();
      cdCommand.run(args, stdin, stdout);
      assertEquals(Environment.currentDirectory, System.getProperty("user.dir"));
  }
  
}
