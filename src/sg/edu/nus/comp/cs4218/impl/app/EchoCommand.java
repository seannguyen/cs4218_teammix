package sg.edu.nus.comp.cs4218.impl.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.EchoException;

public class EchoCommand implements Application{
	public static final String SPACE_SEPERATOR = " ";
	
	public EchoCommand() {
		// This constructor is intentionally empty. Nothing special is needed here.
	}

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws EchoException {		
		if(args.length == 0) {
			System.getProperty("line.seperator");			
			return;
		} else {
			StringBuilder stringBuilder = new StringBuilder();
			for(int i = 0; i < args.length; i++) {
				stringBuilder.append(args[i]);
				stringBuilder.append(SPACE_SEPERATOR);
			}
			//stringBuilder.append(System.getProperty("line.seperator"));			
			try {
				stdout.write(stringBuilder.toString().getBytes());
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}
	}
}
