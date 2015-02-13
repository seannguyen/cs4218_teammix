package sg.edu.nus.comp.cs4218.impl.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.PwdException;

public class PwdCommand implements Application {
  /**
   * Constructor to initialise Environment.currentDirectory
   *
   * @param currentDirectory
   *          an absolute directory path
   */
  public PwdCommand() {
  }

  /**
   * Check if directory is null, exist and isDirectory.
   *
   * @param directory
   *          a directory to be checked
   */
  protected String verifyDirectory(String directory) throws PwdException {
    File checkDirectory = new File(directory);
    // Error Handling
    if (checkDirectory == null || !checkDirectory.exists()
        || !checkDirectory.isDirectory()) {
      throw new PwdException("Cannot find working directory");
    }
    return directory;
  }

  /**
   * Perform print working directory command
   *
   * @param args
   *          [] input arguments
   * @param stdin
   *          inputStream
   * @param stdout
   *          outputStream
   */
  @Override
  public void run(String[] args, InputStream stdin, OutputStream stdout)
      throws AbstractApplicationException {
    if (args.length == 0) {
      try {
        stdout.write(verifyDirectory(Environment.currentDirectory).getBytes());
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      throw new PwdException("Invalid Command");
    }
  }

}