package wichert;

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
			String dbname) 
	{
		Connection conn = null;
		Statement stmt = null;
		String outp = "";
		try 
		{
			String DB_URL = "jdbc:mysql://" + host + "/" + dbname;
			Class.forName(JDBC);
			// Für die Verbindung auf die Datenbank und zum einlesen der
			// Statements
			conn = DriverManager.getConnection(DB_URL, uname, pwd);
			stmt = conn.createStatement();

			ArrayList<String> tables = new ArrayList<>();;
			HashMap<String, ArrayList<String>> keys = new HashMap<>();;
			
			//dmd gibt alle Daten der Datenbank aus
			DatabaseMetaData dmd = conn.getMetaData(); 
			//Holt alle Tabellen der Datenbank
			ResultSet dmdrs = dmd.getTables(null, null, null, null);
			//Geht Schritt für Schritt alle Tabellen durch um die Keys zu finden
			while(dmdrs.next()) 
			{ 
				//Gibt die Metadaten Information zurück (3 steht für den Tabellennamen)
				String table_name = dmdrs.getString(3); 
				//Für die Primary Keys
				ResultSet dmdpk = dmd.getPrimaryKeys(null, null, table_name),
						//Für die Foreign Keys
						  dmdfk = dmd.getImportedKeys(conn.getCatalog(), null, table_name), 
						//Für die Attribute
						  dmdattr = dmd.getColumns(null, null, table_name, null);
				//Für jede Tabelle eine ArrayList
				ArrayList<String> alle_Keys = new ArrayList<>(); 
				//Rausfinden der Primary Keys
				while(dmdpk.next()) 
				{
					boolean isPK = true;
					//Rausfinden der Foreign Keys
					while(dmdfk.next()) 
					{ 
						//Alle Columnnames die einen Foreignkey haben
						String name = dmdfk.getString("FKCOLUMN_NAME");
						//Schauen ob der Foreignkey auch ein Primarykey ist.
						//Wenn der Fall eintritt wird es mit PK und FK markiert.
						if(dmdpk.getString(4).equals(name)) 
						{
							isPK = false;
							String fk_table = dmdfk.getString("FKTABLE_NAME");
							alle_Keys.add("<<PK>><<FK>>" + fk_table + "." + name);
						}
					}
					//Wenn es nicht so ist dann nur PK davor schreiben.
					if(isPK)
						alle_Keys.add("<<PK>>" + dmdpk.getString(4));
				}
				//holt alle Foreign keys der Tabelle
				dmdfk = dmd.getImportedKeys(conn.getCatalog(), null, table_name);
				while(dmdfk.next()) 
				{
					String fk_name = dmdfk.getString("FKCOLUMN_NAME"),
							fk_table = dmdfk.getString("FKTABLE_NAME");
					boolean onlyFK = true;
					for(int i = 0;i < alle_Keys.size(); ++i) 
					{
						//Wenn der Key schon mit PK und FK markiert sein sollte dann passiert nichts.
						if(alle_Keys.get(i).contains(fk_name)) 
						{
							onlyFK = false;
							break;
						}
					}
					//Wenn es noch nicht drinnen steht dann wird ein einfaches FK davor geschrieben.
					if(onlyFK)
						alle_Keys.add("<<FK>>" + fk_table + "." + fk_name);
				}
				while(dmdattr.next()) 
				{
					String key_name = dmdattr.getString("COLUMN_NAME");
					boolean isIncl = true;
					for(int i = 0;i < alle_Keys.size(); ++i) 
					{
						if(alle_Keys.get(i).contains(key_name)) 
						{
							isIncl = false;
							break;
						}
					}
					if(isIncl)
						alle_Keys.add(key_name);
				}
				keys.put(table_name, alle_Keys);
				tables.add(table_name);
			}

			for(int i = 0; i < tables.size();i++)
			{
				System.out.println("Tabelle: "+tables.get(i));
				ArrayList<String> tempo = keys.get(tables.get(i));
				for(int x = 0; x < tempo.size(); x++)
				{
					System.out.println(tempo.get(x));
				}
			}
			stmt.close();
			conn.close();
		} 
		catch (SQLException se) 
		{
			se.printStackTrace();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		finally 
		{
			try 
			{
				if (stmt != null)
					stmt.close();
			} 
			catch (SQLException se2) 
			{
			}
			try {
				if (conn != null)
					conn.close();
			} 
			catch (SQLException se) 
			{
				se.printStackTrace();
			}
		}
		return outp;
	}
}