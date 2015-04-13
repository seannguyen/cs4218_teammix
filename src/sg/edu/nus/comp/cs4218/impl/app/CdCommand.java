package sg.edu.nus.comp.cs4218.impl.app;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Stack;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Configurations;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CdException;

public class CdCommand implements Application {
	protected final static String NOTHING = "";
	protected final static String FILE_SEPARATOR = "file.separator";
	/**
	 * Perform change directory command
	 *
	 * @param args
	 *            input arguments
	 * @param stdin
	 *            inputStream
	 * @param stdout
	 *            outputStream
	 */
	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout)
			throws CdException {
		File newDirectory = null;
		String arg = "";
		if (args.length == 0) {
			newDirectory = changeDirectory(System.getProperty("user.dir"));
		} else if (args.length == 1) {
			arg = args[0] + ": ";
			if (("~").equals(args[0])) {
				newDirectory = changeDirectory(System.getProperty("user.dir"));
			} else if (Paths.get(args[0]).isAbsolute()) {
				newDirectory = changeDirectory(args[0]);
			} else {
				newDirectory = changeDirectory(formatDirectory(
						Environment.currentDirectory, args[0]));
			}
		} else {
			throw new CdException("Invalid arguments");
		}

		if (newDirectory == null) {
			throw new CdException(arg + "Not a directory");
		} else {
			Environment.currentDirectory = newDirectory.getAbsolutePath();
		}
	}

	/**
	 * Returns new File pointing to new directory Returns null if newDirectory
	 * is null or newDirectory is not a directory
	 *
	 * @param newDirectory
	 *            an absolute directory path
	 * @return new directory
	 */
	protected File changeDirectory(String newDirectory) {
		if (newDirectory != null) {
			File newDir = new File(newDirectory);
			if (newDir.isDirectory()) {
				return newDir;
			}
		}
		return null;
	}

	/**
	 * Format root base on OS file separator
	 * 
	 * @param root
	 * 			pattern matching root
	 */
	private static String formatFileSeparator(String root) {
		String newRoot = root;
		if (System.getProperty(FILE_SEPARATOR) != null
				&& System.getProperty(FILE_SEPARATOR).equals(
						Configurations.W_FILESEPARATOR)) {
			newRoot = root.replace("\\", "\\\\");
		}
		return newRoot;
	}
	
	
	/**
	 * Format given new relative directory path into a absolute directory
	 *
	 * @param curAbsoluteDir
	 *            current absolute directory
	 * @param newRelativeDir
	 *            a relative directory
	 * @return formated Directory
	 */
	protected static String formatDirectory(String currAbsoluteDir,
			String newRelativeDir) {
		String separator = File.separator;
		if (("\\").equals(File.separator)) {
			separator = ("\\\\");
		}
		String curAbsoluteDir = formatFileSeparator(currAbsoluteDir);
		if (curAbsoluteDir == null) {
			return NOTHING;
		} else {
			Stack<String> newAbsoluteDir = new Stack<String>();
			newAbsoluteDir
					.addAll(Arrays.asList(curAbsoluteDir.split(separator)));
			if (newRelativeDir != null) {
				for (String token : Arrays.asList(newRelativeDir
						.split(separator))) {
					if (!("").equals(token)) {
						if (("..").equals(token)) { // transverse up
							newAbsoluteDir.pop();
						} else if (((".").equals(token))) { // remain
						} else { // transverse down
							newAbsoluteDir.push(token);
						}
					}
				}
			}

			StringBuilder newWorkingDir = new StringBuilder();
			if (System.getProperty("os.name").toLowerCase().indexOf("mac") > 0) {
				newWorkingDir.append(File.separator); // mac os directory format
			}
			for (int i = 0; i < newAbsoluteDir.size(); i++) {
				newWorkingDir.append(newAbsoluteDir.get(i));
				newWorkingDir.append(File.separator);
			}
			return newWorkingDir.toString();
		}
	}
}
