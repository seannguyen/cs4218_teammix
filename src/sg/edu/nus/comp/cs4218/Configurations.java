package sg.edu.nus.comp.cs4218;

public final class Configurations {
	public static final String MESSAGE_WELCOME = "WELCOME TO CS4128 SHELL";
	public static final String MESSAGE_PROMPT = ">";
	public static final String MESSAGE_E_GENERAL = "Opps, there is somethis wrong!";
	public static final String MESSAGE_E_PARSING = "Your Command Line is Invalid";
	public static final String MESSAGE_E_APP = "Application Error:";
	public static final String MESSAGE_E_MISSA = "Command Not Found";
	public static final String MESSAGE_E_MISSO = "The output stream is missing";
	public static final String MESSGE_E_MISSF = "No Such File or Directory";
	
	public static final char QUOTE_SINGLE = '\'';
	public static final char QUOTE_DOUBLE = '\"';
	public static final char QUOTE_BACK = '`';
	public static final char SPACE_CHAR = ' ';
	public static final char TAB_CHAR = '\t';
	
	public static final String W_FILESEPARATOR = "\\";
	public static final String L_FILESEPARATOR = "/";
	
	public static final String SEMICOLON_TOKEN = ";";
	public static final String PIPE_TOKEN = "|";
	public static final String IN_REIO_TOKEN = "<";
	public static final String OUT_REIO_TOKEN = ">";
	public static final String NEWLINE = String.format("%n");
	public static final String WHITESPACE = " ";
	public static final String WHITESPACEREGEX = String.format("[ \t]");
	
	
	public static final String APPNAME_CD = "cd";
	public static final String APPNAME_LS = "ls";
	public static final String APPNAME_ECHO = "echo";
	public static final String APPNAME_PWD = "pwd";
	public static final String APPNAME_CAT = "cat";
	public static final String APPNAME_HEAD = "head";
	public static final String APPNAME_TAIL = "tail";
	public static final String APPNAME_FIND = "find";
	public static final String APPNAME_WC = "wc";
	public static final String APPNAME_SED = "sed";
}
