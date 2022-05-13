package Indexer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import DBManager.IndexerDbConnection;

public class Indexer {
	
	public static ArrayList<String> degrees;
	public static ArrayList<String> stopwords;
	public static int countOfThreads;
	public static ArrayList<Thread> indexerThreads;
	public static Integer key = 0; 
	public static ArrayList<String> linksList;
	public static int shareRestOfThreads;
	public static int shareFirstThread;

	public Indexer() throws InterruptedException, SQLException 
	{
		getStopWords("stopwords.txt", stopwords);
		try {
			countOfThreads = IndexerDbConnection.getNumThreads();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	//This function fetches the list of stopwords from the stopword.txt
	public void getStopWords(String filename, ArrayList<String>stopWordsList)
	 {
		 try {
		      File myObj = new File(filename);
		      Scanner myReader = new Scanner(myObj);
		      while (myReader.hasNextLine()) {
		        String data = myReader.nextLine();
		        stopWordsList.add(data);
		        
		      }
		      myReader.close();
		    } catch (FileNotFoundException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
	 }	


	 private static class Index extends Thread{
		 Integer threadId;
		 Integer key; //For synchronization purpose
		 ArrayList<String> documents;
		 Integer start;
		 Integer size;
		 public Index(int id, int k, ArrayList<String> docs, int start, int size)
		 {
			 this.threadId = id;
			 this.key = k;
			 this.documents = docs;
			 this.start = start;
			 this.size = size;
			 
		 }
		 
		 public void run()
		 {
			String link = null;
			while(true)
			{
				synchronized(this.key) {
					try 
					{
						//Get the first unindexed link from the database
						link=IndexerDbConnection.getUnIndexedUrl();	
					} 
					catch (SQLException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//If link == null then we break the outer most while loop
					if (link == null)
					{
						break;
					}
					else 
					{
						try 
						{
							//If the link is not null then we start the indexing process for this link
							IndexerDbConnection.BeginIndexing(link.toString());
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
			}
			//We use the jsoup library to get the html content of this link we fetched from the database
			Document document = null;
			try 
			{
				document = Jsoup.connect(link).timeout(0).get();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(document != null)
			{
				ArrayList<String> stemmedWords = new ArrayList<String>();
				Elements htmlDoc = document.select("h1, h2, h3, h4, h5, h6, p, title, i, b"); //Here we divide the document according to the tags/degrees
				IndexerDbConnection.getWordDegrees(degrees);//This query gets us a list of possible degrees/tags (h1,h2,h3,..)
				//We then loop on each degree and call parseText function which gets all the text content for a certain degree, splits the text into words,
				//then stems each word,adds it to the stemmedWords array list, then adds it to the database (if not inserted before) or increments its count in the corresponding
				//degree column (if inserted before in db)
				for(int i = 0 ;i<degrees.size();i++)
				{
					parseText(htmlDoc, degrees.get(i),stemmedWords,stopwords);
				}
			}
		 
		 }
		 }
			
			 //This function checks if a string is number or not
			 static boolean isNumber(String s)
		    {
		        for (int i = 0; i < s.length(); i++)
		            if (Character.isDigit(s.charAt(i)) == false)
		                return false;
		 
		        return true;
		    }
			 
			public static void parseText(Elements textDoc, String degree, ArrayList<String> stemmedWords,ArrayList<String> stopwords )
			 {
				 Elements selectedWords = textDoc.select(degree); //We select from the document the text based on whether the degree passed is h1, h2, h3, ..
				 String extractedText =  selectedWords.text(); //We extract the text content
				 String[] splittedText = null;
				 if(extractedText.length() != 0)
				 {
					 splittedText = extractedText.split("[^a-zA-Z0-9']"); //We split the text into words
				 }
				
				 if(splittedText.length != 0)
				 {
					 ArrayList<String> stemming = new ArrayList<String>(); //This array list will hold the stemmed words
					 Stemmer s = new Stemmer();
					 for (int i=0 ;i<splittedText.length;i++)
					 {
						 splittedText[i] = splittedText[i].toLowerCase(); 
						 if(!stopwords.contains(splittedText[i])) //Check if the current word is a stopword or not
						 {
							 if(isNumber(splittedText[i]) && splittedText[i].length() == 4) //Check if the string is a year for example
							 {
								 stemmedWords.add(splittedText[i]);
								 //call db function to add the stem to db
								 try 
								 {
									IndexerDbConnection.AddNewWord(splittedText[i], degree);
								 } 
								 catch (SQLException e) 
								 {
									// TODO Auto-generated catch block
									e.printStackTrace();
								 }
								 continue;
							 }
							 if(!splittedText[i].isBlank() && !splittedText[i].isEmpty() && !isNumber(splittedText[i])) //Check if the splittedText is not blank or empty or a number 
							 {
								 System.out.println(splittedText[i]);
								 String stem = s.StemWord(splittedText[i]); //Stem the splittedText
								 if(!stemmedWords.contains(stem)) //Check if it has been adde to the stemmedWords before
								 { 
									 stemmedWords.add(stem);  //Add the stem to the stemmedWords
									//Call db function to add the stem to db
									 try 
									 {
										IndexerDbConnection.AddNewWord(stem, degree);
									 } 
									 catch (SQLException e) 
									 {
										// TODO Auto-generated catch block
										e.printStackTrace();
									 }
								 }
								 else
								 {
									//Increment occurrence of the stem
									 try 
									 {
										IndexerDbConnection.IncrementCounts(stem, degree);
									 } 
									 catch (SQLException e) 
									 {
										// TODO Auto-generated catch block
										e.printStackTrace();
									 }
									 
								 }
							 }
						 }
					 }
					 
				 }	 
			 }
}
	 public static void main(String args[]) throws Exception {
			Indexer indexer = new Indexer();
			
			//Get the share for each thread
			if(linksList.size() % countOfThreads == 0)
			{
				shareRestOfThreads = linksList.size()/countOfThreads;
				shareFirstThread = shareRestOfThreads;
			}
			else
			{
				shareRestOfThreads=(linksList.size()/countOfThreads)*(countOfThreads-1); //share for each of the threads other than first thread.
				shareFirstThread=linksList.size()-shareRestOfThreads; //share of first thread
			}
			//Create the threads and assign each one their share
			for (int i = 0 ;i<countOfThreads;i++)
			{
				if(i == 0)
				{
					int threadId=i;
					Thread indexer=new Index(threadId,key, linksList, i*shareFirstThread, shareFirstThread);
					indexer.setName("Thread "+ i);
					indexerThreads.add(indexer);
					indexer.start();
				}
				else
				{
					int threadId=i;
					Thread indexer=new Index(threadId,key, linksList, i*shareRestOfThreads,shareRestOfThreads);
					indexer.setName("Thread "+ i);
					indexerThreads.add(indexer);
					indexer.start();
				}
			}	
			linksList = IndexerDbConnection.returnUnIndexedUrls();
			
			for(int i = 0; i<indexerThreads.size();i++)
			{
				indexerThreads.get(i).join();
			}
			
			
			
		}
}