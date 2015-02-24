package sg.edu.nus.comp.cs4218.shell;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public class SequenceCommand implements Command {
	//Attributes
	Vector <Command> commands = new Vector<Command>();
	
	@Override
	public void evaluate(InputStream stdin, OutputStream stdout)
			throws AbstractApplicationException, ShellException {
		for (int i = 0; i < commands.size(); i++) {
			Command command = commands.get(i);
			command.evaluate(stdin, stdout);
		}
	}

	@Override
	public void terminate() {
	}
	
	public void addCommand (Command command) {
		commands.add(command);
	}
	
	public Command getCommand(int index) {
		return commands.get(index);
	}
	
	public int getCommandSize() {
		return this.commands.size();
	}

}
