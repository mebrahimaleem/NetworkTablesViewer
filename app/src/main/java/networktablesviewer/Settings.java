package networktablesviewer;

import java.io.*;
import java.util.*;

public class Settings {
	public int serverObtain;
	public int team;
	public String ip;

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
