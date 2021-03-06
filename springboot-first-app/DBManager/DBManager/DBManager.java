package DBManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

//class to handle connection with database and all its related functions
public class DBManager {

  static Statement st;
  static Connection conn;

  //function to start connection with the database on the remote server
		static public void DatabaseConnect(){
		
			//static string that contains DB info so we can connect
			//String url = "jdbc:sqlserver://WINDOWS-52CFOSV\\SQLEXPRESS;databaseName=SearchEngine;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";
			String url = "jdbc:sqlserver://DESKTOP-4T81S99;databaseName=SearchEngine;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";
		     
			try {
		    	//Loading the required JDBC Driver class
		 		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");	
		 		
		 		//Creating a connection to the database
		 		Connection conn = DriverManager.getConnection(url);
				 st = conn.createStatement();
        		 //System.out.print("Database connected..."+"\n" );	
		 	}
		     catch (Exception e) {
		         System.out.print("Failed to connect database: "+e+"\n" );
		     }
			
		  }

		  //function to get the number of urls inside the DB
		public static int getURLCount() throws SQLException
		  {	
			//System.out.print(" getURLCount() called"+"\n" );	
			  String sqlStr = "select count(*) from Link";
			  DatabaseConnect();
			  ResultSet rs = st.executeQuery(sqlStr);
			  int count=0;
			  if ( rs.next())
			   {
					 count = rs.getInt(1);
					 return count;
				}
			  return 0;
		  }

		  
		public static List<URL> getAllURLs() throws SQLException , MalformedURLException
		{
			// System.out.print("getAllURls() called"+"\n" );
            ArrayList<URL> urls=new ArrayList<>();
			ArrayList<String>u=new ArrayList<>();
			String sqlStr = "select * from Link";
			DatabaseConnect();
			ResultSet rs = st.executeQuery(sqlStr);
			
			while(rs.next()) {
				u.add( rs.getString("url"));	
			}
			
			for(int j=0; j< u.size(); j++)
			{
				urls.add(new URL(u.get(j)));
			}
			return urls;
		}
		
		public static List<URL> getInbounds(String url) throws SQLException , MalformedURLException
		{
			// System.out.print("getInbounds() called"+"\n" );
            ArrayList<URL> urls=new ArrayList<>();
			ArrayList<String>u=new ArrayList<>();
			String sqlStr = "select * from DiscoveredLinks where associatedUrl ='"+url+"'";
			DatabaseConnect();
			ResultSet rs = st.executeQuery(sqlStr);
			
			while(rs.next()) {
				u.add( rs.getString("url"));	
			}
			
			for(int j=0; j< u.size(); j++)
			{
				urls.add(new URL(u.get(j)));
			}
			return urls;
		}
		

		  public static boolean isExistentURL(String url) throws SQLException
		  {
			// System.out.print("isExistentURL() called"+"\n" );
		
		String sqlStr = "select count(*) from Link where url='"+url+"'";
		DatabaseConnect();
		ResultSet rs = st.executeQuery(sqlStr);
			int count=0;
			if ( rs.next() ) {
	    	    count = rs.getInt(1);
	    	    if(count>0)
	    	    	return true;
	    	    else 
	    	    	return false;
	     }
	     return false;
		  }
		  public static int getCountOutbounds(String url) throws SQLException
		  {
			// System.out.print("getCountOutbounds() called"+"\n" );
		
			String sqlStr = "select outbound from Link where url='"+url+"'";
			DatabaseConnect();
			ResultSet rs = st.executeQuery(sqlStr);
			  int count=0;
			  if ( rs.next())
			   {
					 count = rs.getInt(1);
					 return count;
				}
			  return 0;
		  }

		  public static int getCountWordsForURL(String url) throws SQLException
		  {
			// System.out.print("getCountOutbounds() called"+"\n" );
		
			String sqlStr = "select countWords from Link where url='"+url+"'";
			DatabaseConnect();
			ResultSet rs = st.executeQuery(sqlStr);
			  int count=0;
			  if ( rs.next())
			   {
					 count = rs.getInt(1);
					 return count;
				}
			  return 0;
		  }

