package sg.edu.nus.comp.cs4218.impl.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Stack;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Configurations;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.exception.CdException;
import sg.edu.nus.comp.cs4218.exception.SedException;

public class SedCommand implements Application {
  protected final static String NOTHING = "";
  protected String splitter = "/";
  protected final String MSG = " :" + " replacement expression invalid";

  /**
   * Perform Sed command
   *
   * @param args
   *          input arguments
   * @param stdin
   *          inputStream
   * @param stdout
   *          outputStream
   * @throws SedException
   */
  @Override
  public void run(String[] args, InputStream stdin, OutputStream stdout)
      throws SedException {
    String fileName = "";
    if (args == null || args.length == 0) {
      throw new SedException("Not enough arguments");
    } else if (args.length == 1) {
      processInputStream(stdin, stdout, args[0]);
    } else if (args.length == 2) {
      fileName = getAbsolutePath(args[1]);
      File file = new File(fileName);
      if (doesFileExist(file)) {
        processSed(stdout, file, args[0]);
      } else if (isDirectory(file)) {
        throw new SedException(" " + file.getName() + ":" + " Is a directory");
      } else {
        throw new SedException(" " + file.getName() + ":" + " Does not exist");
      }
    } else if (args.length > 2) {
      for (int i = 1; i < args.length; i++) {
        fileName = getAbsolutePath(args[i]);
        File file = new File(fileName);
        if (doesFileExist(file)) {
          try {
            stdout.write((file.getName() + ":" + Configurations.NEWLINE).getBytes());
          } catch (IOException e) {
            e.printStackTrace();
          }
          processSed(stdout, file, args[0]);
          if(i != args.length - 1) {
            try {
              stdout.write((Configurations.NEWLINE + Configurations.NEWLINE).getBytes());
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        } else {
          try {
            if (isDirectory(file)) {
              stdout.write((file.getName() + ": Is a directory")
                  .getBytes());
            } else {
              stdout.write((file.getName() + ": Does not exist")
                      .getBytes());
            }
            if(i != args.length - 1) {
              stdout.write((Configurations.NEWLINE + Configurations.NEWLINE).getBytes());
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

  /**
   * Process input from file and performs Sed
   *
   * @param stdout
   *          outputStream
   * @param file
   *          file to read
   * @param replacement
   *          input replacement expression
   * @throws SedException
   */
  private void processSed(OutputStream stdout, File file, String replacement)
      throws SedException {
    boolean gMode = validateReplacement(replacement);
    String regExp = replacement.split(splitter)[1];
    String replace = replacement.split(splitter)[2];
    try {
      BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
      String line;
      Boolean isFirst = true, needNewLine = true;
      try {
        while ((line = bufferedReader.readLine()) != null) {
          String newLine = line;
          if (!isFirst) {
            needNewLine = false;
            stdout.write(String.format("%n").getBytes());
          }
          if (gMode) {
            newLine = newLine.replaceAll(regExp, replace);
          } else {
            newLine = newLine.replaceFirst(regExp, replace);
          }
          stdout.write(newLine.getBytes());
          isFirst = false;
        }
        if (needNewLine) {
          stdout.write(String.format("%n").getBytes());
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * Validates replacement expression
   *
   * @param replacement
   *          input replacement expression
   * @return true if gMode is present
   * @throws SedException
   */
  boolean validateReplacement(String replacement) throws SedException {
    String preSplitter = "";
    Boolean specSplitter = false;
    if (replacement.length() < 2) {
      throw new SedException(replacement + MSG);
    } else {
      splitter = replacement.substring(1, 2);
      preSplitter = splitter;
      //\.[]{}()*+-?^$|
      if ("|".equals(splitter) || "\\".equals(splitter) || "$".equals(splitter)
          || ".".equals(splitter) || "[".equals(splitter) || "]".equals(splitter)
          || "{".equals(splitter) || "}".equals(splitter) || "*".equals(splitter)
          || "+".equals(splitter) || "-".equals(splitter) || "?".equals(splitter)
          || "^".equals(splitter)) {
        specSplitter = true;
        splitter = "\\" + splitter;
      }
    }
    String[] parts = replacement.split(splitter);
    boolean gMode = false;
    if (parts.length == 3 || parts.length == 4) {
      if(parts.length == 3 && !specSplitter &&
          !replacement.endsWith(splitter)) {
        throw new SedException(replacement + MSG);
      }
      if(parts.length == 3 && specSplitter &&
          !replacement.endsWith(preSplitter)) {
        throw new SedException(replacement + MSG);
      }
      if (!"s".equals(parts[0])) {
        throw new SedException(replacement + MSG);
      }
      if (parts.length == 4) {
        if ("g".equals(parts[3])) {
          gMode = true;
        } else {
          throw new SedException(replacement + MSG);
        }
      }
    } else {
      throw new SedException(replacement + MSG);
    }
    return gMode;
  }

  /**
   * Check if given file is a directory
   *
   * @param file
   *          file to be check for directory
   */
  public boolean isDirectory(File file) {
    if (file.isDirectory()) {
      return true;
    }
    return false;
  }

  /**
   * Get absolute path of given filePath
   *
   * @param filePath
   *          filePath to get absolute
   */
  public String getAbsolutePath(String filePath) {
    if (filePath.startsWith(Environment.currentDirectory)) {
      return filePath;
    }
    return Environment.currentDirectory + File.separator + filePath;
  }

  /**
   * Checks if given file exist
   *
   * @param file
   *          file to be checked
   */
  public boolean doesFileExist(File file) {
    if (file.exists() && !file.isDirectory()) {
      return true;
    }
    return false;
  }

  /**
   * Reads from stdIn and performs Sed
   * 
   * @param stdin
   *          InputStream
   * @param stdout
   *          OutputStream
   * @param replacement
   *          replacement Expression
   * @throws SedException
   */
  public void processInputStream(InputStream stdin, OutputStream stdout,
      String replacement) throws SedException {
    BufferedReader bufferedReader = null;
    String line;
    boolean isFirst = true, needNewLine = true;
    boolean gMode = validateReplacement(replacement);
    String regExp = replacement.split(splitter)[1];
    String replace = replacement.split(splitter)[2];

    if (stdin == null) {
      throw new SedException("Null stdin");
    }
    try {
      bufferedReader = new BufferedReader(new InputStreamReader(stdin));
      while ((line = bufferedReader.readLine()) != null) {
        if (gMode) {
          line = line.replaceAll(regExp, replace);
        } else {
          line = line.replaceFirst(regExp, replace);
        }
        if (!isFirst) {
          needNewLine = false;
          stdout.write(String.format("%n").getBytes());
        }
        stdout.write(line.getBytes());
        isFirst = false;
      }
      if (needNewLine) {
        stdout.write(String.format("%n").getBytes());
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (bufferedReader != null) {
        try {
          bufferedReader.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return;
  }
}
