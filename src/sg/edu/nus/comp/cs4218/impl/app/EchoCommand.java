package sg.edu.nus.comp.cs4218.impl.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.exception.EchoException;

public class EchoCommand implements Application{
	public static final String SPACE_SEPERATOR = " ";
	/**
	 * Perform Echo command
	 *
	 * @param args
	 *            input arguments
	 * @param stdin
	 *            inputStream
	 * @param stdout
	 *            outputStream
	 */	
	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws EchoException {	
		String string;
		if(args.length == 0) {					
			return;
		} else {
			StringBuilder stringBuilder = new StringBuilder();
			for(int i = 0; i < args.length; i++) {
<<<<<<< HEAD
				stringBuilder.append(args[i]);
				if (i != args.length - 1) {
					stringBuilder.append(SPACE_SEPERATOR);
				}
			}
			//stringBuilder.append(System.getProperty("line.seperator"));			
=======
				string = args[i];				
				if(args[i].equals("")) {
					return;
				} else if(args[i].charAt(0) == '"') {
					string = string.replaceAll("^\"|\"$", "");
				} else if(args[i].charAt(0) == '\'') {
					string = string.replaceAll("^\'|\'$", "");
				} else {
					string = string.replaceAll("\\\\", "");
				}
				stringBuilder.append(string);
				stringBuilder.append(SPACE_SEPERATOR);
			}					
>>>>>>> c00608f71d2be87c4f185c4b1e6d4d63b59f8b15
			try {
				stdout.write(stringBuilder.toString().trim().getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
