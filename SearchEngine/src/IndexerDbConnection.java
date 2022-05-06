import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class IndexerDbConnection {
	
		static Statement statement;
		static Connection conn;
		    
		static public void DatabaseConnect(){
		
			String url = "jdbc:sqlserver://DESKTOP-4T81S99;databaseName=SearchEngine;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";
		     try {
		    	//Loading the required JDBC Driver class
		 		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");	
		 		
		 		//Creating a connection to the database
		 		Connection conn = DriverManager.getConnection(url);
		 		//System.out.println(conn);
		 		//Executing SQL query and fetching the result
		 		Statement st = conn.createStatement();
		 		String sqlStr = "select * from Link";
		 		ResultSet rs = st.executeQuery(sqlStr);
		 		while (rs.next()) {
		 			System.out.println(rs.getString(2));
		 		}		
		 	}
		     catch (Exception e) {
		         System.out.print("Database Not Connected"+e);
		     }
		  }
		
		public static void main(String args[]) throws Exception {
			IndexerDbConnection id = new IndexerDbConnection();
			id.DatabaseConnect();
			return;
		}
}
