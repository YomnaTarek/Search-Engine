package Crawler;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;

import DBManager.DBManager;

public class Crawl implements Runnable {

    //Number of Crawled pages is 5000 pages
    public int threshold=5000;

    //appropriate data structure to determine the order of page visits is a queue
    static List<URL> LinksQueue;

    //lock for synchronizing the threads while dealing with the database
    public Integer lock;

    //thread number used if the process is suddenly terminated
    int threadNumber;

    Crawl( List<URL> Links, int start, int size, int lock, int threadNum){
        LinksQueue=Links.subList(start,start+size-1); //each threads gets a specified part of the seedlist  
        this.lock=lock; 
        this.threadNumber=threadNum;
    }

    @Override
    public void run() {

        int URLcount=0;
        URL current;

           while(!LinksQueue.isEmpty() && URLcount<threshold)     
           {
               //getting the first element in the queue
              current= LinksQueue.get(0);
              //inserting in the database which thread is handeling which link at the moment
              String threadName=Thread.currentThread().getName();
              synchronized(this.lock)
              {
              try {
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
                    synchronized(this.lock)
                    {
                       try {
                        URLcount=DBManager.getURLCount();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                
                     try {
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
                    DBManager.deleteThreadURL(Integer.parseInt(threadName));
                } catch (NumberFormatException | SQLException e) {
                    e.printStackTrace();
                }

           }
    }
    public static void main(String[] args) throws SQLException {
        //getting the number of threads from the user
        int numberOfThreads=WebCrawler.readNumberOfThreads();

        //reading the seedlist and calculating the share of each thread
        List<URL> seed=WebCrawler.readingSeed();
        int seedSize=seed.size();
        int share=seedSize/numberOfThreads;
        int lock=0;

       //inserting seedList in the DB
       for (int i=0; i<seedSize; i++)
       {
           DBManager.addURL(seed.get(i).toString(), i);
       }

        //thread ids go from 0 to the threadnumber
        for(int i=0; i<numberOfThreads; i++)
        {
            //we start the threads (each one gets its share of the seedList)
          Thread t1=  new Thread(new Crawl(seed, i*share, share, lock,i));
          t1.setName(Integer.toString(i));
          t1.start();
        }

    }
    
}
