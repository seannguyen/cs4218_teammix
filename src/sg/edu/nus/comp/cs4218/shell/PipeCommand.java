package sg.edu.nus.comp.cs4218.shell;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public class PipeCommand implements Command {
	// Attributes
	Vector<Command> commands = new Vector<Command>();

	@Override
	public void evaluate(InputStream stdin, OutputStream stdout)
			throws AbstractApplicationException, ShellException {
		ByteArrayOutputStream pipeOut = new ByteArrayOutputStream();
		for (int i = 0; i < commands.size(); i++) {
			if (commands.size() == 1) {
				commands.get(i).evaluate(stdin, stdout);
			} else if (i == 0) {
				commands.get(i).evaluate(stdin, pipeOut);
			} else if (i == commands.size() - 1) {
				ByteArrayInputStream pipeIn = new ByteArrayInputStream(
						pipeOut.toByteArray());
				commands.get(i).evaluate(pipeIn, stdout);
			} else {
				ByteArrayInputStream pipeIn = new ByteArrayInputStream(
						pipeOut.toByteArray());
				commands.get(i).evaluate(pipeIn, pipeOut);
			}
		}
		terminate();
	}

	@Override
	public void terminate() {
		//terminate a pipe 
	}

	public void addCommand(Command command) {
		commands.add(command);
	}

	public Command getCommand(int index) {
		return commands.get(index);
	}

	public int getCommandSize() {
		return this.commands.size();
	}
}
