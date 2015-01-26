package wichert;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.SQLException;

/**
 * Die Klasse Main führt die Methode execute aus nachdem sie nachgesehen hat
 * welche der Befehlsfelder ausgefüllt sich und welche nicht und diese in die
 * zugehörigen Variablen speichert.
 * 
 * @author Patrick Wichert
 * @version 10.01.2015
 * 
 */
public class Main {
	public static void main(String[] args) {
		if (args.length < 3) 
		{
			System.out.println("-h ... Hostname\n-u ... Benutzername\n-p ... Passwort\n-d ... Datenbank\n-s ... Feld, nach dem sortiert werden soll\n-r ... Sortierrichtung. Standard: ASC\n-w ... eine Bedingung in SQL-Syntax, die zum Filtern der Tabelle verwendet wird\n-t ... Trennzeichen, dass für die Ausgabe verwendet werden soll\n-f ... Kommagetrennte Liste der Felder, die im Ergebnis enthalten sein sollen\n-o ... Name der Ausgabedatei sonst: Ausgabe auf der Konsole\n-T ... Tabellenname");
			System.exit(1);
		}

		String output = "";
		String host = "localhost", uname = System.getProperty("user.name"), pwd = "", dbname = "";
		String[] fields = new String[] { "*" };
		String tabname = "", sort = "", sortdir = "ASC", whereclause = "", terminator = ";";
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-h")) {
				host = args[i + 1];
			}
			if (args[i].equals("-u")) {
				uname = args[i + 1];
			}
			if (args[i].equals("-p")) {
				pwd = args[i + 1];
			}
			if (args[i].equals("-d")) {
				dbname = args[i + 1];
			}
			if (args[i].equals("-T")) {
				tabname = args[i + 1];
			}
			if (args[i].equals("-f")) {
				if (args[i + 1].charAt(0) == '\\') {
					fields = new String[] { "*" };
				} else {
					fields = args[i + 1].split(",");
				}
			}
			if (args[i].equals("-s")) {
				sort = args[i + 1];
			}
			if (args[i].equals("-r")) {
				sortdir = args[i + 1];
			}
			if (args[i].equals("-w")) {
				whereclause = args[i + 1];
			}
			if (args[i].equals("-t")) {
				terminator = args[i + 1];
			}
			if (args[i].equals("-o")) {
				output = args[i + 1];
			}
		}
		if (output.equals("")) {
			System.out.println(Exporter.execute(host, uname, pwd, dbname,
					fields, tabname, sort, sortdir, whereclause, terminator));
		} else {
			try {
				RandomAccessFile file = new RandomAccessFile(output, "rw");
				file.writeUTF(Exporter
						.execute(host, uname, pwd, dbname, fields, tabname,
								sort, sortdir, whereclause, terminator));
				file.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}