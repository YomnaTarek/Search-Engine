package Indexer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import DBManager.IndexerDbConnection;
import Ranker.Ranker;

public class Indexer {
	
	public static ArrayList<String> stopwords = new ArrayList<String>();
	public static int countOfThreads;
	public static ArrayList<Thread> indexerThreads = new ArrayList<Thread>();
	public static Integer key = 0; 
	public static ArrayList<String> linksList = new ArrayList<String>();
	public static int shareRestOfThreads;
	public static int shareFirstThread;
	public static ArrayList<String> degrees = new ArrayList<String>(Arrays.asList("h1","h2","h3","h4","h5","h6","i", "b", "p"));
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
		stopWordsList = new ArrayList<String>();
		 try {
		      File myObj = new File(filename);
		      Scanner myReader = new Scanner(myObj);
		      while (myReader.hasNextLine()) {
		        String data = myReader.nextLine();
		        stopWordsList.add(data);
		        
		      }
		      stopwords = stopWordsList;
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
		 Integer end;
		 public Index(int id, int k, ArrayList<String> docs, int start, int end)
		 {
			 this.threadId = id;
			 this.key = k;
			 this.documents = docs;
			 this.start = start;
			 this.end = end;
		 }
		 
		 public void run()
		 {
			 
			String link = null;
			for(int k = start ;k<end;k++)
			{
					Integer countWords = 0;
					System.out.println("Thread no. "+this.threadId+" parsing url" + this.documents.get(k));
					link = documents.get(k);
					//If link == null then we break the outer most while loop
					if (link == null)
					{
						break;
					}
					else 
					{
						//Check if the link is instagram or facebook or google,if yes,then indexing is not possible.
						String tempLink=link.toLowerCase();
						if(tempLink.contains("facebook") || tempLink.contains("google") || tempLink.contains("instagram") ||  tempLink.contains("youtube") || tempLink.contains("twitter"))
						{
							System.out.println("link: "+link+" is not indexable");
							//set begin and end indexing to 1.
			                                try {
				                             IndexerDbConnection.BeginIndexing(link.toString());
			                                     } catch (SQLException e) {
				                       // TODO Auto-generated catch block
				                        e.printStackTrace();
			                                }							
			                                try {
				                             IndexerDbConnection.EndIndexing(link);
			                                     } catch (SQLException e) {
				                       // TODO Auto-generated catch block
				                        e.printStackTrace();
			                                }
							
							continue;
						}
						try 
						{
							//If the link is not null then we start the indexing process for this link
							IndexerDbConnection.BeginIndexing(link.toString());
							
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
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
				//We then loop on each degree and call parseText function which gets all the text content for a certain degree, splits the text into words,
				//then stems each word,adds it to the stemmedWords array list, then adds it to the database (if not inserted before) or increments its count in the corresponding
				//degree column (if inserted before in db)
				System.out.println("^^^^^^^^^^^" + degrees.size());
				for(int i = 0 ;i<degrees.size();i++)
				{
					ArrayList<String> wordsOfTag = new ArrayList<String>();
					String [] text = null;
					text = htmlDoc.select(degrees.get(i)).text().split("\\s+");
					if(text[0] != "" && text.length != 1)
					{
						int num = htmlDoc.select(degrees.get(i)).text().split("\\s+").length;	
						countWords = countWords + num;
					}
					
					try {
						parseText(link,htmlDoc, degrees.get(i),stemmedWords,stopwords);
						
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			//Set end indexing of this url to 1 to mark it as fully parsed
			try {
				IndexerDbConnection.EndIndexing(link);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println(countWords);
			
			try {
				
				IndexerDbConnection.updateWordCount(link, countWords);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			 
			public static void parseText(String link, Elements textDoc, String degree, ArrayList<String> stemmedWords,ArrayList<String> stopwords) throws SQLException
			 {
				 Elements selectedWords = textDoc.select(degree); //We select from the document the text based on whether the degree passed is h1, h2, h3, ..
				 String extractedText =  selectedWords.text(); //We extract the text content
				 String[] splittedText = null;
				 ArrayList<String> arrayOfWords = new ArrayList<String>();
				 if(extractedText.length() != 0)
				 {
					 splittedText = extractedText.split("[^a-zA-Z0-9]"); //We split the text into words
					
					 for(int i = 0 ;i<splittedText.length;i++)
					 {
						 if(splittedText[i] != " ")
						 {
							 arrayOfWords.add(splittedText[i]);	
						 }
					 }
				 }
				
				 
				 if(arrayOfWords.size() != 0)
				 {
					 Stemmer s = new Stemmer();
					 for (int i=0 ;i<arrayOfWords.size();i++)
					 {
						 arrayOfWords.set(i, arrayOfWords.get(i).toLowerCase()) ; 
						 if(!stopwords.contains(arrayOfWords.get(i))) //Check if the current word is a stopword or not
						 {
							 if(isNumber(arrayOfWords.get(i)) && arrayOfWords.get(i).length() == 4) //Check if the string is a year for example
							 {
								 boolean flag = IndexerDbConnection.isExistWord(link, arrayOfWords.get(i));
								 if(!flag) //Check if it has been added to the stemmedWords before
								 { 
									 stemmedWords.add(arrayOfWords.get(i));
									 //call db function to add the stem to db
									 try 
									 {
										IndexerDbConnection.AddNewWord(link,arrayOfWords.get(i), degree);
									 } 
									 catch (SQLException e) 
									 {
										// TODO Auto-generated catch block
										e.printStackTrace();
									 }
									 continue;
								 }
								 else
								 {
									//Increment occurrence of the stem
									 try 
									 {
										IndexerDbConnection.IncrementCounts(link,arrayOfWords.get(i), degree);
									 } 
									 catch (SQLException e) 
									 {
										// TODO Auto-generated catch block
										e.printStackTrace();
									 } 
								 }
							 }
							 if(!arrayOfWords.get(i).trim().isEmpty() && !arrayOfWords.get(i).isEmpty() && !isNumber(arrayOfWords.get(i))) //Check if the splittedText is not blank or empty or a number 
							 {
								 //System.out.println(splittedText[i]);
								 String stem = s.StemWord(arrayOfWords.get(i)); //Stem the splittedText
								 boolean flag2 = IndexerDbConnection.isExistWord(link, stem);
								 if(!flag2) //Check if it has been added to the stemmedWords before
								 { 
									 
									 if(stem.length()>1)
									 {
										 stemmedWords.add(stem);  //Add the stem to the stemmedWords
										//Call db function to add the stem to db
										 try 
										 {
											IndexerDbConnection.AddNewWord(link,stem, degree);
										 } 
										 catch (SQLException e) 
										 {
											// TODO Auto-generated catch block
											e.printStackTrace();
										 }
									 }
								 }
								 else
								 {
									//Increment occurrence of the stem
									 try 
									 {
										IndexerDbConnection.IncrementCounts(link,stem, degree);
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

	 public static void main(String args[]) throws Exception {
		
	    	 System.out.println("Do you want to: 1.index 2.reset indexing");
	    	 BufferedReader consoleReader =  new BufferedReader(new InputStreamReader(System.in)); 
	    	 String choice = consoleReader.readLine(); 
	        
	        //Re-indexing logic
	        if(Integer.parseInt(choice)==2) {
	        	IndexerDbConnection.deleteIndexingWordsTable(); //delete all entries in the indexing words table
	        	IndexerDbConnection.zeroingEndAndBegin(); //reset the beginIndexing and endIndexing of all links
	    		//TODO: delete rank results
	        	
	        }
	        else{	
	        	IndexerDbConnection.ResetAlreadyStarted();//Start indexing from the last link that has not yet been indexed
	        }
			Indexer indexerMain = new Indexer();
			System.out.println("*******************INDEXER***************************");
			System.out.println("stopwords " + stopwords);
			System.out.println("count of threads " + countOfThreads);
			linksList = IndexerDbConnection.returnUnIndexedUrls();
			
			System.out.println("list of links" + linksList);
			//Get the share for each thread
			if(linksList.size() % countOfThreads == 0)
			{
				shareRestOfThreads = linksList.size()/countOfThreads;
				shareFirstThread = shareRestOfThreads;
			}
			else
			{
				shareRestOfThreads=(linksList.size()/countOfThreads); //share for each of the threads other than first thread.
				shareFirstThread=linksList.size()-(shareRestOfThreads*(countOfThreads-1)); //share of first thread
			}
			int threadShareEnd = 0;
			//Create the threads and assign each one their share
			for (int i = 0 ;i<countOfThreads;i++)
			{
				if(i == 0)
				{
					int threadId=i;
					Index indexer=new Index(threadId,key, linksList,0, shareFirstThread);
					threadShareEnd=shareFirstThread;
					indexer.setName("Thread "+ i);
					indexerThreads.add(indexer);
					indexer.start();
					System.out.print("****first if\n");
					System.out.println(indexer.start + " " + indexer.end + " " + indexer.threadId);
					System.out.print("\n****end of thread");
				}
				else
				{
					int threadId=i;
					Index indexer2=new Index(threadId,key, linksList,threadShareEnd,threadShareEnd+shareRestOfThreads);
					threadShareEnd=threadShareEnd+shareRestOfThreads;
					indexer2.setName("Thread "+ i);
					indexerThreads.add(indexer2);
					indexer2.start();
					System.out.print("\n****second if\n");
					System.out.println(indexer2.start + " " + indexer2.end + " " + indexer2.threadId);
					System.out.print("\n****end of thread\n");
				}
			}
			
			for(int i = 0; i<indexerThreads.size();i++)
			{
				indexerThreads.get(i).join();
			}
			Ranker.pageRanker();
			System.out.println("Khalasna");
		}
}
