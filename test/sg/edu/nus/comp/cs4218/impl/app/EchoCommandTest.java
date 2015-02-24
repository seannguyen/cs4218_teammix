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

	@Test
	public void testEchoOneString() throws EchoException {
		String[] args = {"abcdefg"};
		echoCommand.run(args, stdin, stdout);
		assertEquals("abcdefg", stdout.toString());		
	}
	
	@Test
	public void testEchoThreeString() throws EchoException {
		String[] args = {"abcdefg", "123asdfgh000", "LKJH12POI09qwe"};
		echoCommand.run(args, stdin, stdout);
		assertEquals("abcdefg 123asdfgh000 LKJH12POI09qwe", stdout.toString());		
	}
	
	@Test
	public void testEchoWithDoubleQuotes() throws EchoException {
		String[] args = {"\"abcdefg\"", "\"123asdfgh000\"", "\"LKJH12POI09qwe\""};
		echoCommand.run(args, stdin, stdout);
		assertEquals("abcdefg 123asdfgh000 LKJH12POI09qwe", stdout.toString());		
	}
	
	@Test
	public void testEchoWithSingleQuotes() throws EchoException {
		String[] args = {"\'abcdefg\'", "\'123asdfgh000\'", "\'LKJH12POI09qwe\'"};
		echoCommand.run(args, stdin, stdout);
		assertEquals("abcdefg 123asdfgh000 LKJH12POI09qwe", stdout.toString());		
	}
	
	@Test
	public void testEchoWithSingleQuotesInsideDoubleQuotes() throws EchoException {
		String[] args = {"\"\'abcdefg\'\"", "\"123a\'sd\'fgh000\"", "\"LKJH12POI09qwe\'\'\""};
		echoCommand.run(args, stdin, stdout);
		assertEquals("\'abcdefg\' 123a\'sd\'fgh000 LKJH12POI09qwe\'\'", stdout.toString());		
	}
	
	@Test
	public void testEchoWithDoubleQuotesInsideSingleQuotes() throws EchoException {
		String[] args = {"\'\"abcdefg\"\'", "\'123a\"sd\"fgh000\'", "\'LKJH12POI09qwe\"\"\'"};
		echoCommand.run(args, stdin, stdout);
		assertEquals("\"abcdefg\" 123a\"sd\"fgh000 LKJH12POI09qwe\"\"", stdout.toString());		
	}
	
	@Test
	public void testEchoWithSingleQuoteInsideDoubleQuotes() throws EchoException {
		String[] args = {"\"\'abcdefg\"", "\"123a\'sdfgh000\"", "\"LKJH12POI09qwe\'\""};
		echoCommand.run(args, stdin, stdout);
		assertEquals("\'abcdefg 123a\'sdfgh000 LKJH12POI09qwe\'", stdout.toString());		
	}
	
	@Test
	public void testEchoWithDoubleQuoteInsideSingleQuotes() throws EchoException {
		String[] args = {"\'\"abcdefg\'", "\'123a\"sdfgh000\'", "\'LKJH12POI09qwe\"\'"};
		echoCommand.run(args, stdin, stdout);
		assertEquals("\"abcdefg 123a\"sdfgh000 LKJH12POI09qwe\"", stdout.toString());		
	}
	
	@Test
	public void testEchoWithNoArgs() throws EchoException {
		String[] args = {};
		echoCommand.run(args, stdin, stdout);
		assertEquals("", stdout.toString());		
	}
	
	@Test
	public void testEchoWithEmptyArgs() throws EchoException {
		String[] args = {""};
		echoCommand.run(args, stdin, stdout);
		assertEquals("", stdout.toString());		
	}
	
	@Test
	public void testEchoWithBackSlash() throws EchoException {
		String[] args = {"\\asdfg", "zxcvb\\n", "qwerty\\"};
		echoCommand.run(args, stdin, stdout);
		assertEquals("asdfg zxcvbn qwerty", stdout.toString());		
	}
	
	@Test
	public void testEchoSplitArgsByDoubleQuotes() throws EchoException {
		String[] args = {"asdfg\"lkjhg\"123456"};
		echoCommand.run(args, stdin, stdout);
		assertEquals("asdfg\"lkjhg\"123456", stdout.toString());		
	}
	
	@Test
	public void testEchoSplitArgsBySingleQuotes() throws EchoException {
		String[] args = {"asdfg\'lkjhg\'123456"};
		echoCommand.run(args, stdin, stdout);
		assertEquals("asdfg\'lkjhg\'123456", stdout.toString());		
	}
	
	@Test
	public void testEchoDoubleSingleQuotesWithSpaces() throws EchoException {
		String[] args = {"\"ASDqwert \' y09876ZXC\"", "\'ASDqwert \" y09876ZXC\'"};
		echoCommand.run(args, stdin, stdout);
		assertEquals("ASDqwert \' y09876ZXC ASDqwert \" y09876ZXC", stdout.toString());		
	}
}
