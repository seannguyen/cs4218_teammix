package sg.edu.nus.comp.cs4218;

import java.util.HashMap;
import java.util.Map;

public final class Environment {
	
	/**
	 * Java VM does not support changing the current working directory. 
	 * For this reason, we use Environment.currentDirectory instead.
	 */
	public static volatile String currentDirectory = System.getProperty("user.dir");
	public static boolean running = true;
	
	public static Map<String, Application> nameAppMaps = new HashMap<String, Application>();
	
	private Environment() {
	};
	
}
