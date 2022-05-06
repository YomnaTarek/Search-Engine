import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
			prepStatement.executeQuery();
			
		}

		//Increment the count for h1,h2,h3,h4,h5,h6,italic,bold,p.
		static public void IncrementCounts(String word,String degree) throws SQLException {
			String foundCount=null;
			PreparedStatement prepStatement= conn.prepareStatement("SELECT ? FROM IndexingWords WHERE word=?", Statement.RETURN_GENERATED_KEYS );
			prepStatement.setString(1, degree);
			prepStatement.setString(2, word);
			ResultSet result = prepStatement.executeQuery();
			if (result.next()) 
			{
				foundCount= result.getString(1);
		    }
			if(foundCount!=null)
			{
				Integer newCount=Integer.parseInt(foundCount)+1;
				prepStatement= conn.prepareStatement("UPDATE IndexingWords set ?="+newCount.toString()+ " WHERE word=?", Statement.RETURN_GENERATED_KEYS );
				prepStatement.setString(1, degree);
				prepStatement.setString(2, word);
				prepStatement.executeUpdate();
			}
		}

	    public static void main(String args[]) throws Exception {
			IndexerDbConnection id = new IndexerDbConnection();
			id.DatabaseConnect();
			return;
	    }
}
