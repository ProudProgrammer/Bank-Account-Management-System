package db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public final class DBSettings {
	
	//egy alapértelmezett adatbázis cím, user és pass beégetve a programba
	private static final String ADDRESS = "//192.168.48.1:3306/bank"; 
	private static final String USER = "bank";
	private static final String PASS = "bank";
	
	//a konfigurációs fájl
	private static final File file = new File("settings/dbsettings.dat");
	
	private DBSettings() {
	}
	
	public static String[] getSettings() {
		Scanner scanner = null;
		String[] settings = new String[3];;
		try {
			scanner = new Scanner(file);
			while(scanner.hasNext()) {
				settings[0] = scanner.nextLine();
				settings[1] = scanner.nextLine();
				settings[2] = scanner.nextLine();
			}
		} catch (FileNotFoundException e) {
			settings[0] = ADDRESS;
			settings[1] = USER;
			settings[2] = PASS;
			return settings;
		} finally {
			if(scanner != null)
				scanner.close();
		}
		return settings;
	}

	public static void setSettings(String address, String user, String pass) throws FileNotFoundException {
		PrintWriter out = null;
		out = new PrintWriter(file);
		out.println(address);
		out.println(user);
		out.println(pass);
		if(out != null)
			out.close();
	}
}
