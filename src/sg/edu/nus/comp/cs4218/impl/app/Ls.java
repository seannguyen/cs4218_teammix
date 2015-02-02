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

public class Ls implements Application {
    protected Environment environment;
  
    public Ls(String currentDirectory) {
      environment.currentDirectory = currentDirectory;
    }
    
    protected List<File> getFiles(File directory) {
        if (Files.exists(directory.toPath())) {
            File[] files = directory.listFiles();
            return Arrays.asList(files);
        } else {
            return null;
        }
    }

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

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws LsException {
      List<File> files = null;
      if (args.length == 1) {
          File targetDirectory = new File(args[1]);
          files = getFiles(targetDirectory);
      } else if (args.length == 0) {
          File currentDirectory = new File(environment.currentDirectory);
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