package sg.edu.nus.comp.cs4218.impl.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
			processStdin(stdin, stdout);
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
		String line = "";
		BufferedReader bufferedReader = null;
		
		try { 
			bufferedReader = new BufferedReader(new InputStreamReader(stdin));
			while ((line = bufferedReader.readLine()) != null) {
				line = line + String.format("%n");
				stdout.write(line.getBytes());
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
