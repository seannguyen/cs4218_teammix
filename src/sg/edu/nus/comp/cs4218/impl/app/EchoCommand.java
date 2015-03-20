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
			//processStdin();
		} else {
			StringBuilder stringBuilder = new StringBuilder();
			for(int i = 0; i < args.length; i++) {
				string = args[i];				
				if(args[i].equals("")) {
					return;
				} 
				stringBuilder.append(string);
				if (i != args.length) {
					stringBuilder.append(SPACE_SEPERATOR);
				}
			}					
			try {
				stdout.write(stringBuilder.toString().trim().getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void processStdin(InputStream stdin, OutputStream stdout) throws EchoException {
		
	}
}
