package sg.edu.nus.comp.cs4218.impl.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.EchoException;

public class Echo implements Application{
	public Echo() {
	}

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
		if(args.length == 1) {
			System.getProperty("line.seperator");
			return;
		} else {
			StringBuilder stringBuilder = new StringBuilder();
			for(int i = 1; i < args.length; i++) {
				stringBuilder.append(args[i]);
			}
			stringBuilder.append(System.getProperty("line.seperator"));			
			try {
				stdout.write(stringBuilder.toString().getBytes());
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}
	}
}
