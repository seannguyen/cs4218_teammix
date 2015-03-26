package sg.edu.nus.comp.cs4218.impl.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Configurations;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.LsException;

public class LsCommand implements Application {
	protected Boolean skipNewLine = true;
	protected int numOfDirectories = 0;
	protected Boolean multiple = false;

	/**
	 * Perform List directory command
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
			throws LsException {
		multiple = false;
		List<File> files = null;
		if (args.length > 1) {
			multiple = true;
			int count = 0;
			printNonDirectory(stdout, args);
			for (String arg : args) {
				File targetDirectory = new File(arg.replaceAll(
						Configurations.NEWLINE, ""));
				try {
					files = getFiles(targetDirectory);
					if (isDirectory(targetDirectory)) {
						printNewLine(stdout);
						count++;
					}
					if (!targetDirectory.getName().startsWith(".")
							&& files != null) {
						stdout.write((targetDirectory.getName() + ":")
								.getBytes());
						if (!files.isEmpty() && count < numOfDirectories) {
							stdout.write((Configurations.NEWLINE).getBytes());
						}
						if (!files.isEmpty() && count == numOfDirectories) {
							stdout.write((Configurations.NEWLINE).getBytes());
						}
						printResults(stdout, files);
						if (count < numOfDirectories && !files.isEmpty()) {
							stdout.write((Configurations.NEWLINE).getBytes());
						} else if (count < numOfDirectories) {
							stdout.write((Configurations.NEWLINE).getBytes());
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (LsException e) {
					if (!doesFileExist(targetDirectory)) {
						try {
							stdout.write((Configurations.NEWLINE
									+ targetDirectory.getName() + ": Does not exist")
									.getBytes());
							if (count < numOfDirectories) {
								stdout.write((Configurations.NEWLINE)
										.getBytes());
							}
						} catch (IOException e2) {
							e.printStackTrace();
						}
					}
				}
			}
		} else if (args.length == 1) {
			File targetDirectory = new File(args[0].replaceAll(
					Configurations.NEWLINE, ""));
			files = getFiles(targetDirectory);
			printResults(stdout, files);
		} else if (args.length == 0) {
			File currentDirectory = new File(Environment.currentDirectory);
			files = getFiles(currentDirectory);
			printResults(stdout, files);
		} else {
			throw new LsException("Invalid arguments");
		}

	}

	/**
	 * Checks if given file exist
	 *
	 * @param file
	 *            file to be checked
	 */
	public boolean doesFileExist(File file) {
		if (file.exists() && !file.isDirectory()) {
			return true;
		}
		return false;
	}

	/**
	 * Helper Method to print non directory files
	 *
	 * @param args
	 *            input arguments
	 * @param stdout
	 *            outputStream
	 */
	void printNonDirectory(OutputStream stdout, String... args) {
		skipNewLine = true;
		numOfDirectories = 0;
		List<File> nonDirectoryFiles = new ArrayList<File>();
		for (String arg : args) {
			File file = new File(arg.replaceAll(Configurations.NEWLINE, ""));
			if (Files.exists(file.toPath())) {
				if (isDirectory(file)) {
					numOfDirectories++;
				} else {
					nonDirectoryFiles.add(file);
				}
			}
		}
		if (!nonDirectoryFiles.isEmpty()) {
			skipNewLine = false;
		}
		if (!nonDirectoryFiles.isEmpty()) {
			printResults(stdout, nonDirectoryFiles);
		}
	}

	/**
	 * Helper Method to print newline
	 */
	void printNewLine(OutputStream stdout) {
		if (!skipNewLine) {
			String newLines = Configurations.NEWLINE;
			try {
				stdout.write(newLines.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
			skipNewLine = true;
		}
	}

	/**
	 * Retrieves the list of files and print to stdout in a given format
	 *
	 * @param list
	 *            of files
	 */
	private void printResults(OutputStream stdout, List<File> files) {
		if (files != null) {
			try {
				stdout.write(convertFilesToString(files).getBytes());
				stdout.write(Configurations.NEWLINE.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Retrieves the list of files in the given directory
	 *
	 * @param directory
	 *            a directory path
	 * @return list of files in directory
	 * @throws LsException
	 */
	protected List<File> getFiles(File directory) throws LsException {
		if (Files.exists(directory.toPath())) {
			if (isDirectory(directory)) {
				File[] files = directory.listFiles();
				return Arrays.asList(files);
			} else {
				throw new LsException(directory.getName().replace(
						Configurations.NEWLINE, "")
						+ ": Not a directory");
			}
		} else {
			throw new LsException("No such file or directory");
		}
	}

	/**
	 * Converts the a list of files into formatted string for printing
	 *
	 * @param files
	 *            a list of files
	 * @return a string of all the files in the list
	 */
	protected String convertFilesToString(List<File> files) {
		String returnable = null;
		if (files != null) {
			StringBuilder stringBuilder = new StringBuilder();
			for (File file : files) {
				if (!file.getName().startsWith(".")) {
					stringBuilder.append(file.getName());
					if (file.isDirectory()) {
						stringBuilder.append(File.separator);
					}
					stringBuilder.append('\t');
				}
			}
			returnable = stringBuilder.toString();
		}
		return returnable;
	}

	/**
	 * Check if given file is a directory
	 *
	 * @param file
	 *            file to be check for directory
	 */
	public boolean isDirectory(File file) {
		if (file.isDirectory()) {
			return true;
		}
		return false;
	}
}