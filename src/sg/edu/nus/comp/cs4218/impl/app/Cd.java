package sg.edu.nus.comp.cs4218.impl.app;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Stack;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CdException;

public class Cd implements Application{
  protected Environment environment;
  
  public Cd(String currentDirectory) {
    environment.currentDirectory = currentDirectory;
  }
  
  protected File changeDirectory(String newDirectory) throws CdException {
      if (newDirectory != null){
          File newDir = new File(newDirectory);
          if (newDir.isDirectory()){
              return newDir;
          }
      } else {
        throw new CdException("Invalid Directory");
      }
      return null;
  }

  protected static String formatDirectory(String curAbsoluteDir, String newRelativeDir){
    String separator = File.separator;
    if(File.separator.equals("\\")){
        separator =("\\\\");
    }
    if (curAbsoluteDir != null ){
        Stack<String> newAbsoluteDir = new Stack<String>();
        newAbsoluteDir.addAll(Arrays.asList(curAbsoluteDir.split(separator)));
        if (newRelativeDir != null){
            for(String token: Arrays.asList(newRelativeDir.split(separator))){
                if (!token.equals("")){
                    if (token.equals("..")){            // transverse up 
                        newAbsoluteDir.pop();
                    }else if ((token.equals("."))){     // remain 
                    }else{                              // transverse down
                        newAbsoluteDir.push(token);
                    }
                }
            }
        }
        
        StringBuilder newWorkingDir = new StringBuilder();
        if (System.getProperty("os.name").toLowerCase().indexOf("mac") > 0){
            newWorkingDir.append(File.separator);       // mac os directory format
        }
        for (int i = 0; i<newAbsoluteDir.size(); i++){
            newWorkingDir.append(newAbsoluteDir.get(i));
                newWorkingDir.append(File.separator);
        }
        return newWorkingDir.toString();
    }else{
        return "";
    }
  }
  
  @Override
  public void run(String[] args, InputStream stdin, OutputStream stdout) throws CdException {
    File newDirectory = null;
    if (args.length==0){
      newDirectory = changeDirectory(System.getProperty( "user.dir" ));
    }else if(args.length == 1){ 
        if (args[0].equals("~")){
          newDirectory = changeDirectory(System.getProperty( "user.dir" ));
        }else if (Paths.get(args[0]).isAbsolute()){
          newDirectory = changeDirectory(args[0]);
        }else{
          newDirectory = changeDirectory(formatDirectory(environment.currentDirectory, args[0]));
        }
    }else{
      throw new CdException("Invalid arguments");
    }
    
    if (newDirectory != null){
        environment.currentDirectory =  newDirectory.getAbsolutePath();
    }
  }
}
