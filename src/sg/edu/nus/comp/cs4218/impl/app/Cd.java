package sg.edu.nus.comp.cs4218.impl.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.AbstractApplication;
import sg.edu.nus.comp.cs4218.exception.CdException;
import sg.edu.nus.comp.cs4218.util.common_util;

public class Cd extends AbstractApplication implements Application{
  private File workingDirectory;
  
  public Cd(String[] arguments) {
    super(arguments);
    if(args == null || args.length == 0 || !args[0].equals("cd")) {
      //set status code
    }
  }
  
  public File changeDirectory(String newDirectory) throws CdException {
      if (newDirectory != null){
          File newDir = new File(newDirectory);
          if (newDir.isDirectory()){
              //setStatusCode(55);
              return newDir;
          }
      } else {
        throw new CdException("Invalid Directory");
      }
      return null;
  }

  @Override
  public void run(String[] args, InputStream stdin, OutputStream stdout) throws CdException {
    File newDirectory = null;
    if (args.length==1){
      newDirectory = changeDirectory(System.getProperty( "user.dir" ));
    }else if(args.length == 2){ 
        if (args[1].equals("~")){
          newDirectory = changeDirectory(System.getProperty( "user.dir" ));
        }else if (Paths.get(args[1]).isAbsolute()){
          newDirectory = changeDirectory(args[1]);
        }else{
          newDirectory = changeDirectory(common_util.formatDirectory(workingDirectory.getAbsolutePath(), args[1]));
        }
    }else{
      throw new CdException("Invalid arguments");
    }
    
    if (newDirectory != null){
        workingDirectory =  new File(newDirectory.getAbsolutePath());
    }
    
    try {
      stdout.write(workingDirectory.toString().getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
    
  }
}
