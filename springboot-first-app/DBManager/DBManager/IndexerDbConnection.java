package DBManager;
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
		 		
		 	}
		     catch (Exception e) {
		         System.out.print("Database Not Connected"+e);
		     }
		  }


	    //Reset beginIndexing to 0 for all the words that their urls have started indexing but are not finished yet(endIndexing=0). 
	    static public void ResetAlreadyStarted() throws SQLException {  //nondone
			DatabaseConnect();
	    	String foundLinks="";
			PreparedStatement prepStatement= conn.prepareStatement("SELECT url FROM Link WHERE beginIndexing =1 and endIndexing=0 ", Statement.RETURN_GENERATED_KEYS);
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
			DatabaseConnect();
			PreparedStatement prepStatement= conn.prepareStatement( "UPDATE Link set endIndexing=0,beginIndexing=0", Statement.RETURN_GENERATED_KEYS );
			prepStatement.executeUpdate();
		}

		//Dropping the IndexingWords Table.
		static public void deleteIndexingWordsTable() throws SQLException {
			DatabaseConnect();
			PreparedStatement prepStatement= conn.prepareStatement("DELETE FROM IndexingWords", Statement.RETURN_GENERATED_KEYS );
			prepStatement.executeQuery();
			
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

		//Increment the count for h1,h2,h3,h4,h5,h6,italic,bold,p.
		static public void IncrementCounts(String url, String word,String degree) throws SQLException {
			String foundCount=null;
			PreparedStatement prepStatement= conn.prepareStatement("SELECT "+degree+" FROM IndexingWords WHERE word=? and url=?", Statement.RETURN_GENERATED_KEYS );

			prepStatement.setString(1, word);
			prepStatement.setString(2, url);
			ResultSet result = prepStatement.executeQuery();
			if (result.next()) 
			{
				foundCount= result.getString(1);
		    }
			if(foundCount!=null)
			{
				Integer newCount=Integer.parseInt(foundCount)+1;
				prepStatement= conn.prepareStatement("UPDATE IndexingWords set "+degree+"="+newCount.toString()+ " WHERE word=? and url=?", Statement.RETURN_GENERATED_KEYS );
				//prepStatement.setString(1, degree);
				prepStatement.setString(1, word);
				prepStatement.setString(2, url);
				prepStatement.executeUpdate();
			}
			else
			{
				Integer count = 1;
				prepStatement= conn.prepareStatement("UPDATE IndexingWords set "+degree+"="+count.toString()+" WHERE word=? and url=?", Statement.RETURN_GENERATED_KEYS );
				//prepStatement.setString(1, degree);
				prepStatement.setString(1, word);
				prepStatement.setString(2, url);
				prepStatement.executeUpdate();
				
			}
		}

		//Adding a new word to the IndexingWords table.
		static public void AddNewWord(String url, String word,String degree) throws SQLException {

			PreparedStatement prepStatement= conn.prepareStatement("INSERT INTO IndexingWords(word, url, "+degree+") VALUES (?,?,1)", Statement.RETURN_GENERATED_KEYS );
			//prepStatement.setString(1, degree);
			prepStatement.setString(1,word);
			prepStatement.setString(2,url);
			prepStatement.executeQuery();
		}
		
		//Sets the number of threads entered by the user in the crawler process to be used in the indexer process
		static public void setNumberOfThreads(int id, int num)
		{
			DatabaseConnect();
			PreparedStatement prepStatement = null;
			try {
				prepStatement = conn.prepareStatement("INSERT INTO CountThreads(id, numOfThreads) VALUES (0,?)", Statement.RETURN_GENERATED_KEYS );
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            try {
				prepStatement.setString(1,Integer.toString(num));
				prepStatement.executeQuery();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
		}
		
		//deleting the number of threads entry.
        static public void deleteNumThreads() throws SQLException {
        	DatabaseConnect();
            PreparedStatement prepStatement= conn.prepareStatement( "DELETE FROM CountThreads WHERE id=0", Statement.RETURN_GENERATED_KEYS );
            prepStatement.executeQuery();
        }

        //getting the number of threads.
        static public int getNumThreads() throws SQLException {
        	DatabaseConnect();
            int numThreads=0;
            PreparedStatement prepStatement= conn.prepareStatement( "SELECT numOfThreads FROM CountThreads WHERE id=0", Statement.RETURN_GENERATED_KEYS );
            ResultSet result= prepStatement.executeQuery();
            if (result.next()) 
            {
                numThreads= result.getInt(1);
            }
            return numThreads;
        }

      //returns unindexed urls 
        static public ArrayList<String> returnUnIndexedUrls() throws SQLException {
        ArrayList<String> unindexed = new ArrayList<String>();
        PreparedStatement prepStatement= conn.prepareStatement("SELECT url FROM Link WHERE beginIndexing=0", Statement.RETURN_GENERATED_KEYS);
        ResultSet result = prepStatement.executeQuery();
        while(result.next())
        {
            unindexed.add(result.getString(1));
        }
        return unindexed;
    }
        static public boolean isExistWord(String url, String word) throws SQLException
        {
	        PreparedStatement prepStatement= conn.prepareStatement("SELECT count(*) FROM IndexingWords WHERE url=? and word=?");
            prepStatement.setString(1, url);
            prepStatement.setString(2, word);
            ResultSet result=prepStatement.executeQuery();
            int count;
            if ( result.next() ) 
            {
              count = result.getInt(1);
              if(count>0)
              {
                  return true;
              }
            
              else
              {
                  return false;
              }
            }
            return false;
	            
          }
        
      //removing thread from threads table
        static public void removeThread(String link,Integer threadId) throws SQLException {
            PreparedStatement prepStatement= conn.prepareStatement( "DELETE FROM Threads WHERE url=? and threadId=?", Statement.RETURN_GENERATED_KEYS );
            prepStatement.setString(1, link);
            prepStatement.setString(2,threadId.toString());
            prepStatement.executeQuery();
        }
        //adding thread to threads table
        static public void addThread (String link,Integer threadId) throws SQLException {
            PreparedStatement prepStatement= conn.prepareStatement( "INSERT INTO Threads(url,threadId) VALUES(?,?)", Statement.RETURN_GENERATED_KEYS);
            prepStatement.setString(1,link);
            prepStatement.setString(2,threadId.toString());
            prepStatement.executeQuery();
        }
        
      //Updating the words count attribute in Link table.
        static public void updateWordCount (String link,Integer count) throws SQLException {
        	PreparedStatement prepStatement= conn.prepareStatement("UPDATE Link set countWords =? WHERE url=?", Statement.RETURN_GENERATED_KEYS);
            prepStatement.setString(1,count.toString());
            prepStatement.setString(2,link);
            prepStatement.executeUpdate();

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
