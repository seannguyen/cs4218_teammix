package sg.edu.nus.comp.cs4218.impl.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Vector;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Configurations;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CdException;
import sg.edu.nus.comp.cs4218.exception.FindException;

public class FindCommand implements Application {
  protected final static String NOTHING = "";
  protected final static String FILE_SEPARATOR = "file.separator";
  
  protected Vector<String> getFilesFromPattern(String start, String args) throws IOException {
        final Vector<String> results = new Vector<String>();
        String root = start + File.separator;
        Path startDir = Paths.get(root);
        FileSystem fileSystem = FileSystems.getDefault();
        String globPattern = "glob:" + root + args;

        if (System.getProperty(FILE_SEPARATOR) != null 
                && System.getProperty(FILE_SEPARATOR).equals(Configurations.WINDOWS_FILESEPARATOR)) {
            globPattern = globPattern.replace("\\", "\\\\");
        }
        final PathMatcher matcher = fileSystem.getPathMatcher(globPattern);

        FileVisitor<Path> matcherVisitor = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attribs) {
                return checkFileName(file);
            }
            @Override
            public FileVisitResult preVisitDirectory(Path file, BasicFileAttributes attribs) {
                return checkFileName(file);
            }
            
            private FileVisitResult checkFileName(Path file) {
                if (matcher.matches(file)) {
                    results.add(file.toString());
                }
                return FileVisitResult.CONTINUE;
            }
        };
        Files.walkFileTree(startDir, matcherVisitor);
    return results;
  }
  
  private void checkErrorStatus(int error) throws FindException {
    if(error == 1) {
      throw new FindException(Configurations.MESSGE_ERROR_FILENOTFOUND);
    } else if(error == 2){
      throw new FindException("Invalid Directory");
    }
  }
  
  /**
   * Perform change directory command
   *
   * @param args
   *          input arguments
   * @param stdin
   *          inputStream
   * @param stdout
   *          outputStream
   */
  @Override
  public void run(String[] args, InputStream stdin, OutputStream stdout)
      throws FindException {
    Vector<String> results = new Vector<String>();
    String pattern = NOTHING, root = NOTHING;
    int error = 0;
    if (args.length == 2) {
      if(!("-name").equals(args[0])) {
        throw new FindException("Missing -name");
      } 
      root = Environment.currentDirectory;
      pattern = args[1];
    } else if (args.length == 3) {
      if(!("-name").equals(args[1])) {
        throw new FindException("Missing -name");
      } 
      root = args[0];
      pattern = args[2];
    }
    try {
      results = getFilesFromPattern(root, pattern);
    } catch (IOException e) {
      error = 1;
    } catch (InvalidPathException e) {
      error = 2;
    }
    
    checkErrorStatus(error);
    
    if (System.getProperty(FILE_SEPARATOR) != null 
        && System.getProperty(FILE_SEPARATOR).equals(Configurations.WINDOWS_FILESEPARATOR)) {
      root = root.replace("\\", "\\\\");
    }
    
    for(String result : results) {
      System.out.println(result.replaceFirst(root, NOTHING));
    } 
  }
}
