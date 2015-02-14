import java.sql.DatabaseMetaData;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Die Klasse Exporter bastelt mit den eingegebenen Befehlen einen Select befehl
 * zusammen und dieser wird dann aus der Datenbank die auch angegeben werden
 * muss ausgelesen.
 * 
 * @author Patrick Wichert
 * @version 10.01 2015
 * 
 */

public class Exporter {
	static final String JDBC = "com.mysql.jdbc.Driver";

	/**
	 * @param host
	 * @param uname
	 * @param pwd
	 * @param dbname
	 * @param fields
	 * @param tabname
	 * @param sort
	 * @param sortdir
	 * @param whereclause
	 * @param terminator
	 * @return
	 */
	public static String execute(String host, String uname, String pwd,
			String dbname, String[] fields, String tabname, String sort,
			String sortdir, String whereclause, String terminator) {
		Connection conn = null;
		Statement stmt = null;
		String outp = "";
		try {
			String DB_URL = "jdbc:mysql://" + host + "/" + dbname;
			Class.forName(JDBC);
			// Für die Verbindung auf die Datenbank und zum einlesen der
			// Statements
			conn = DriverManager.getConnection(DB_URL, uname, pwd);
			stmt = conn.createStatement();
			// Hier wird der Befehl langsam zusammengesetzt und beginnt mit der
			// variable sql
			// die ein Select beinhaltet.
			// Die Daten werden aus den Variablen die oben in die Methode
			// gesetzt wurden
			// ausgelesen und eingesetzt um den Selectbefehl zu
			// vervollständigen.
			String sql = "SELECT ";
			for (int i = 0; i < fields.length; i++) {
				if (i != fields.length - 1) {
					sql += fields[i] + ", ";
				} else {
					sql += fields[i] + " ";
				}
			}
			sql += "From " + tabname;

			if (!whereclause.equals("")) {
				sql += " Where " + whereclause;
			}

			if (!sort.equals("")) {
				sql += " group by " + sort;
				if (!sortdir.equals("")) {
					sql += " " + sortdir;
				}
			}

			
			

			// Metadaten besorgen
			// java.sql.ResultSetMetaData md = rs.getMetaData();
			/*
			 * Resultset für PrimaryKeys ResultSet
			 * ps=metadata.getPrimaryKeys(null,null,tabname);
			 */
			;
			/*
			 * Spaltennamen ausgeben for( int i = 1; i <= md.getColumnCount();
			 * i++ ){ outp += md.getColumnLabel(i) + " " ; outp +="\n"; }
			 */

			/*
			 * Primary Key Ausgabe while(ps.next()){ outp += "Primarykey: " +
			 * ps.getString("COLUMN_NAME"); outp +="\n"; } while (rs.next()) {
			 * for (int i = 1; i <= md.getColumnCount(); i++) { outp +=
			 * rs.getString(i) + " " + terminator + " "; } outp += "\n"; }
			 */
			
			
			
			ResultSet rs = stmt.executeQuery(sql);

			DatabaseMetaData metadata = conn.getMetaData();
			
			
			// ArrayList für alle Tabellen
			ArrayList<String> tables = new ArrayList<String>();
			// ArrayList für alle ForeignKeys deren Tabelle
			ArrayList<String> fkTable = new ArrayList<String>();
			// ArrayList für alle ForeignKeys, deren Column
			ArrayList<String> fkColumn = new ArrayList<String>();
			// ArrayList für alle PrimaryKeys
			ArrayList<String> pk = new ArrayList<String>();
			// ArrayList für alle Columns
			ArrayList<String> columnList = new ArrayList<String>();
			
			
			
			
			
			
			// Besorgung aller Tabellen

			String[] types = { "TABLE" };

			ResultSet rsTables = metadata.getTables(null, null, dbname, types);
			
			while (rsTables.next()) {
				tables.add(rsTables.getString("TABLE_NAME"));
			}
			
			

			// Besorgung der ForeignKeys
			for (int i = 0; i < tables.size(); i++) {
				ResultSet fkrs = metadata.getExportedKeys(conn.getCatalog(),
						null, tables.get(i));
				fkTable.add(fkrs.getString("FKTABLE_NAME"));
				fkColumn.add(fkrs.getString("FKCOLUMN_NAME"));
			}
			
			

			// Besorgung der PrimaryKeys
			for (int i = 0; i < tables.size(); i++) {
				ResultSet pkrs = metadata.getPrimaryKeys(null, null,
						tables.get(i));
				while (pkrs.next()) {
					pk.add(pkrs.getString("COLUMN_NAME"));
				}

			}
			
			
			
			
			//Besorgung aller Columns
			for(int i=0;i<tables.size();i++){
				ResultSet crs=metadata.getColumns(null,null,tables.get(i),null);
				while(crs.next()){
					columnList.add(crs.getString("COLUMN_NAME"));
				}
			}
			
	////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////7
			
			
						
						
				ArrayList<ArrayList<String>> superOrderedTable=new ArrayList<ArrayList<String>>();
				
						
				String[] types1 = { "TABLE" };

				ResultSet rsTables1 = metadata.getTables(null, null, dbname, types1);
				
				
					
				while (rsTables1.next()) {
	
					
					ArrayList<String> singleTable= new ArrayList<String>();
					String tablename="Tablename: " + rsTables1.getString("TABLE_NAME");
					singleTable.add(tablename);
					ResultSet pkrs1 = metadata.getPrimaryKeys(null, null,
							singleTable.get(0));
					while (pkrs1.next()) {
						String primarykey="PK: " + pkrs1.getString("COLUMN_NAME");
						singleTable.add(primarykey);
					}
					
						ResultSet fkrs1 = metadata.getExportedKeys(conn.getCatalog(),
								null, singleTable.get(0));
						String foreignkeyTable="FK TABLE" + fkrs1.getString("FKTABLE_NAME");
						String foreignkeyColumn="FK COLUMN" + fkrs1.getString("FKTABLE_NAME");
						singleTable.add(foreignkeyTable);
						singleTable.add(foreignkeyColumn);
						
						
						ResultSet crs1=metadata.getColumns(null,null,singleTable.get(0),null);
						while(crs1.next()){
							String columnname="Columnname: " + crs1.getString("COLUMN_NAME");
							singleTable.add(columnname);
						}
						
					
					superOrderedTable.add(singleTable);	
					}
			
						
						
						
						
						
						
						
						
						
			
////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////7		
			
			
			
			
			
			
			
			
			
			
			rs.close();
			stmt.close();
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
}