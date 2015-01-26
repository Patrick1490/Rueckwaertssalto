
import java.sql.DatabaseMetaData;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Die Klasse Exporter bastelt mit den eingegebenen Befehlen einen Select befehl
 * zusammen und dieser wird dann aus der Datenbank die auch angegeben werden
 * muss ausgelesen.
 * 
 * @author Patrick Wichert
 * @version 10.01 2015
 * 
 */

public class Exporter 
{
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
	public static String execute(String host, String uname, String pwd, String dbname, String[] fields, String tabname, String sort,
			String sortdir, String whereclause, String terminator) 
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
			//Hier wird der Befehl langsam zusammengesetzt und beginnt mit der
			// variable sql
			// die ein Select beinhaltet.
			// Die Daten werden aus den Variablen die oben in die Methode
			// gesetzt wurden
			// ausgelesen und eingesetzt um den Selectbefehl zu
			// vervollständigen.
			String sql = "SELECT ";
			for (int i = 0; i < fields.length; i++) 
			{
				if (i != fields.length - 1) 
				{
					sql += fields[i] + ", ";
				} else {
					sql += fields[i] + " ";
				}
			}
			sql += "From " + tabname;

			if (!whereclause.equals("")) 
			{
				sql += " Where " + whereclause;
			}

			if (!sort.equals("")) 
			{
				sql += " group by " + sort;
				if (!sortdir.equals("")) 
				{
					sql += " " + sortdir;
				}
			}

			// Solange etwas im Resultset vorhanden ist wird die while
			// ausgeführt	
			// und in der for-Schleife werden alle Daten in der Zeile (wenn
			// notwendig) ausgelesen
			ResultSet rs = stmt.executeQuery(sql);
			
			DatabaseMetaData metadata=conn.getMetaData();
			//Metadaten besorgen
			java.sql.ResultSetMetaData md = rs.getMetaData();
			//Resultset für PrimaryKeys
			ResultSet ps=metadata.getPrimaryKeys(null,null,tabname);
			// Spaltennamen ausgeben
		      for( int i = 1; i <= md.getColumnCount(); i++ ){
		         outp += md.getColumnLabel(i) + " "  ;
		      		outp +="\n";
		      }
			//Primary Key Ausgabe
		      while(ps.next()){
		      outp += "Primarykey: " + ps.getString("COLUMN_NAME");
		      outp +="\n";
		      }
		      while (rs.next()) 
			{
				for (int i = 1; i <= md.getColumnCount(); i++) 
				{
					outp += rs.getString(i) + " " + terminator + " ";
				}
				outp += "\n";
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException se) 
		{
			se.printStackTrace();
		} catch (Exception e) 
		{
			e.printStackTrace();
		} finally 
		{
			try 
			{
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) 
			{
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) 
			{
				se.printStackTrace();
			}
		}
		return outp;
	}
}