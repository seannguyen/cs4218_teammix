package sg.edu.nus.comp.cs4218.integration;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Configurations;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.SedException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.exception.WcException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.SedException;
import sg.edu.nus.comp.cs4218.impl.app.CatCommand;
import sg.edu.nus.comp.cs4218.impl.app.CdCommand;
import sg.edu.nus.comp.cs4218.impl.app.EchoCommand;
import sg.edu.nus.comp.cs4218.impl.app.FindCommand;
import sg.edu.nus.comp.cs4218.impl.app.GrepCommand;
import sg.edu.nus.comp.cs4218.impl.app.HeadCommand;
import sg.edu.nus.comp.cs4218.impl.app.LsCommand;
import sg.edu.nus.comp.cs4218.impl.app.PwdCommand;
import sg.edu.nus.comp.cs4218.impl.app.SedCommand;
import sg.edu.nus.comp.cs4218.impl.app.TailCommand;
import sg.edu.nus.comp.cs4218.impl.app.WcCommand;
import sg.edu.nus.comp.cs4218.shell.SimpleShell;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class CommandAndPipeTest {
  private SimpleShell shell;
  private static ByteArrayOutputStream stdout;
  private static ByteArrayInputStream stdin;
  String[] args;

  @Before
  public void setUp() throws Exception {
	  Environment.currentDirectory = System.getProperty("user.dir");
    shell = new SimpleShell();
    stdout = new ByteArrayOutputStream();
    Environment.nameAppMaps.put(Configurations.APPNAME_CD, new CdCommand());
    Environment.nameAppMaps.put(Configurations.APPNAME_LS, new LsCommand());
    Environment.nameAppMaps.put(Configurations.APPNAME_ECHO, new EchoCommand());
    Environment.nameAppMaps.put(Configurations.APPNAME_CAT, new CatCommand());
    Environment.nameAppMaps.put(Configurations.APPNAME_HEAD, new HeadCommand());
    Environment.nameAppMaps.put(Configurations.APPNAME_TAIL, new TailCommand());
    Environment.nameAppMaps.put(Configurations.APPNAME_PWD, new PwdCommand());
    Environment.nameAppMaps.put(Configurations.APPNAME_FIND, new FindCommand());
    Environment.nameAppMaps.put(Configurations.APPNAME_WC, new WcCommand());
    Environment.nameAppMaps.put(Configurations.APPNAME_SED, new SedCommand());
    Environment.nameAppMaps.put(Configurations.APPNAME_GREP, new GrepCommand());
  }

  @Test
  public void testCatAndSed() throws AbstractApplicationException,
      ShellException {
    String input = "cat test-files-basic" + File.separator
        + "One.txt  | sed s/txt/java/";
    shell.parseAndEvaluate(input, stdout);
    String expected = "This is one.java" + System.lineSeparator()
        + "Not two.java or three.txt" + System.lineSeparator()
        + "Day 1 was a long day." + System.lineSeparator()
        + "Day 2 was a short day." + System.lineSeparator()
        + "Day 56 was great.";
    Assert.assertEquals(expected, stdout.toString());
  }

  @Test
  public void testEchoAndSed() throws AbstractApplicationException,
      ShellException {
    String input = "echo 'mary had a little lamb'  | sed s/a/BBB/";
    shell.parseAndEvaluate(input, stdout);
    String expected = "mBBBry had a little lamb" + System.lineSeparator();
    Assert.assertEquals(expected, stdout.toString());
  }

  @Test
  public void testEchoAndWc() throws AbstractApplicationException,
      ShellException {
    String input = "echo 'mary had a little lamb'  | wc";
    shell.parseAndEvaluate(input, stdout);
    String expected = "1\t5\t23\t" + System.lineSeparator();
    Assert.assertEquals(expected, stdout.toString());
  }
  
  @Test
  public void testFindAndWc() throws AbstractApplicationException,
      ShellException {
    String input = "find test-files-basic -name *.txt | wc";
    shell.parseAndEvaluate(input, stdout);
    String expected = "7\t7\t280\t" + System.lineSeparator();
    Assert.assertEquals(expected, stdout.toString());
  }

  @Test
  public void testFindAndCat() throws AbstractApplicationException,
      ShellException {
    String input = "find test-files-basic -name *.txt | cat";
    shell.parseAndEvaluate(input, stdout);
    String expected = "." + File.separator + "test-files-basic"
        + File.separator + ".HideFolder" + File.separator + ".textFile3.txt"
        + System.lineSeparator() + "." + File.separator + "test-files-basic"
        + File.separator + ".HideFolder" + File.separator + ".textFile4.txt"
        + System.lineSeparator() + "." + File.separator + "test-files-basic"
        + File.separator + ".HideFolder" + File.separator + "textFile1.txt"
        + System.lineSeparator() + "." + File.separator + "test-files-basic"
        + File.separator + ".HideFolder" + File.separator + "textFile2.txt"
        + System.lineSeparator() + "." + File.separator + "test-files-basic"
        + File.separator + ".Two.txt" + System.lineSeparator() + "."
        + File.separator + "test-files-basic" + File.separator + "NormalFolder"
        + File.separator + "Normal.txt" + System.lineSeparator() + "."
        + File.separator + "test-files-basic" + File.separator + "One.txt" +  System.lineSeparator();
    Assert.assertEquals(expected, stdout.toString());
  }
  
  @Test
  public void testFindAndSed() throws AbstractApplicationException,
      ShellException {
    String input = "find test-files-basic -name *.txt | sed s/txt/java/g";
    shell.parseAndEvaluate(input, stdout);
    String expected = "." + File.separator + "test-files-basic"
        + File.separator + ".HideFolder" + File.separator + ".textFile3.java"
        + System.lineSeparator() + "." + File.separator + "test-files-basic"
        + File.separator + ".HideFolder" + File.separator + ".textFile4.java"
        + System.lineSeparator() + "." + File.separator + "test-files-basic"
        + File.separator + ".HideFolder" + File.separator + "textFile1.java"
        + System.lineSeparator() + "." + File.separator + "test-files-basic"
        + File.separator + ".HideFolder" + File.separator + "textFile2.java"
        + System.lineSeparator() + "." + File.separator + "test-files-basic"
        + File.separator + ".Two.java" + System.lineSeparator() + "."
        + File.separator + "test-files-basic" + File.separator + "NormalFolder"
        + File.separator + "Normal.java" + System.lineSeparator() + "."
        + File.separator + "test-files-basic" + File.separator + "One.java";
    Assert.assertEquals(expected, stdout.toString());
  }
  
  @Test
  public void testFindAndSedAndSed() throws AbstractApplicationException,
      ShellException {
    String input = "find test-files-basic -name *.txt | sed s/Hide/Changed/g | sed s/txt/java/g";
    shell.parseAndEvaluate(input, stdout);
    String expected = "." + File.separator + "test-files-basic"
        + File.separator + ".ChangedFolder" + File.separator + ".textFile3.java"
        + System.lineSeparator() + "." + File.separator + "test-files-basic"
        + File.separator + ".ChangedFolder" + File.separator + ".textFile4.java"
        + System.lineSeparator() + "." + File.separator + "test-files-basic"
        + File.separator + ".ChangedFolder" + File.separator + "textFile1.java"
        + System.lineSeparator() + "." + File.separator + "test-files-basic"
        + File.separator + ".ChangedFolder" + File.separator + "textFile2.java"
        + System.lineSeparator() + "." + File.separator + "test-files-basic"
        + File.separator + ".Two.java" + System.lineSeparator() + "."
        + File.separator + "test-files-basic" + File.separator + "NormalFolder"
        + File.separator + "Normal.java" + System.lineSeparator() + "."
        + File.separator + "test-files-basic" + File.separator + "One.java";
    Assert.assertEquals(expected, stdout.toString());
  }
  
  @Test
  public void testFindAndCatAndWc() throws AbstractApplicationException,
      ShellException {
    String input = "find test-files-basic -name *.txt | cat | wc";
    shell.parseAndEvaluate(input, stdout);
    String expected = "7\t7\t280\t" +  System.lineSeparator();
    Assert.assertEquals(expected, stdout.toString());
  }
  
  @Test
  public void testCatAndHeadAndWc() throws AbstractApplicationException,
      ShellException {
    String input = "cat test-files-basic" + File.separator +"One.txt | head -n 1 | wc";
    shell.parseAndEvaluate(input, stdout);
    String expected = "1\t3\t16\t" +  System.lineSeparator();
    Assert.assertEquals(expected, stdout.toString());
  }
  
  @Test
  public void testFindAndWc2() throws AbstractApplicationException,
      ShellException {
    String input = "find test-files-basic -name *.txt | wc";
    shell.parseAndEvaluate(input, stdout);
    String expected = "7\t7\t280\t" +  System.lineSeparator();
    Assert.assertEquals(expected, stdout.toString());
  }
  
  @Test(expected = WcException.class)
  public void testFindAndSedAndNegativeCommandAndSed() throws AbstractApplicationException,
      ShellException, WcException{
    String input = "find test-files-basic -name *.txt | sed s/Hide/Changed/g | wc -haha | sed s/txt/java/g";
    shell.parseAndEvaluate(input, stdout);
    String expected = "wc: illegal option -haha";
    Assert.assertEquals(expected, stdout.toString());
  }
  
  
}
