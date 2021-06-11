package ALLINUSDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	Connection conn = null;
	public Connection getDbConnection () throws ClassNotFoundException, SQLException {
		if(conn == null) {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:allinus.db");
			//System.out.println("Opened database successfuly");
			return conn;
		}else {
			conn.close();
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:allinus.db");
			//System.out.println("Opened database successfuly");
			return conn;
		}
	}
}
