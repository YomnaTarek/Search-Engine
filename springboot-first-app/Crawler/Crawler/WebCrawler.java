package Crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.io.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import DBManager.IndexerDbConnection;


public class WebCrawler {
//Number of Crawled pages is 5000 pages
int threshold=5000;
  
//Number of threads entered by the user (default =5)
int numberOfThreads=5;

//appropriate data structure to determine the order of page visits is a queue
static List<URL> LinksQueue;

static int id = 0;
  //function to read number of threads from user
public static int readNumberOfThreads()
{
    BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in)); 
    int numberOfThreads=0;
    try {
        System.out.print("Please enter number of threads:"+"\n" );	
        String temp = consoleReader.readLine();
       numberOfThreads=Integer.parseInt(temp);
       IndexerDbConnection.setNumberOfThreads(id, numberOfThreads);

    } catch (IOException e) {
        
        e.printStackTrace();
    }
    return numberOfThreads;
}

//function to read original URLs from the seed list file
public static List<URL>  readingSeed()
 { ArrayList<URL> q=new ArrayList<>();
    try {
       
       //should be dynamic-> this is temporary
        //File myObj = new File("C:\\Users\\habib\\Desktop\\Senior1-Sem2\\APT\\Project APT\\Search-Engine\\seedList.txt");
    	 File myObj = new File("C:\\Users\\saiko\\OneDrive\\Desktop\\Search-Engine\\springboot-first-app\\seedList.txt");
    	Scanner fileReader = new Scanner(myObj);
        while (fileReader.hasNextLine()) {
            String data = fileReader.nextLine();
            try {
                q.add(new URL(data));
            } catch (MalformedURLException e) {
                
                e.printStackTrace();
            }
        }
        fileReader.close();
    } catch (FileNotFoundException e) {
        System.out.println("Error: could not continue reading seedList"+"\n" );
        e.printStackTrace();
    }
    return q;
}

//function to download html document from url and parse it for new hyperLinks
public static List<URL> HTMLParse(URL originalURL)
{
    ArrayList<URL> discoredURLs=new ArrayList<>();
    try {
       //connecting to the jsoup library to download the html document
        Document document = Jsoup.connect(originalURL.toString()).get();
        //selecting the available hyperlinks on the page
        final Elements hyperLinks = document.select("a[href]");
        
          for (final Element Link : hyperLinks) 
          {   
              String discoveredLink = Link.attr("abs:href").trim();
              //if link is emty pass to the next one
              if(discoveredLink=="")
                  continue;
             //we need to normalize the urls that we have to  check if they are referring to the same page
             //we use an object from the class NormalizeURL to do so.
            discoveredLink=NormalizeURL.normalize(discoveredLink);  
              
              //if link is an image or pdf file or login page pass to the next one
              if(discoveredLink.matches("(.*).jpg")||discoveredLink.matches("(.*)/pdf")||discoveredLink.matches("(.*).pdf")||discoveredLink.matches("(.*).png")
                    ||discoveredLink.matches("(.*)/image(.*)")||discoveredLink.matches("(.*)login(.*)"))
                  continue;
            //if  document's protocol is different that http and https pass to the next one
              if(!discoveredLink.matches("http://(.*)") && !discoveredLink.matches("https://(.*)")) 
                  continue;
              if(!RobotTest(originalURL, discoveredLink))
                  continue;
         //if we passed all the restrictions we add the normalized link to an array of URLs and return it  
         //making sure first that the url does not already exsist in the array
         if(!discoredURLs.contains(new URL( discoveredLink)))   
         discoredURLs.add(new URL( discoveredLink));

          }
        } catch (IOException e) {
            System.out.print("Jsoup Error: "+ e +"\n" );
            e.printStackTrace();
            
        }
    return discoredURLs;
}

//function to determin if url is disallowed in robots.text
 static boolean RobotTest (URL originalURL, String discoveredURL) {
    //creating the domain which will contain the robots.txt file
    String Domain =  originalURL.getProtocol() + "://" + originalURL.getHost();
  
    Document htmlDocument;
    try {
            htmlDocument = Jsoup.connect(Domain + "/robots.txt").get();
    }
    catch ( IOException ioe){
        //if there is no robots.txt file this means that the discovered UrL is allowed
       return true;
    }

    //getting all the words in the html document to parse them 
    String[] allDocumentwords = htmlDocument.text().split(" ");
    ArrayList<String> restrictions=new ArrayList<>();
    boolean userAgentExists = false;
    for (int i = 0; i < allDocumentwords.length-2; i++) {
        //parsing the document until we fing a user-agent
        if (allDocumentwords[i].equals("User-agent:") && (allDocumentwords[i + 1].equals("*"))) {
            userAgentExists = true;
            i += 2;
        }
        //adding all the restrictions and the file names to a list
        if (userAgentExists) { 
            restrictions.add(allDocumentwords[i]);
        }
    }

    //looping on the restristrictions list to see if there are any disallow restriction 
    //and checking if the restricted file is the same as the discovered RUL passed to the function
    for (int i = 0; i <  restrictions.size() - 1; i++) {
        if ( restrictions.get(i).equals("Disallow:"))
        {
            // restrictions.get(i + 1) gets the restricted file name
            int difference = discoveredURL.compareTo((Domain +  restrictions.get(i + 1)));
            if(difference ==0)
            {
                return false;
            }
        }  
    }
   //discovered url is not disallowed in the robots.txt file
return true;
}

    public static void main(String[] args) throws Exception{

        // String test=NormalizeURL.normalize("https://example.com/");
        // System.out.println(test);
        LinksQueue=readingSeed();
        for(int i=0; i<LinksQueue.size(); i++)
        {
        System.out.println(LinksQueue.get(i).toString()+"\n" );
        }
        System.out.println("---------------now getting hyperlinks--------------");
        List<URL> list =HTMLParse(LinksQueue.get(0));

         for(int i=0; i<list.size(); i++)
        {
        System.out.println(list.get(i).toString()+"\n" );
        }
    }
}