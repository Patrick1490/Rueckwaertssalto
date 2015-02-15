package wichert;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Die Klasse Exporter bastelt mit den eingegebenen Befehlen einen Select befehl
 * zusammen und dieser wird dann aus der Datenbank die auch angegeben werden
 * muss ausgelesen.
 * 
 * @author Patrick Wichert
 * @version 10.01 2015
 * 
 */

public class Rueckwaertssalto {
	static final String JDBC = "com.mysql.jdbc.Driver";

	/**
	 * @param host
	 * @param uname
	 * @param pwd
	 * @param dbname
	 * @return
	 */
	public static String execute(String host, String uname, String pwd,
			String dbname) {
		Connection conn = null;
		Statement stmt = null;
		String outp = "";
		try {
			String DB_URL = "jdbc:mysql://" + host + "/" + dbname;
			Class.forName(JDBC);
			// F�r die Verbindung auf die Datenbank und zum einlesen der
			// Statements
			conn = DriverManager.getConnection(DB_URL, uname, pwd);
			stmt = conn.createStatement();

			ArrayList<String> tables = new ArrayList<>();
			;
			HashMap<String, ArrayList<String>> keys = new HashMap<>();
			;

			// dmd gibt alle Daten der Datenbank aus
			DatabaseMetaData dmd = conn.getMetaData();
			// Holt alle Tabellen der Datenbank
			ResultSet dmdrs = dmd.getTables(null, null, null, null);
			// Geht Schritt f�r Schritt alle Tabellen durch um die Keys zu
			// finden
			while (dmdrs.next()) {
				// Gibt die Metadaten Information zur�ck (3 steht f�r den
				// Tabellennamen)
				String table_name = dmdrs.getString(3);
				// F�r die Primary Keys
				ResultSet dmdpk = dmd.getPrimaryKeys(null, null, table_name),
				// F�r die Foreign Keys
				dmdfk = dmd
						.getImportedKeys(conn.getCatalog(), null, table_name),
				// F�r die Attribute
				dmdattr = dmd.getColumns(null, null, table_name, null);
				// F�r jede Tabelle eine ArrayList
				ArrayList<String> alle_Keys = new ArrayList<>();
				// Rausfinden der Primary Keys
				while (dmdpk.next()) {
					boolean isPK = true;
					// Rausfinden der Foreign Keys
					while (dmdfk.next()) {
						// Alle Columnnames die einen Foreignkey haben
						String name = dmdfk.getString("FKCOLUMN_NAME");
						// Schauen ob der Foreignkey auch ein Primarykey ist.
						// Wenn der Fall eintritt wird es mit PK und FK
						// markiert.
						if (dmdpk.getString(4).equals(name)) {
							isPK = false;
							String fk_table = dmdfk.getString("FKTABLE_NAME");
							alle_Keys.add("<<PK>><<FK>>" + fk_table + "."
									+ name);
						}
					}
					// Wenn es nicht so ist dann nur PK davor schreiben.
					if (isPK)
						alle_Keys.add("<<PK>>" + dmdpk.getString(4));
				}
				// holt alle Foreign keys der Tabelle
				dmdfk = dmd
						.getImportedKeys(conn.getCatalog(), null, table_name);
				while (dmdfk.next()) {
					String fk_name = dmdfk.getString("FKCOLUMN_NAME"), fk_table = dmdfk
							.getString("FKTABLE_NAME");
					boolean onlyFK = true;
					for (int i = 0; i < alle_Keys.size(); ++i) {
						// Wenn der Key schon mit PK und FK markiert sein sollte
						// dann passiert nichts.
						if (alle_Keys.get(i).contains(fk_name)) {
							onlyFK = false;
							break;
						}
					}
					// Wenn es noch nicht drinnen steht dann wird ein einfaches
					// FK davor geschrieben.
					if (onlyFK)
						alle_Keys.add("<<FK>>" + fk_table + "." + fk_name);
				}
				while (dmdattr.next()) {
					String key_name = dmdattr.getString("COLUMN_NAME");
					boolean isIncl = true;
					for (int i = 0; i < alle_Keys.size(); ++i) {
						if (alle_Keys.get(i).contains(key_name)) {
							isIncl = false;
							break;
						}
					}
					if (isIncl)
						alle_Keys.add(key_name);
				}
				keys.put(table_name, alle_Keys);
				tables.add(table_name);
			}

			for (int i = 0; i < tables.size(); i++) {
				System.out.println("Tabelle: " + tables.get(i));
				ArrayList<String> tempo = keys.get(tables.get(i));
				for (int x = 0; x < tempo.size(); x++) {
					System.out.println(tempo.get(x));
				}
			}		
			
			// ERD-Diagramm zeichnen/Dot-File erstellen
			// Im RM werden zwar schon die ganzen PKs und FKs gespeichert aber
			// hier wird nochmal alles aufgerufen
			// weil es umst�ndlich wird die ganzen Sachen nochmal aufzurufen und
			// duchzuordnen etc.

			// x Variable, weil nodes nicht den selben namen haben dürfen,labels
			// aber schon. Wird immer wieder erh�ht
			
			int x = 0;
			//dot-Files beginnen mit graph G( bzw je nach dem welche Struktur verwendet(zb. diagraph))
			String erd = "graph G{ overlap = false \n";
			//Geht alles solange durch, bis alle Tabellen durchgearbeitet worden sind
			for (int i = 0; i < tables.size(); i++) {
				//Gibt den Entit�ten Box-Form
				erd += tables.get(i) + " [shape=box];\n";
				ResultSet fk1 = dmd.getImportedKeys(null, null,
						tables.get(i));
				//Hier werden foreignkeys besorgt
				ArrayList<String> fkList = new ArrayList<String>();
				while (fk1.next()) {
					fkList.add(fk1.getString("PKCOLUMN_NAME"));
				}
				fk1.close();
				//Hier primarykeys besorgt
				ResultSet pk1 = dmd.getPrimaryKeys(null, null, tables.get(i));
				ArrayList<String> pks = new ArrayList<String>();
				//Wenn in der fkListe schon ein pk vorhanden ist
				//wird normal geaddet ohne dass der primarykey das label u bekommt (f�r nur primarykey)
				//(wird dann sp�ter verarbeitet)
				while (pk1.next()) {
					if (fkList.contains(pk1.getString(4))) {
						pks.add(pk1.getString(4));
					} else {
					//Label u f�r unterstreichen	
						erd += "id" + x + " [label=<<u>" + pk1.getString(4)
								+ "</u>>];\n";
						erd += tables.get(i) + " -- id" + x + ";\n";
						x++;
						pks.add(pk1.getString(4));
					}
				}
				pk1.close();
				//hier werden wieder foreignkeys geholt,
				ResultSet fk2 = dmd.getImportedKeys(null, null,
						tables.get(i));
				ArrayList<String> fks = new ArrayList<String>();
				//pkTable �berpr�ft dann ob es eine beziehung schon mal gab oder nicht
				String pkTable = "";
				while (fk2.next()) {
					String fkColumnName =fk2.getString("FKCOLUMN_NAME");
					String pkTableName = fk2.getString("PKTABLE_NAME");
				//Gibt es im pkTable schon den pk der tabelle wird nichts unternommen	
					if (pkTable.equals(pkTableName)) {
				//ansonsten wird die beziehung gebaut und mit den jeweiligen labels versehen
					} else {
						pkTable = pkTableName;
						fks.add(fk2.getString("PKCOLUMN_NAME"));
						//Wenn primarykey ein foreignkey ist wird er rot und unterstrichen
						if (pks.contains(fkColumnName)) {
							erd += pkTableName + " [shape=box];\n"; 
							erd += "id" + x + 200
									+ "[shape=diamond, label=hat]\n";
							erd += tables.get(i) + "-- id" + x + 200
									+ " [label=1];";
							erd += "id" + x + 200 + " -- " + pkTableName
									+ "[label=n];\n";
							erd += "id" + x + "[label=<<u>" + fkColumnName
									+ "</u>> fontcolor=Red];\n"; 
							erd += tables.get(i) + " -- id" + x + ";\n";
							x++;
						} else {
							//ansonsten wird der primarykey dann nur rot gehalten (kein strichliertes unterstreichen gefunden)
							erd += pkTableName + " [shape=box];\n";
							erd += "id" + x + 200
									+ "[shape=diamond, label=hat]\n";
							erd += tables.get(i) + "-- id" + x + 200
									+ " [label=1];";
							erd += "id" + x + 200 + " -- " + pkTableName
									+ "[label=n];\n";
							erd += "id" + x + " [label=" + fkColumnName
									+ " fontcolor=Red];\n";
							erd += tables.get(i) + " -- id" + x + ";\n";
							x++;
						}
					}
				}

				fk2.close();
				// Columns der Tabellen geholt
				
				ArrayList<String> columns1 = new ArrayList<String>();
				ResultSet result = dmd.getColumns(null, null, tables.get(i),
						null);
				while (result.next()) {
					columns1.add(result.getString(4));
				}
				result.close();
				// Alles was kein Pk/Fk ist wird hinzugef�gt 
				for (int k = 0; k < columns1.size(); k++) {
					
					if (pks.contains(columns1.get(k).toString())
							&& fks.contains(columns1.get(k).toString())) {

					} else if (pks.contains(columns1.get(k).toString())) {

					} else if (fks.contains(columns1.get(k).toString())) {

					} else {
						//Bekommen keine speziellen labels
						erd += "id" + x + " [label="
								+ columns1.get(k).toString() + "];\n";
						erd += tables.get(i) + " -- id" + x + ";\n";
						x++;
					}
				}
				erd += "\n";
			}
			erd += "}";

			stmt.close();

			//Erzeugt ein File 
			File dotDatei = new File("erd_"+dbname+".dot");
			BufferedWriter output = new BufferedWriter(new FileWriter(dotDatei));
			output.write(erd);
			output.close();
			Rueckwaertssalto.draw();

			conn.close();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		return outp;
	}
	public static void draw()
	{
		try {
			//Beim ersten wird der Pfad bis zur neato.exe von GraphViz angegeben
			//-Tpng ist f�r eine Datei mit der Endung .png
			//Danach kommt der Pfad vom .dot file angegeben
			//und der letzte Pfad ist der Pfad wo das File bzw. Bild hingespeichert werden soll.
			Runtime.getRuntime().exec("C:\\schule\\2014-2015\\Insy\\graphviz-2.38\\release\\bin\\neato.exe -Tpng C:\\Users\\workspace\\Rückwärtssalto\\erd_test1.dot -o C:\\Users\\workspace\\Rückwärtssalto\\erd_test1.png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}