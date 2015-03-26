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
	private final File workingDir = new File(System.getProperty("user.dir"));
	private InputStream stdin;
	private OutputStream stdout;

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		fileTemp = File.createTempFile("cd1", "");
		folderTemp1 = Files.createTempDirectory("cdFolderTemp1").toFile();
		folderTemp2 = Files.createTempDirectory(workingDir.toPath(),
				"cdFolderTemp2").toFile();
		Environment.currentDirectory = workingDir.getAbsolutePath();
		cdCommand = new CdCommand();
	}

	@After
	public void tearDown() {
		fileTemp.delete();
		folderTemp1.delete();
		folderTemp2.delete();
		cdCommand = null;
		stdin = null;
		stdout = null;
		Environment.currentDirectory = System.getProperty("user.dir");
	}

	/**
	 * Tests helper changeDirectory(String newDirectory) newDirectory is null
	 */
	@Test
	public void testChangeDirectoryToNull() {
		assertNull(cdCommand.changeDirectory(null));
	}

	/**
	 * Tests helper changeDirectory(String newDirectory) newDirectory is empty
	 * String
	 */
	@Test
	public void testChangeDirectoryToEmptyString() {
		assertNull(cdCommand.changeDirectory(""));
	}

	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * absolute path to non-directory
	 * 
	 * @throw CdException
	 */
	@Test
	public void testChangeDirectoryToFile() throws CdException {
		assertNull(cdCommand.changeDirectory(fileTemp.getAbsolutePath()));
		expectedEx.expect(CdException.class);
		expectedEx.expectMessage("Not a directory");
		String args[] = { fileTemp.getAbsolutePath() };
		cdCommand.run(args, stdin, stdout);
	}

	/**
	 * Test helper FormatDirectory to convert relative to absolute path
	 */
	@Test
	public void testFormatDirectoryRelativeInput() {
		String results = cdCommand.formatDirectory(
				workingDir.getAbsolutePath(), folderTemp2.getName());
		assertEquals(results, folderTemp2.getAbsolutePath() + File.separator);
	}

	/**
	 * Test helper FormatDirectory to return nothing when curr working dir is
	 * null
	 */
	@Test
	public void testFormatDirectoryNullCurrentWorkingDir() {
		String results = cdCommand.formatDirectory(null, folderTemp2.getName());
		assertEquals(results, "");
	}

	/**
	 * Test helper FormatDirectory to return nothing when curr working dir is
	 * null
	 */
	@Test
	public void testFormatDirectoryTransverse() {
		String results = cdCommand.formatDirectory(
				folderTemp2.getAbsolutePath(), "..");
		assertEquals(results, workingDir.getAbsolutePath() + File.separator);
	}

	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * invalid args size 2 (more than 1)
	 * 
	 * @throw CdException
	 */
	@Test
	public void testTwoArgs() throws CdException {
		expectedEx.expect(CdException.class);
		expectedEx.expectMessage("Invalid arguments");
		String args[] = { fileTemp.getAbsolutePath(), "src" };
		cdCommand.run(args, stdin, stdout);
	}

	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * invalid args size 5 (more than 1)
	 * 
	 * @throw CdException
	 */
	@Test
	public void testFiveArgs() throws CdException {
		expectedEx.expect(CdException.class);
		expectedEx.expectMessage("Invalid arguments");
		String args[] = { fileTemp.getAbsolutePath(), "src", "sg", "",
				fileTemp.getAbsolutePath() };
		cdCommand.run(args, stdin, stdout);
	}

	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Home ~
	 * 
	 * @throws CdException
	 */
	@Test
	public void cdHomeTest() throws CdException {
		String[] args = { "~" };
		cdCommand.run(args, stdin, stdout);
		assertEquals(Environment.currentDirectory,
				System.getProperty("user.dir"));
	}

	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * absolute path to folder
	 * 
	 * @throw CdException
	 */
	@Test
	public void cdToFullPathFolder() throws CdException {
		String args[] = { folderTemp1.getAbsolutePath() };
		cdCommand.run(args, stdin, stdout);
		assertEquals(Environment.currentDirectory,
				folderTemp1.getAbsolutePath());
	}

	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * relative path to folder
	 * 
	 * @throw CdException
	 */
	@Test
	public void cdToRelativePathFolder() throws CdException {
		String relativePath = folderTemp2.getName();
		String args[] = { relativePath };
		cdCommand.run(args, stdin, stdout);
		assertEquals(Environment.currentDirectory,
				folderTemp2.getAbsolutePath());
	}

	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * change directory to parent folder
	 * 
	 * @throw CdException
	 */
	@Test
	public void cdToParentFolder() throws CdException {
		String parentPath = workingDir.getParent();
		String args[] = { ".." };
		cdCommand.run(args, stdin, stdout);
		assertEquals(Environment.currentDirectory, parentPath);
	}

	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * cd null back to user.dir
	 * 
	 * @throw CdException
	 */
	@Test
	public void cdNullTest() throws CdException {
		String[] args = {};
		cdCommand.run(args, stdin, stdout);
		assertEquals(Environment.currentDirectory,
				System.getProperty("user.dir"));
	}

}
