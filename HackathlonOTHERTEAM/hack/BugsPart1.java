package hack;
import static org.junit.Assert.assertEquals;
import integration.Utilities;

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
import java.util.TreeSet;

import logic.ShellLogic;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.FindException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.app.AppCat;
import sg.edu.nus.comp.cs4218.impl.app.AppFind;
import sg.edu.nus.comp.cs4218.impl.app.AppGrep;
import sg.edu.nus.comp.cs4218.impl.app.AppSed;
import sg.edu.nus.comp.cs4218.impl.app.AppWc;

public class BugsPart1 {
  ShellLogic shell;
  OutputStream stdout;
  AppSed sed;
  AppFind find;
  AppWc wc;
  AppGrep grep;
  AppCat cat;
  static final String TEST_DIR = "testResources" + File.separator
          + "IntegrationTest";
  private final static String FILE = "testFile1.txt";
  private final static String FOLDER = "test_folder";
  private final static Path FOLDER_PATH = Paths.get(FOLDER);
  private final static Path FILE_PATH = Paths.get(FILE);
  private final static String CONTENT = "a\nb\nc";

  @BeforeClass
  public static void setUpBeforeClass() throws IOException {		
		FileWriter fileWriter = null;
      BufferedWriter bufferedWriter = null;
      try {
      	Files.createFile(FILE_PATH);
      	Files.createDirectory(FOLDER_PATH);
      } catch (FileAlreadyExistsException e) {
      	System.err.println("File already exists: " + e.getMessage());
      }
      
      try {
          File file = new File(FILE_PATH.toString());
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
  
  @Before
  public void setUp() throws Exception {
	  OutputStream stdout = new ByteArrayOutputStream();
      shell = new ShellLogic();
      sed = new AppSed();
      find = new AppFind();
      wc = new AppWc();
      grep = new AppGrep();
      cat = new AppCat();
      Environment.currentDirectory = System.getProperty("user.dir");
  }

  @After
  public void tearDown() throws Exception {

      Environment.currentDirectory = System.getProperty("user.dir");
  }
  
  @AfterClass
	public static void tearDownAfterClass() throws IOException {
	  try {
		  Files.delete(FILE_PATH);
		  Files.delete(FOLDER_PATH);
	  } catch (IOException e) {
		  //e.printStackTrace(); Ignore because windows cannot delete
	  }
	}
  
  /**
   * The bug is due to invalid handling of � symbol [|] from SHELL when passing
   * inputs to Application SED Ref Project_description.pdf , Section SED,
   * "Note that the symbols �/� used to separate regexp and replacement string can be substituted by any
      other symbols. For example, �s/a/b/� and �s|a|b|� are the same replacement rules."
      
      System Level Testing
   */
  @Test
  public void testShellParsingOfPipeCharToSed() {

      String cmdline = "sed s|words|SENTENCE|   "+ TEST_DIR + File.separator  + "manyWords.txt";
      
      String expected = "there are many SENTENCE" + System.lineSeparator() +
                          "in this text file" + System.lineSeparator() +
                            "SENTENCE are the highlight" + System.lineSeparator() +
                            System.lineSeparator() +
                            "are WORDS the same?" + System.lineSeparator() +
                            "how about SENTENCEsssssssssss" + System.lineSeparator() +
                            "let us find out" + System.lineSeparator() +
                            "class is in session" + System.lineSeparator() ;
    

      OutputStream output = new ByteArrayOutputStream();

      try {
          shell.parseAndEvaluate(cmdline, output);
      } catch (AbstractApplicationException | ShellException e) {
          e.printStackTrace();
      }

      assertEquals(expected, output.toString());
  }


  /**
    * The bug is due to invalid [REPLACEMENT] format from SED. SED should throw Exception
    * When [REPLACEMENT] Does not have 3 Separating Symbols. Example s/a/b/ (3 backslash), s/a/b/g (3 backslash)
    * Thus if [REPLACEMENT] contains s/a/b (2 Backslash) This is invalid.
    * 
    *  Ref Project_description.pdf , Section SED,
    *  "Note that the symbols �/� used to separate regexp and replacement string can be substituted by any
    * other symbols. For example, �s/a/b/� and �s|a|b|� are the same replacement rules."
    * 
    * 
    * 
    *   Unit Level Testing
   * @throws AbstractApplicationException 
    */
    @Test(expected = AbstractApplicationException.class)
    public void testSEDReplacementCheckError() throws AbstractApplicationException {

      String[] args = {"s/words/SENTENCE/", TEST_DIR + File.separator  + "manyWords.txt"};
    
      String expected = "there are many SENTENCE" + System.lineSeparator() +
                        "in this text file" + System.lineSeparator() +
                          "SENTENCE are the highlight" + System.lineSeparator() +
                          System.lineSeparator() +
                          "are WORDS the same?" + System.lineSeparator() +
                          "how about SENTENCEsssssssssss" + System.lineSeparator() +
                          "let us find out" + System.lineSeparator() +
                          "class is in session" + System.lineSeparator() ;
  

      OutputStream output = new ByteArrayOutputStream();
      sed.run(args, null, output);
      assertEquals(expected, output.toString());
    }

    
    /**
     * The bug occurs when Only a [*] wildcard is place at the back of a [Pattern]
     * Find application should find only files starting with [Pattern] in this case.
     * 
     * Example: I want to find "379*"
     * Current Directory contains:
     *        37917_1
     *        37917_2
     *        actual37917
     *        expcted37917
     * Running find application with [Pattern] = "379*" should return
     *  37917_1
     *  37917_2
     *      
     *  Ref Project_description.pdf , Section Find,
     *  "PATTERN � file name with some parts replaced with * (asterisk)"
     * 
     *   Unit Level Testing
    * @throws AbstractApplicationException 
     */
     @Test
     public void testFindMatchingFileNameStartingWithWord() throws AbstractApplicationException {

       String[] args = { "testResources" + File.separator +"GrepTest" + File.separator, "-name", "379*"};
       System.out.println(args[0]);
       String expected = "testResources\\GrepTest\\37917_1" + System.lineSeparator() +
                         "testResources\\GrepTest\\37917_2" + System.lineSeparator();

       OutputStream output = new ByteArrayOutputStream();
       find.run(args, null, output);
       assertEquals(expected, output.toString());
     }
     
     /**
      * Given the input stream:
      * a\n
      * b\n
      * c
      * 
      * The return result is Lines: 2 Words: 2 Characters: 5
      * whereas the expected result is Lines: 2 Words: 3 Characters: 5
      * 
      * Unit Level Testing
      */
     @Test
 	public void testWithNoNewlineAtTheEndOfFile() throws Exception {
 		InputStream in = new ByteArrayInputStream("a\nb\nc".getBytes());
 		wc.run(new String[] {}, in, stdout);
 		assertEquals("File\nLines: 2\nWords: 3\nCharacters: 5\n", stdout.toString());
 	}
     
     /**
      * Given one non-existing file and one existing file
      * 
      * The expected results should print exception msg for non-existing file
      * but return result for existing file
      * 
      * Unit Level Testing
      */
     @Test
 	public void testGREPExistingFileAndNonExistingFile() {
 		String[] args = {"a", FILE, FOLDER};
 		try {
 			grep.run(args, null, stdout);
 		} catch (AbstractApplicationException e) {
 			e.printStackTrace();
 		}
 		assertEquals("grep: TEST: Is a directory\ntestFile1.txt:a\n", stdout.toString());
 	}
     
     /**
      * Given one non-existing file and one existing file
      * 
      * The expected results should print exception msg for non-existing file
      * but return result for existing file
      * 
      * Unit Level Testing
      */
    @Test
 	public void testCATExistingFileAndNonExistingFile() {
 		String[] args = {FILE, FOLDER};
 		try {
 			cat.run(args, null, stdout);
 		} catch (AbstractApplicationException e) {
 			e.printStackTrace();
 		}
 		assertEquals("cat: TEST: Is a directory\na\nb\nc", stdout.toString());
 	}
    
    /**
     * Given one non-existing file and one existing file
     * 
     * The expected results should print exception msg for non-existing file
     * but return result for existing file
     * 
     * Unit Level Testing
     */
   @Test
	public void testWCExistingFileAndNonExistingFile() {
		String[] args = {FILE, FOLDER};
		try {
			wc.run(args, null, stdout);
		} catch (AbstractApplicationException e) {
			e.printStackTrace();
		}
		assertEquals("wc: TEST: Is a directory\nFile\nLines: 2\nWords: 3\nCharacters: 5\n", stdout.toString());
	}
}