import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;


//class to handle connection with database and all its related functions
public class DBManager {

  static Statement state;
  static Connection conn;

  //function to start connection with the database on the remote server
		static public void DatabaseConnect(){
		
			//static string that contains DB info so we can connect
			String url = "jdbc:sqlserver://WINDOWS-52CFOSV\\SQLEXPRESS;databaseName=SearchEngine;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";
		     try {
		    	//Loading the required JDBC Driver class
		 		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");	
		 		
		 		//Creating a connection to the database
		 		Connection conn = DriverManager.getConnection(url);
        		 System.out.print("Database connected..."+"\n" );

		 		//Executing temporary SQL query and fetching the result
		 		Statement st = conn.createStatement();
		 		String sqlStr = "select * from Link";
		 		ResultSet rs = st.executeQuery(sqlStr);
		 		while (rs.next()) {
		 			System.out.println(rs.getString(2));
		 		}		
       
		 	}
		     catch (Exception e) {
		         System.out.print("Failed to connect database: "+e+"\n" );
		     }
		  }
		public static void main(String args[]) throws Exception {
			
			DBManager.DatabaseConnect();
			return;
		}

}