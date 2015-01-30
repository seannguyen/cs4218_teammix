package sg.edu.nus.comp.cs4218.impl.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.PwdException;

public class Pwd implements Application{
    protected Environment environment;
    
    public Pwd(String currentDirectory) {
      environment.currentDirectory = currentDirectory;
    }
    
    protected String converDirectoryToString(String directory) throws PwdException {
      File checkDirectory = new File(directory);
        //Error Handling
        if(checkDirectory == null || !checkDirectory.exists() || !checkDirectory.isDirectory()){
            throw new PwdException("Cannot find working directory");
        }
        //Processing the 
        return directory;
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
      if (args.length == 1){
        try {
          stdout.write(converDirectoryToString(environment.currentDirectory).getBytes());
        } catch (IOException e) {
          e.printStackTrace();
        }
      }else{
        throw new PwdException("Invalid Command");
      }
    }


    
}