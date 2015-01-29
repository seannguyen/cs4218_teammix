package sg.edu.nus.comp.cs4218;

import java.io.File;

public abstract class AbstractApplication {
	protected String[] args;
	private int statusCode = 0;
	
	public AbstractApplication(String[] args) {
		this.args = args;
	}
	
	public abstract String execute(File workingDir, String stdin);
	
	public int getStatusCode() {
		return statusCode;
	}
	
	protected void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
}
