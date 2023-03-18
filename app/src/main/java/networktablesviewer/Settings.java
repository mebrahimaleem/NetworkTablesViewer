package networktablesviewer;

import java.io.*;
import java.util.*;

/**
 * Holds user settings for the application
 */
public class Settings {
	/**
	 * How the application should obtain the NT server IP
	 * 0 - use Driver station
	 * 1 - use static IP
	 * 2 - use team number
	 */
	public int serverObtain;

	/** The team number stored in settings */
	public int team;
	
	/** The static IP stored in settings */
	public String ip;

	/**
	 * Reads and updates settings from the settings file
	 * This function has undefined behavior when the settings file does not exists or is not in the correct format
	 */
	public void read() {
		File f = new File("settings");
		Scanner inp = null;
		System.out.println(f.getAbsolutePath());

		try {
			inp = new Scanner(f);
			serverObtain = inp.nextInt();
			team = inp.nextInt();
			inp.nextLine();
			ip = inp.nextLine();
		}

		catch (Exception e) {}

		finally {
			try { inp.close(); }
			catch (Exception e) {}
		}
	}

	/**
	 * Writes current settings to the settings file
	 * This function has undefined behavior when the settings file does not exists or is not in the correct format
	 */
	public void save() {
		Writer w = null;

		try {
			w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("settings"), "utf-8"));
			w.write(Integer.toString(serverObtain) + "\n" + Integer.toString(team) + "\n" + ip + "\n");
		}

		catch (Exception e) {}

		finally {
			try { w.close(); }
			catch (Exception e) {} 
		}
	}
}
