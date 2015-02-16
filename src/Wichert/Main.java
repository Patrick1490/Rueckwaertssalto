package wichert;

import java.io.IOException;
import java.io.RandomAccessFile;


/**
 * Die Klasse Main führt die Methode erzeugen aus nachdem sie nachgesehen hat
 * welche der Befehlsfelder ausgefüllt sich und welche nicht und diese in die
 * zugehörigen Variablen speichert.
 * 
 * @author Patrick.W
 * @version 10.01.2015
 * 
 */
public class Main 
{
	public static void main(String[] args) throws IOException {
		if (args.length < 3) 
		{
			System.out
					.println("-h ... Hostname\n-u ... Benutzername\n-p ... Passwort\n-d ... Datenbank");
			System.exit(1);
		}

		String output = "";
		String host = "localhost", uname = System.getProperty("user.name"), pwd = "", dbname = "";
		for (int i = 0; i < args.length; i++) 
		{
			if (args[i].equals("-h")) 
			{
				host = args[i + 1];
			}
			if (args[i].equals("-u")) 
			{
				uname = args[i + 1];
			}
			if (args[i].equals("-p")) 
			{
				pwd = args[i + 1];
			}
			if (args[i].equals("-d")) 
			{
				dbname = args[i + 1];
			}
		}
		if (output.equals("")) {
			System.out.println(Rueckwaertssalto.erzeugen(host, uname, pwd, dbname));
		} 
		else 
		{
			try 
			{
				RandomAccessFile file = new RandomAccessFile(output, "rw");
				file.writeUTF(Rueckwaertssalto.erzeugen(host, uname, pwd, dbname));
				file.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
}