		  //getting the number of hits in our urls for a specific
		  public static int getCountURLsForWord(String word) throws SQLException
		  {
			// System.out.print("getCountOutbounds() called"+"\n" );
		
			String sqlStr = "select count(*) from IndexingWords where word='"+word+"'";
			DatabaseConnect();
			ResultSet rs = st.executeQuery(sqlStr);
			  int count=0;
			  if ( rs.next())
			   {
					 count = rs.getInt(1);
					 return count;
				}
			  return 0;
		  }

		  public static int getCountTitle(String word,String url) throws SQLException
		  {
			// System.out.print("getCountTitle() called"+"\n" );
		
			String sqlStr = "select title from IndexingWords where word='"+word+"'and url='"+url+"'";
			DatabaseConnect();
			ResultSet rs = st.executeQuery(sqlStr);
			  int count=0;
			  if ( rs.next())
			   {
					 count = rs.getInt(1);
					 return count;
				}
			  return 0;
		  }
		  public static int getCountH1(String word,String url) throws SQLException
		  {
			// System.out.print("getCountTitle() called"+"\n" );
		
			String sqlStr = "select h1 from IndexingWords where word='"+word+"'and url='"+url+"'";
			DatabaseConnect();
			ResultSet rs = st.executeQuery(sqlStr);
			  int count=0;
			  if ( rs.next())
			   {
					 count = rs.getInt(1);
					 return count;
				}
			  return 0;
		  }
		  public static int getCountH2(String word,String url) throws SQLException
		  {
			// System.out.print("getCountTitle() called"+"\n" );
		
			String sqlStr = "select h2 from IndexingWords where word='"+word+"'and url='"+url+"'";
			DatabaseConnect();
			ResultSet rs = st.executeQuery(sqlStr);
			  int count=0;
			  if ( rs.next())
			   {
					 count = rs.getInt(1);
					 return count;
				}
			  return 0;
		  }
		  public static int getCountH3(String word,String url) throws SQLException
		  {
			// System.out.print("getCountTitle() called"+"\n" );
		
			String sqlStr = "select h3 from IndexingWords where word='"+word+"'and url='"+url+"'";
			DatabaseConnect();
			ResultSet rs = st.executeQuery(sqlStr);
			  int count=0;
			  if ( rs.next())
			   {
					 count = rs.getInt(1);
					 return count;
				}
			  return 0;
		  }
		  public static int getCountH4(String word,String url) throws SQLException
		  {
			// System.out.print("getCountTitle() called"+"\n" );
		
			String sqlStr = "select h4 from IndexingWords where word='"+word+"'and url='"+url+"'";
			DatabaseConnect();
			ResultSet rs = st.executeQuery(sqlStr);
			  int count=0;
			  if ( rs.next())
			   {
					 count = rs.getInt(1);
					 return count;
				}
			  return 0;
		  }
		  public static int getCountH5(String word,String url) throws SQLException
		  {
			// System.out.print("getCountTitle() called"+"\n" );
		
			String sqlStr = "select h5 from IndexingWords where word='"+word+"'and url='"+url+"'";
			DatabaseConnect();
			ResultSet rs = st.executeQuery(sqlStr);
			  int count=0;
			  if ( rs.next())
			   {
					 count = rs.getInt(1);
					 return count;
				}
			  return 0;
		  }
		  public static int getCountH6(String word,String url) throws SQLException
		  {
			// System.out.print("getCountTitle() called"+"\n" );
		
			String sqlStr = "select h6 from IndexingWords where word='"+word+"'and url='"+url+"'";
			DatabaseConnect();
			ResultSet rs = st.executeQuery(sqlStr);
			  int count=0;
			  if ( rs.next())
			   {
					 count = rs.getInt(1);
					 return count;
				}
			  return 0;
		  }
		  public static int getCountP(String word,String url) throws SQLException
		  {
			// System.out.print("getCountTitle() called"+"\n" );
		
			String sqlStr = "select p from IndexingWords where word='"+word+"'and url='"+url+"'";
			DatabaseConnect();
			ResultSet rs = st.executeQuery(sqlStr);
			  int count=0;
			  if ( rs.next())
			   {
					 count = rs.getInt(1);
					 return count;
				}
			  return 0;
		  }
		  public static int getCountBold(String word,String url) throws SQLException
		  {
			// System.out.print("getCountTitle() called"+"\n" );
		
			String sqlStr = "select b from IndexingWords where word='"+word+"'and url='"+url+"'";
			DatabaseConnect();
			ResultSet rs = st.executeQuery(sqlStr);
			  int count=0;
			  if ( rs.next())
			   {
					 count = rs.getInt(1);
					 return count;
				}
			  return 0;
		  }
		  public static int getCountItalic(String word,String url) throws SQLException
		  {
			// System.out.print("getCountTitle() called"+"\n" );
		
			String sqlStr = "select i from IndexingWords where word='"+word+"'and url='"+url+"'";
			DatabaseConnect();
			ResultSet rs = st.executeQuery(sqlStr);
			  int count=0;
			  if ( rs.next())
			   {
					 count = rs.getInt(1);
					 return count;
				}
			  return 0;
		  }



