package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.io.OutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.exception.EchoException;

public class EchoCommandTest {
	private EchoCommand echoCommand;
	private InputStream stdin;
	private OutputStream stdout;
	
	@Before
	public void setUp() throws Exception {
		echoCommand = new EchoCommand();
		stdout = new java.io.ByteArrayOutputStream();
	}
	
	@After
	public void tearDown() throws Exception {
		
	}

	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Echo user input (one input)
	 * 
	 * @throw EchoException
	 */
	@Test
	public void testEchoOneString() throws EchoException {
		String[] args = {"abcdefg"};
		echoCommand.run(args, stdin, stdout);
		assertEquals("abcdefg", stdout.toString());		
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Echo user input (three input)
	 * 
	 * @throw EchoException
	 */
	@Test
	public void testEchoThreeString() throws EchoException {
		String[] args = {"abcdefg", "123asdfgh000", "LKJH12POI09qwe"};
		echoCommand.run(args, stdin, stdout);
		assertEquals("abcdefg 123asdfgh000 LKJH12POI09qwe", stdout.toString());		
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Echo user input with double quotes (multiple input)
	 * 
	 * @throw EchoException
	 */
	@Test
	public void testEchoWithDoubleQuotes() throws EchoException {
		String[] args = {"\"abcdefg\"", "\"123asdfgh000\"", "\"LKJH12POI09qwe\""};
		echoCommand.run(args, stdin, stdout);
		assertEquals("abcdefg 123asdfgh000 LKJH12POI09qwe", stdout.toString());		
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Echo user input with single quotes (multiple input)
	 * 
	 * @throw EchoException
	 */
	@Test
	public void testEchoWithSingleQuotes() throws EchoException {
		String[] args = {"\'abcdefg\'", "\'123asdfgh000\'", "\'LKJH12POI09qwe\'"};
		echoCommand.run(args, stdin, stdout);
		assertEquals("abcdefg 123asdfgh000 LKJH12POI09qwe", stdout.toString());		
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Echo user input Single quotes inside double quotes(multiple input)
	 * 
	 * @throw EchoException
	 */
	@Test
	public void testEchoWithSingleQuotesInsideDoubleQuotes() throws EchoException {
		String[] args = {"\"\'abcdefg\'\"", "\"123a\'sd\'fgh000\"", "\"LKJH12POI09qwe\'\'\""};
		echoCommand.run(args, stdin, stdout);
		assertEquals("\'abcdefg\' 123a\'sd\'fgh000 LKJH12POI09qwe\'\'", stdout.toString());		
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Echo user input double quotes inside single quotes (multiple input)
	 * 
	 * @throw EchoException
	 */
	@Test
	public void testEchoWithDoubleQuotesInsideSingleQuotes() throws EchoException {
		String[] args = {"\'\"abcdefg\"\'", "\'123a\"sd\"fgh000\'", "\'LKJH12POI09qwe\"\"\'"};
		echoCommand.run(args, stdin, stdout);
		assertEquals("\"abcdefg\" 123a\"sd\"fgh000 LKJH12POI09qwe\"\"", stdout.toString());		
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Echo user input with single quote inside double quotes (multiple input)
	 * 
	 * @throw EchoException
	 */
	@Test
	public void testEchoWithSingleQuoteInsideDoubleQuotes() throws EchoException {
		String[] args = {"\"\'abcdefg\"", "\"123a\'sdfgh000\"", "\"LKJH12POI09qwe\'\""};
		echoCommand.run(args, stdin, stdout);
		assertEquals("\'abcdefg 123a\'sdfgh000 LKJH12POI09qwe\'", stdout.toString());		
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Echo user input double quote inside single quotes(multiple input)
	 * 
	 * @throw EchoException
	 */
	@Test
	public void testEchoWithDoubleQuoteInsideSingleQuotes() throws EchoException {
		String[] args = {"\'\"abcdefg\'", "\'123a\"sdfgh000\'", "\'LKJH12POI09qwe\"\'"};
		echoCommand.run(args, stdin, stdout);
		assertEquals("\"abcdefg 123a\"sdfgh000 LKJH12POI09qwe\"", stdout.toString());		
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Echo no args
	 * 
	 * @throw EchoException
	 */
	@Test
	public void testEchoWithNoArgs() throws EchoException {
		String[] args = {};
		echoCommand.run(args, stdin, stdout);
		assertEquals("", stdout.toString());		
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Echo empty string
	 * 
	 * @throw EchoException
	 */
	@Test
	public void testEchoWithEmptyArgs() throws EchoException {
		String[] args = {""};
		echoCommand.run(args, stdin, stdout);
		assertEquals("", stdout.toString());		
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Echo user input with backslash
	 * 
	 * @throw EchoException
	 */
	@Test
	public void testEchoWithBackSlash() throws EchoException {
		String[] args = {"\\asdfg", "zxcvb\\n", "qwerty\\"};
		echoCommand.run(args, stdin, stdout);
		assertEquals("asdfg zxcvbn qwerty", stdout.toString());		
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Echo user input splitted by double quotes
	 * 
	 * @throw EchoException
	 */
	@Test
	public void testEchoSplitArgsByDoubleQuotes() throws EchoException {
		String[] args = {"asdfg\"lkjhg\"123456"};
		echoCommand.run(args, stdin, stdout);
		assertEquals("asdfg\"lkjhg\"123456", stdout.toString());		
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Echo user input splitted by single quotes
	 * 
	 * @throw EchoException
	 */
	@Test
	public void testEchoSplitArgsBySingleQuotes() throws EchoException {
		String[] args = {"asdfg\'lkjhg\'123456"};
		echoCommand.run(args, stdin, stdout);
		assertEquals("asdfg\'lkjhg\'123456", stdout.toString());		
	}
	
	/**
	 * Test void run(String[] args, InputStream stdin, OutputStream stdout) Test
	 * Echo user input with double single quotes with spaces
	 * 
	 * @throw EchoException
	 */
	@Test
	public void testEchoDoubleSingleQuotesWithSpaces() throws EchoException {
		String[] args = {"\"ASDqwert \' y09876ZXC\"", "\'ASDqwert \" y09876ZXC\'"};
		echoCommand.run(args, stdin, stdout);
		assertEquals("ASDqwert \' y09876ZXC ASDqwert \" y09876ZXC", stdout.toString());		
	}
}
