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
   * The bug is due to invalid handling of "|" symbol [|] from SHELL when passing
   * inputs to Application SED Ref Project_description.pdf , Section SED,
   * "Note that the symbols "/" used to separate regexp and replacement string can be substituted by any
      other symbols. For example, "s/a/b/" and "s|a|b|" are the same replacement rules."
      
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
    *  "Note that the symbols "/" used to separate regexp and replacement string can be substituted by any
    * other symbols. For example, "s/a/b/" and "s|a|b|" are the same replacement rules."
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
     *  "PATTERN ï¿½ file name with some parts replaced with * (asterisk)"
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
   
   /**
    * when find -name *.java
    * Output should be all the all the java files starting from root directory
    * However, an exception msg is throw stating that there are too many args.
    * 
    * With Ref from, Project Description section find
    * find [PATH] -name PATTERN
        PATTERN – file name with some parts replaced with * (asterisk).
        PATH – the root directory for search. If not specified, use the current directory.
    * 
    * 
    * For ease of expected results, I have chosen to find files ending with .md instead
    * base on current directory which is usr.dir, the output should be README.md
    * 
    * System Level Testing
   * @throws ShellException 
    */
  @Test
   public void testShellandFindWithoutGivenPathAndWithAStarInPattern() throws ShellException {
       String cmdLine = "find -name *.md";
       OutputStream output = new ByteArrayOutputStream();
       try {
         shell.parseAndEvaluate(cmdLine, output);
       } catch (AbstractApplicationException e) {
           e.printStackTrace();
       }
       assertEquals("README.md", output.toString());
   }
   
  
  /**
   * Finding directory in current directory as root
   * when find -name src
   * The application should still return a result showing the folder
   * In this case, I am finding a folder called src and it exist.
   * There for the result should be:
   * src
   * 
   *  
   * With Ref from, Project Description section find
   * find [PATH] -name PATTERN
       PATTERN – file name with some parts replaced with * (asterisk).
       PATH – the root directory for search. If not specified, use the current directory.
   * 
   * 
   * For ease of expected results, I have chosen to find files ending with .md instead
   * base on current directory which is usr.dir, the output should be README.md
   * 
   * System Level Testing
  * @throws ShellException 
   */
 @Test
  public void testFindNoPathButFindForDirectory() throws ShellException {
       String[] args = {"-name", "src"};
      OutputStream output = new ByteArrayOutputStream();
      try {
        find.run(args, null, output);
      } catch (AbstractApplicationException e) {
          e.printStackTrace();
      }
      assertEquals("src", output.toString());
  }
 
 
 /**
  * PipingCommand seems unresponsive when find all javafiles and piping to cat is used.
  * find -name *.java | cat
  * 
  * The result should have been output of all java files(recursively) found and passing it to cat
  * and cat outputting the same output.
  * 
  * The function find src -name *.java works fine, so I believe your team agrees that
  * finding files recursively is part of the project requirements.
  * 
  * You guys can change the test case to reduce the number of results. But I hope
  * I manage to explain clearly.
  * thanks
  * 
  * System Level Testing
 * @throws ShellException 
  */
 @Test
 public void testFindAllJavaFilesAndPipingToCat() throws ShellException {
     String cmdLine = "find -name *.java | cat";
     OutputStream output = new ByteArrayOutputStream();
     String expected = 
     "src" + File.separator + "logic" + File.separator + "MainLogic.java" + 
     "src" + File.separator + "logic" + File.separator + "ShellLogic.java" + 
     "src" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "Application.java" + 
     "src" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "Command.java" + 
     "src" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "Environment.java" + 
     "src" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "exception" + File.separator + "AbstractApplicationException.java" + 
     "src" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "exception" + File.separator + "CatException.java" + 
     "src" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "exception" + File.separator + "CdException.java" + 
     "src" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "exception" + File.separator + "EchoException.java" + 
     "src" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "exception" + File.separator + "FindException.java" + 
     "src" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "exception" + File.separator + "GrepException.java" + 
     "src" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "exception" + File.separator + "HeadException.java" + 
     "src" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "exception" + File.separator + "LsException.java" + 
     "src" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "exception" + File.separator + "PwdException.java" + 
     "src" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "exception" + File.separator + "SedException.java" + 
     "src" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "exception" + File.separator + "ShellException.java" + 
     "src" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "exception" + File.separator + "TailException.java" + 
     "src" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "exception" + File.separator + "WcException.java" + 
     "src" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "impl" + File.separator + "app" + File.separator + "AppCat.java" + 
     "src" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "impl" + File.separator + "app" + File.separator + "AppCd.java" + 
     "src" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "impl" + File.separator + "app" + File.separator + "AppEcho.java" + 
     "src" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "impl" + File.separator + "app" + File.separator + "AppFind.java" + 
     "src" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "impl" + File.separator + "app" + File.separator + "AppGrep.java" + 
     "src" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "impl" + File.separator + "app" + File.separator + "AppHead.java" + 
     "src" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "impl" + File.separator + "app" + File.separator + "AppLs.java" + 
     "src" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "impl" + File.separator + "app" + File.separator + "AppPwd.java" + 
     "src" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "impl" + File.separator + "app" + File.separator + "AppSed.java" + 
     "src" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "impl" + File.separator + "app" + File.separator + "AppTail.java" + 
     "src" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "impl" + File.separator + "app" + File.separator + "AppWc.java" + 
     "src" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "impl" + File.separator + "app" + File.separator + "Utilities.java" + 
     "src" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "impl" + File.separator + "cmd" + File.separator + "CallCmd.java" + 
     "src" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "impl" + File.separator + "cmd" + File.separator + "PipeCmd.java" + 
     "src" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "impl" + File.separator + "cmd" + File.separator + "SeqCmd.java" + 
     "src" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "Shell.java" + 


     "test" + File.separator + "integration" + File.separator + "GrepAndPipeTest.java" + 
     "test" + File.separator + "integration" + File.separator + "GrepAndSubcmdTest.java" + 
     "test" + File.separator + "integration" + File.separator + "RedirectOrFindOnStateChangeTest.java" + 
     "test" + File.separator + "integration" + File.separator + "Utilities.java" + 
     "test" + File.separator + "logic" + File.separator + "ShellLogicTest.java" + 
     "test" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "impl" + File.separator + "app" + File.separator + "AppCatTest.java" + 
     "test" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "impl" + File.separator + "app" + File.separator + "AppCdTest.java" + 
     "test" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "impl" + File.separator + "app" + File.separator + "AppEchoTest.java" + 
     "test" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "impl" + File.separator + "app" + File.separator + "AppFindTest.java" + 
     "test" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "impl" + File.separator + "app" + File.separator + "AppGrepTest.java" + 
     "test" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "impl" + File.separator + "app" + File.separator + "AppHeadTest.java" + 
     "test" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "impl" + File.separator + "app" + File.separator + "AppLsTest.java" + 
     "test" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "impl" + File.separator + "app" + File.separator + "AppPwdTest.java" + 
     "test" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "impl" + File.separator + "app" + File.separator + "AppSedTest.java" + 
     "test" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "impl" + File.separator + "app" + File.separator + "AppTailTest.java" + 
     "test" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "impl" + File.separator + "app" + File.separator + "AppWcTest.java" + 
     "test" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "impl" + File.separator + "app" + File.separator + "RandoopForAppFind" + File.separator + "RandoopTest.java" + 
     "test" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "impl" + File.separator + "app" + File.separator + "RandoopForAppFind" + File.separator + "RandoopTest0.java" + 
     "test" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "impl" + File.separator + "app" + File.separator + "UtilitiesTest.java" + 
     "test" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "impl" + File.separator + "cmd" + File.separator + "CallCmdTest.java" + 
     "test" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "impl" + File.separator + "cmd" + File.separator + "PipeCmdTest.java" + 
     "test" + File.separator + "sg" + File.separator + "edu" + File.separator + "nus" + File.separator + "comp" + File.separator + "cs4218" + File.separator + "impl" + File.separator + "cmd" + File.separator + "SeqCmdTest.java";
     
     try {
       shell.parseAndEvaluate(cmdLine, output);
     } catch (AbstractApplicationException e) {
         e.printStackTrace();
     }
     assertEquals(expected, output.toString());
 }
}