		  public static void addURL(String url, int index) throws SQLException
		  {
			// System.out.print(" addURL() called"+"\n" );
			  index++;
            String sqlStr = "INSERT INTO Link VALUES ('"+index+"','"+url+"',1,0,0,0,0,0,0,0)";
			DatabaseConnect();
			st.executeUpdate(sqlStr);

		  }
		  public static void updateRank(String url, double rank) throws SQLException {
			// System.out.print("updateRank() called"+"\n" );
			String sqlStr="UPDATE Link set rank+='"+rank+"' WHERE url='"+url+"'";
			DatabaseConnect();
			st.executeUpdate(sqlStr);
			  
		  }
		  public static void addPopularity() throws SQLException {
			// System.out.print("addPopularity() called"+"\n" );
			String sqlStr="UPDATE Link set rank= popularity";
			DatabaseConnect();
			st.executeUpdate(sqlStr);
			  
		  }
		  public static void addPopularityForURL(String url) throws SQLException {
			// System.out.print("addPopularity() called"+"\n" );
			String sqlStr="UPDATE Link set rank= popularity  WHERE url='"+url+"'";
			DatabaseConnect();
			st.executeUpdate(sqlStr);
			  
		  }
		  public static void resetRank() throws SQLException {
			// System.out.print("resetRank() called"+"\n" );
			String sqlStr="UPDATE Link set rank= 0";
			DatabaseConnect();
			st.executeUpdate(sqlStr);
			  
		  }
		  public static void updatePopularity(String url, double rank) throws SQLException {
			// System.out.print("updatePopularity() called"+"\n" );
			String sqlStr="UPDATE Link set popularity='"+rank+"'WHERE url='"+url+"'";
			DatabaseConnect();
			st.executeUpdate(sqlStr);
			  
		  }
		  public static void incrementInBound(String url) throws SQLException {
			// System.out.print("incrementInbound() called"+"\n" );
			String sqlStr="UPDATE Link set inBound=inBound+1 WHERE url='"+url+"'";
			DatabaseConnect();
			st.executeUpdate(sqlStr);
			  
		  }
		  public static void incrementOutBound(String url)throws SQLException {
			// System.out.print("incrementOutbound() called"+"\n" );
			String sqlStr="UPDATE Link set outBound=outBound+1 WHERE url='"+url+"'";
			DatabaseConnect();
			st.executeUpdate(sqlStr);
		}
		public static void insertDiscoveredLink(int index,String url, String discovered)throws SQLException {
			// System.out.print("insertDiscoveredLink() called"+"\n" );
			index++;
			String sqlStr = "INSERT INTO DiscoveredLinks (id,url,associatedUrl,setActive) VALUES ('"+index+"','"+url+"','"+discovered+"',1)";
			DatabaseConnect();
			st.executeUpdate(sqlStr);
		}

