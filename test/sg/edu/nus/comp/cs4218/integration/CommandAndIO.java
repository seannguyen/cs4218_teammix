package sg.edu.nus.comp.cs4218.integration;


import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Configurations;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.LsException;
import sg.edu.nus.comp.cs4218.exception.SedException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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
import java.nio.file.Files;
import java.nio.file.Paths;


public class CommandAndIO {
	private static Shell shell;
	  private static ByteArrayOutputStream stdout;
	  private static ByteArrayInputStream stdin;
	  final private static String Folder = "test-dump";
	  String[] args;

	  @Before
	  public void setUp() throws Exception {
		  Environment.currentDirectory = System.getProperty("user.dir");
	      shell = new SimpleShell();
	      stdout = new ByteArrayOutputStream();
	      Environment.nameAppMaps.put(Configurations.APPNAME_CD, new CdCommand());
	      Environment.nameAppMaps.put(Configurations.APPNAME_LS, new LsCommand());
	      Environment.nameAppMaps.put(Configurations.APPNAME_ECHO,
	              new EchoCommand());
	      Environment.nameAppMaps.put(Configurations.APPNAME_CAT,
	              new CatCommand());
	      Environment.nameAppMaps.put(Configurations.APPNAME_HEAD,
	              new HeadCommand());
	      Environment.nameAppMaps.put(Configurations.APPNAME_TAIL,
	              new TailCommand());
	      Environment.nameAppMaps.put(Configurations.APPNAME_PWD,
	              new PwdCommand());
	      Environment.nameAppMaps.put(Configurations.APPNAME_FIND,
	              new FindCommand());
	      Environment.nameAppMaps.put(Configurations.APPNAME_WC, new WcCommand());
	      Environment.nameAppMaps.put(Configurations.APPNAME_SED, new SedCommand());
	      Environment.nameAppMaps.put(Configurations.APPNAME_GREP, new GrepCommand());
	      Files.createDirectories(Paths.get(Folder));
	  }
	  
	  @After
	  public void tearDown() throws Exception {
		  Environment.currentDirectory = System.getProperty("user.dir");
	     shell = null;
	     stdout = null;
	  }
	   
	  @Test
	  public void testCDandEchoToFile() throws AbstractApplicationException,
	      ShellException {
	    String input = "cd test-dump; echo a > file.txt";
	    shell.parseAndEvaluate(input, stdout);
	    File file = new File("test-dump" + File.separator + "file.txt");
		assertTrue(file.exists());
	    try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader("test-dump" + File.separator + "file.txt"));
			String line;
			try {						
				while((line = bufferedReader.readLine()) != null) {	
					Assert.assertEquals("a", line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	  }	

	  @Test
	  public void testEchoandCat() throws AbstractApplicationException,
	      ShellException {
	    String input = "echo ab > file.txt; cat file.txt";
	    shell.parseAndEvaluate(input, stdout);
	    File file = new File("file.txt");
		assertTrue(file.exists());
	    try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader("file.txt"));
			String line;
			try {						
				while((line = bufferedReader.readLine()) != null) {	
					Assert.assertEquals("ab", line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	  }	
	  
	  @Test
	  public void testEchoandCatandEcho() throws AbstractApplicationException,
	      ShellException {
	    String input = "echo ab > file.txt; cat file.txt; echo cd > file.txt";
	    shell.parseAndEvaluate(input, stdout);
	    File file = new File("file.txt");
		assertTrue(file.exists());
	    try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader("file.txt"));
			String line;
			try {						
				while((line = bufferedReader.readLine()) != null) {	
					Assert.assertEquals("cd", line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	    Assert.assertEquals("ab" + Configurations.NEWLINE, stdout.toString());
	  }	
	  
	  @Test
	  public void testNegativeCase1InvalidFirst() throws AbstractApplicationException,
	      ShellException {
	    String input = "''; echo a > shouldnotappear.txt";
	    shell.parseAndEvaluate(input, stdout);
	    File file = new File("shouldnotappear.txt");
		assertTrue(!file.exists());
	  }	
	  
	  @Test
	  public void testNegativeCase2InvalidLast() throws AbstractApplicationException,
	      ShellException {
	    String input = "`echo a > shouldappear.txt`; ''";
	    shell.parseAndEvaluate(input, stdout);
	    File file = new File("shouldappear.txt");
		assertTrue(file.exists());
	  }	
	  
	  @Test
	  public void testNegativeCase3InvalidOverall() throws AbstractApplicationException,
	      ShellException {
	    String input = "`echo a > shouldnotappear.txt`; ;";
	    shell.parseAndEvaluate(input, stdout);
	    File file = new File("shouldnotappear.txt");
		assertTrue(file.exists());
	  }	
}
