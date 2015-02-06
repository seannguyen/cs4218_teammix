package sg.edu.nus.comp.cs4218.shell;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public class PipeCommand implements Command {
	//Attributes
	Vector <Command> commands = new Vector<Command>();
		
	@Override
	public void evaluate(InputStream stdin, OutputStream stdout)
			throws AbstractApplicationException, ShellException {
		//This is and old complicated piping method  
//		PipedInputStream nonFinalLastIn = (PipedInputStream) stdin;
//		for (int i = 0; i < commands.size(); i++) {
//			final PipedInputStream in = new PipedInputStream();
//			final PipedInputStream lastIn = nonFinalLastIn;
//			final PipedOutputStream out = new PipedOutputStream(in);
//			final Command command = commands.get(i);
//			new Thread(
//					new Runnable(){
//						public void run(){
//							try {
//								command.evaluate(lastIn, out);
//							} catch (AbstractApplicationException | ShellException | IOException e) {
//								e.printStackTrace();
//							}
//						}
//					}
//					).start();
//			nonFinalLastIn = in;
//		}
//		terminate();
		
		//This is an simpler way
		ByteArrayOutputStream pipeOut = new ByteArrayOutputStream();
		for (int i = 0; i < commands.size(); i++) {
			if (commands.size() == 1) {
				commands.get(i).evaluate(stdin, stdout);
			} else if (i == 0) {
				commands.get(i).evaluate(stdin, pipeOut);
			} else if (i == commands.size() - 1){
				ByteArrayInputStream pipeIn = new ByteArrayInputStream(pipeOut.toByteArray());
				commands.get(i).evaluate(pipeIn, stdout);
			} else {
				ByteArrayInputStream pipeIn = new ByteArrayInputStream(pipeOut.toByteArray());
				commands.get(i).evaluate(pipeIn, pipeOut);
			}
		}
		terminate();
	}

	@Override
	public void terminate() {
	}
	
	public void addCommand (Command command) {
		commands.add(command);
	}

}