		public static int getCountDiscovered() throws SQLException
		{
			// System.out.print("getCountDiscoveredLink() called"+"\n" );
			String sqlStr = "select count(*) from DiscoveredLinks";
			  DatabaseConnect();
			  ResultSet rs = st.executeQuery(sqlStr);
			  int count=0;
			  if ( rs.next())
			   {
					 count = rs.getInt(1);
					 return count;
				}
			  return 0;
		}

		public static boolean isExistentDiscovered(String url,String discovred) throws SQLException
		{
			// System.out.print("isExistentDiscovered() called"+"\n" );
	  String sqlStr = "select count(*) from DiscoveredLinks where url='"+url+"' and associatedUrl='"+discovred+"'";
	  DatabaseConnect();
	  ResultSet rs = st.executeQuery(sqlStr);
		  int count=0;
		  if ( rs.next() ) {
			  count = rs.getInt(1);
			  if(count>0)
				  return true;
			  else 
				  return false;
	   }
	   return false;
		}
		public static Boolean isParsed() {
			return true;
		}
		public static void setParsed(String url) throws SQLException {
			// System.out.print("setParsed() called"+"\n" );
			String sqlStr="UPDATE Link set parsed=1 WHERE url='"+url+"'";
			DatabaseConnect();
			st.executeUpdate(sqlStr);
		}

		public static List<URL> getUnparsedURLs() throws SQLException , MalformedURLException
		{
			// System.out.print("getUnparsedURls() called"+"\n" );
            ArrayList<URL> urls=new ArrayList<>();
			ArrayList<String>u=new ArrayList<>();
			String sqlStr = "select * from Link where parsed=0";
			DatabaseConnect();
			ResultSet rs = st.executeQuery(sqlStr);
			
			while(rs.next()) {
				u.add( rs.getString("url"));	
			}
			
			for(int j=0; j< u.size(); j++)
			{
				urls.add(new URL(u.get(j)));
			}
			return urls;
		}
		public static int getCountUnparsed() throws SQLException , MalformedURLException
		{
			// System.out.print("countUnparsedURls() called"+"\n" );
			String sqlStr = "select count(*) from Link where parsed=0";
			DatabaseConnect();
			ResultSet rs = st.executeQuery(sqlStr);
			  int count=0;
			  if ( rs.next())
			   {
					 count = rs.getInt(1);
					 return count;
				}
			  return 0;
		}
		public static int getCountThreadURL() throws SQLException
		{
			// System.out.print("countThreadURls() called"+"\n" );
			String sqlStr = "select count(*) from Threads";
			  DatabaseConnect();
			  ResultSet rs = st.executeQuery(sqlStr);
			  int count=0;
			  if ( rs.next())
			   {
					 count = rs.getInt(1);
					 return count;
				}
			  return 0;
		}
		public static void insertThreadURL(String url, int threadNum, int index) throws SQLException {
			// System.out.print("insertThread() called"+"\n" );
			index++;
			String sqlStr = "INSERT INTO Threads VALUES ('"+index+"','"+url+"','"+threadNum+"')";
			DatabaseConnect();
			st.executeUpdate(sqlStr);
			System.out.print(url+" inserted"+"\n" );	

		}
		public static void deleteThreadURL(int threadNum) throws SQLException {
			// System.out.print("deleteThread() called"+"\n" );
			String sqlStr = "DELETE FROM Threads  WHERE threadId='"+threadNum+"'";
			DatabaseConnect();
			st.executeUpdate(sqlStr);
		}
		
		public static ArrayList<String> sortResults(int limit, int pageNumber) throws SQLException
		{
			ArrayList<String> urls = new ArrayList<String>();
			int offset = (pageNumber*limit)-limit;
			String sqlStr = "SELECT * FROM Link ORDER BY rank DESC OFFSET "+offset+" ROWS FETCH FIRST "+limit+" ROWS ONLY";
			DatabaseConnect();
			ResultSet rs = st.executeQuery(sqlStr);
			while(rs.next()) {
				urls.add(rs.getString("url"));	
			}
			return urls;
		}
		public static void main(String args[]) throws Exception {
			updateRank("https://www.instagram.com/rottentomatoes/",1.0);
			return;
		}

}