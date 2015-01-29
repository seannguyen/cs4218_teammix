package sg.edu.nus.comp.cs4218.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Random;
import java.util.Stack;

public class common_util {
  public static String formatDirectory(String curAbsoluteDir, String newRelativeDir){
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
}
