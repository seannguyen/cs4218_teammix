package sg.edu.nus.comp.cs4218.impl.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.LsException;

public class LsCommand implements Application {
    /**
     * Constructor to initialise Environment.currentDirectory
     *
     * @param currentDirectory
     *          an absolute directory path
     */
    public LsCommand() {
    }
    
    /**
     * Retrieves the list of files in the given directory
     *
     * @param directory
     *          a directory path
     * @return list of files in directory
     */
    protected List<File> getFiles(File directory) {
        if (Files.exists(directory.toPath())) {
            File[] files = directory.listFiles();
            return Arrays.asList(files);
        } else {
            return null;
        }
    }
    
    /**
     * Converts the a list of files into formatted string 
     * for printing
     *
     * @param files
     *          a list of files
     * @return a string of all the files in the list
     */
    protected String convertFilesToString(List<File> files) {
        String returnable = null;
        if (files != null) {
            StringBuilder stringBuilder = new StringBuilder();
            for (File file : files) {
                stringBuilder.append(file.getName());
                if (file.isDirectory()) {
                    stringBuilder.append(File.separator);
                }
                stringBuilder.append('\t');
            }
            returnable = stringBuilder.toString();
        } 
        return returnable;
    }

    /**
     * Perform List directory command
     *
     * @param args
     *          input arguments
     * @param stdin
     *          inputStream
     * @param stdout
     *          outputStream
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws LsException {
      List<File> files = null;
      if (args.length == 1) {
          File targetDirectory = new File(args[0]);
          files = getFiles(targetDirectory);
      } else if (args.length == 0) {
          File currentDirectory = new File(Environment.currentDirectory);
          files = getFiles(currentDirectory);
      }
      
      if(files != null) {
        try {
          stdout.write(convertFilesToString(files).getBytes());
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      
    }

}