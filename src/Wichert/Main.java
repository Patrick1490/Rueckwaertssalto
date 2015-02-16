package wichert;

import java.io.IOException;


/**
 * Die Klasse Main führt die Methode erzeugen aus nachdem sie nachgesehen hat
 * welche der Befehlsfelder ausgefüllt sich und welche nicht und diese in die
 * zugehörigen Variablen speichert.
 * 
 * @author Patrik Velkoski & Patrick Wichert
 * @version 16.02.2015
 * 
 */
public class Main 
{
	public static void main(String[] args) throws IOException {
		if (args.length < 3) 
		{
			System.out.println("-h ... Hostname\n-u ... Benutzername\n-p ... Passwort\n-d ... Datenbank\n-n ... Path vom Neato File (jeder \\ muss doppelt gemacht werden!!)\n-dot ... Path vom Dot File das gezeichnet werden soll\n-s ... Path vom Ort wohin das File gespeichert werden soll als png");
			System.exit(1);
		}
		String host = "localhost", uname = System.getProperty("user.name"), pwd = "", dbname = "", pathNeato = "", pathDot = "", speicher = "";
		int lauf1 = 0,lauf2 = 0,lauf3 = 0,lauf4 = 0,lauf5 = 0,lauf6 = 0,lauf7 = 0;
		System.out.println("Doppelte Einträge werden verworfen!");
		for (int i = 0; i < args.length; i++) 
		{
			if (args[i].equals("-h")) 
			{
				lauf1++;
				if(lauf1 == 1)
					host = args[i + 1];
			}
			if (args[i].equals("-u")) 
			{
				lauf2++;
				if(lauf2 == 1)
					uname = args[i + 1];
			}
			if (args[i].equals("-p")) 
			{
				lauf3++;
				if(lauf3 == 1)
					pwd = args[i + 1];
			}
			if (args[i].equals("-d")) 
			{
				lauf4++;
				if(lauf4 == 1)
					dbname = args[i + 1];
			}
			if (args[i].equals("-n")) 
			{
				lauf5++;
				if(lauf5 == 1)
					pathNeato = args[i + 1];
			}
			if (args[i].equals("-dot")) 
			{
				lauf6++;
				if(lauf6 == 1)
					pathDot = args[i + 1];
			}
			if (args[i].equals("-s")) 
			{
				lauf7++;
				if(lauf7 == 1)
					speicher = args[i + 1];
			}
		}
		if (dbname.equals("")) {
			System.out.println("Überprüfe nochmals die Eingabe! Die Datenbank wurde nicht eingetragen!");
		} 
		else 
		{
			Rueckwaertssalto.erzeugen(host, uname, pwd, dbname, pathNeato, pathDot, speicher);
		}
	}
}