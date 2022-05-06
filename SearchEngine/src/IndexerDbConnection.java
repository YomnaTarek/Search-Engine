import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


import java.sql.ResultSetMetaData;

import java.sql.SQLException;
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
		 		conn = DriverManager.getConnection(url);
		 		//System.out.println(conn);
		 		//Executing SQL query and fetching the result
		 		statement = conn.createStatement();
		 		String sqlStr = "select * from Link";
		 		ResultSet rs = statement.executeQuery(sqlStr);
		 		while (rs.next()) {
		 			System.out.println(rs.getString(2));
		 		}		
		 	}
		     catch (Exception e) {
		         System.out.print("Database Not Connected"+e);
		     }
		  }


	    //Reset beginIndexing to 0 for all the words that their urls have started indexing but are not finished yet(endIndexing=0). 
	    static public void ResetAlreadyStarted(Integer numThreads) throws SQLException {  //nondone
			String foundLinks="";
			PreparedStatement prepStatement= conn.prepareStatement("SELECT url FROM Link WHERE beginIndexing =1 and endIndexing=0 limit "+numThreads.toString(), Statement.RETURN_GENERATED_KEYS);
			ResultSet result=prepStatement.executeQuery();
			if(result.next())
			{
				foundLinks=result.getString(1);
				if(foundLinks==null)
				    foundLinks="";
			}
			if(foundLinks.length()!=0)
			{
			    prepStatement= conn.prepareStatement("UPDATE Link set beginIndexing=0 WHERE beginIndexing=1 and endIndexing=0", Statement.RETURN_GENERATED_KEYS);
                prepStatement.executeUpdate();
			}
		}

		//Finding an unindexed url and returning it.
		  static public String getUnIndexedUrl() throws SQLException {
			String  unindexed=null;
			PreparedStatement prepStatement= conn.prepareStatement("SELECT url FROM Link WHERE endIndexing =0 and beginIndexing=0 limit 1", Statement.RETURN_GENERATED_KEYS);
			ResultSet result = prepStatement.executeQuery();
			if (result.next()) 
			{
				unindexed= result.getString(1);
		    }
			return unindexed;
		}
        //Start indexing a url by setting beginIndexing.
		static public void BeginIndexing(String urlStarted)  throws SQLException {
			PreparedStatement prepStatement = conn.prepareStatement("UPDATE Link set beginIndexing=1 WHERE url=?", Statement.RETURN_GENERATED_KEYS );
			prepStatement.setString(1, urlStarted);
			prepStatement.executeUpdate();
		}
        //Start indexing a url by setting endIndexing.
		static public void EndIndexing(String urlDone) throws SQLException {
			PreparedStatement prepStatement = conn.prepareStatement("UPDATE Link set endIndexing=1 WHERE url=?", Statement.RETURN_GENERATED_KEYS );
			prepStatement.setString(1, urlDone);
			prepStatement.executeUpdate();
		}

		//Reseting the values of the attributes endIndexing and beginIndexing in the Link table for all the urls.
		static public void zeroingEndAndBegin() throws SQLException {
			PreparedStatement prepStatement= conn.prepareStatement( "UPDATE Link set endIndexing=0,beginIndexing=0", Statement.RETURN_GENERATED_KEYS );
			prepStatement.executeUpdate();
		}

		//Dropping the IndexingWords Table.
		static public void deleteIndexingWordsTable() throws SQLException {
			PreparedStatement prepStatement= conn.prepareStatement("DELETE FROM IndexingWords", Statement.RETURN_GENERATED_KEYS );
			prepStatement.executeUpdate();
			
		}
		
		//Gets the tags as a list from database
		static public void getWordDegrees(ArrayList<String> degrees)
		{
			try {
			ResultSet rs = statement.executeQuery("SELECT * FROM IndexingWords");
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			for (int i = 4; i <= columnCount; i++ ) {
			  String name = rsmd.getColumnName(i);
			  degrees.add(name);
			}
			}
			catch(Exception e)
			{				
				System.out.print("couldnt fetch"+e);	
			}
		
		}
	    public static void main(String args[]) throws Exception {
			
			IndexerDbConnection id = new IndexerDbConnection();
			id.DatabaseConnect();
			ArrayList<String> degrees = new ArrayList<String>();
			id.getWordDegrees(degrees);
			System.out.println(degrees);
			
			return;
	    }
}
