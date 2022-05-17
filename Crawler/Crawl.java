package Crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

import DBManager.DBManager;

public class Crawl implements Runnable {

    //Number of Crawled pages is 5000 pages
    public static int threshold=40;

    //appropriate data structure to determine the order of page visits is a queue
    ArrayList <URL> LinksQueue=new ArrayList<>();

    //lock for synchronizing the threads while dealing with the database
    public Integer lock;

    //thread number used if the process is suddenly terminated
    int threadNumber;

    Crawl( List<URL> Links, int start, int size, int lock, int threadNum){
        for(int i=start; i<start+size; i++)
        {
            LinksQueue.add(Links.get(i));
        } //each threads gets a specified part of the seedlist  
        this.lock=lock; 
        this.threadNumber=threadNum;
    }

    @Override
    public void run() {

        int URLcount=0;
        URL current;
        String threadName=Thread.currentThread().getName();
        for (int i=0; i<LinksQueue.size(); i++)
        {
         System.out.print("Thread("+ threadName+") index"+Integer.toString(i)+" :" +LinksQueue.get(i).toString()+"\n");
        }
           while(URLcount<threshold)     
           {
               //getting the first element in the queue
              current= LinksQueue.get(0);
              
              synchronized(this.lock)
              {
              try {
                System.out.print("Thread"+threadName+" is inserting into threads table the url:"+current.toString()+"\n" );	
                //inserting in the database which thread is handeling which link at the moment
                int threadTableID=DBManager.getCountThreadURL();
                DBManager.insertThreadURL(current.toString(),Integer.parseInt(threadName),threadTableID);
            } catch (NumberFormatException | SQLException e1) {
                e1.printStackTrace();
            }
            }
              List<URL> DiscoveredLinks = WebCrawler.HTMLParse(current); //returns the normalized list of urls in this link
              for (final URL link : DiscoveredLinks) 
              {
                  //getting the number of threads inside the DB
                    synchronized(this.lock)//synchronization is needed when dealing with database elements
                    {
                       try {
                        URLcount=DBManager.getURLCount();
                    //checking to see if we surpassed the threshold or if the link was already parsed
                        if(DBManager.isExistentURL(link.toString()))
                        {
                            //if the url already exists in the db we need to increment its inbound number
                            DBManager.incrementInBound(link.toString());
                            if(!DBManager.isExistentDiscovered(current.toString(), link.toString()))
                            {
                                int id=DBManager.getCountDiscovered();
                                //adding the 2 urls in the bounds table that tells us which url called which hyperlink
                                DBManager.insertDiscoveredLink(id, current.toString(), link.toString());
                            }
                           
                        }
                        if(URLcount<threshold && !DBManager.isExistentURL(link.toString()))
                        {
                            //if not we add it to the list used to get more hyperlinks and to the db for storage
                        LinksQueue.add(link);
                        System.out.print("Thread"+threadName+" is inserting into Link table the url:"+link.toString()+"\n" );	
                        DBManager.addURL(link.toString(),URLcount);
                        int id=DBManager.getCountDiscovered();
                        //adding the 2 urls in the bounds table that tells us which url called which hyperlink
                        DBManager.insertDiscoveredLink(id, current.toString(), link.toString());
                        }
                         //the outbound of the current url needs to be incremented in both cases
                         DBManager.incrementOutBound(current.toString());
                    
                    } catch (SQLException e) {
                        
                        e.printStackTrace();
                    }
                    }
              }
              //when we finished with this element it is removed from queue and we declare in the DB that it finished parsing
              try {
                DBManager.setParsed(current.toString());
            } catch (SQLException e) {
                
                e.printStackTrace();
            }
              LinksQueue.remove(0);

          //deleting the thread entry from the thread table as it finished parsing that id
              
                try {
                    System.out.println(
                        "thread"+threadName+" terminating"
                    );
                    DBManager.deleteThreadURL(Integer.parseInt(threadName));
                } catch (NumberFormatException | SQLException e) {
                    e.printStackTrace();
                }

           }
           
    }
    public static void main(String[] args) throws SQLException, MalformedURLException {
        //getting the number of threads from the user
        int numberOfThreads=WebCrawler.readNumberOfThreads();

        //reading the seedlist and calculating the share of each thread
        List<URL> seed=WebCrawler.readingSeed();
        int seedSize;
        int share;
        int lock=0;
        ArrayList<URL> seedElements=new ArrayList<>();
    if(DBManager.getURLCount()==0)
        {
        seedSize=seed.size();
        share=seedSize/numberOfThreads;

       //inserting normalized seedList in the DB
       for (int i=0; i<seedSize; i++)
       {
          String tempElement= NormalizeURL.normalize(seed.get(i).toString());
           System.out.print("Inserting seed element:"+Integer.toString(i)+"\n" );	
           DBManager.addURL(tempElement, i);
           seedElements.add(new URL(tempElement));
       }
       
        //thread ids go from 0 to the threadnumber
        for(int j=0; j<numberOfThreads; j++)
        {
            //we start the threads (each one gets its share of the seedList)
          Thread t1=  new Thread(new Crawl(seedElements, j*share, share, lock,j));
          t1.setName(Integer.toString(j));
          t1.start();
        }
    }
        //--------------------------continue crawling if interrupted------------------------------
        //we need to add recrawling if interrupted
        //check if there is unparsed elements and the database is not empty then we need to continue crawling
     else if(DBManager.getURLCount()<threshold &&  DBManager.getCountUnparsed()!=0 )
     {
        numberOfThreads=WebCrawler.readNumberOfThreads();
         seed.clear();
         seedElements.clear();

          //the new elements to be crawled are the ones that were unparsed in the db
         seed=DBManager.getUnparsedURLs();
         seedSize=seed.size();
         share=seedSize/numberOfThreads;

       //normalization
       for (int i=0; i<seedSize; i++)
       {
          String tempElement= NormalizeURL.normalize(seed.get(i).toString());
           seedElements.add(new URL(tempElement));
       }
         //thread ids go from 0 to the threadnumber
         for(int j=0; j<numberOfThreads; j++)
         {
             //we start the threads (each one gets its share of the seedList)
           Thread t1=  new Thread(new Crawl(seedElements, j*share, share, lock,j));
           t1.setName(Integer.toString(j));
           t1.start();
         }
     }

    }
    
}
