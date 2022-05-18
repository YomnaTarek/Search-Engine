package Ranker;

import java.util.*;
import java.net.URL;
import java.sql.SQLException;
import java.net.MalformedURLException;
import DBManager.DBManager;
import java.lang.Math;
public class Ranker {

	public Ranker() {
		// TODO Auto-generated constructor stub
	}
	
	// pageRanker algorithm to calculate popularity
	public static void pageRanker() throws SQLException, MalformedURLException
	{
        //nb of iterrations needed in algorithm
		int maxNbIterations= 3;

        //total number of links in our DB
		int nbOfURLs=DBManager.getURLCount();

        //2 lists needed to calculate the rank of each link
		double calculationRanks[] = new double[nbOfURLs];
        double Ranks[] = new double[nbOfURLs];
        Arrays.fill(Ranks, (double)((double)1/(double)nbOfURLs));//initalizing all the ranks with an initial value of 1/total number of links
        System.out.printf("\n Initial PageRank Values , 0th Step \n");
        for (int k = 0; k < nbOfURLs; k++)
        {
        System.out.printf(" Page Rank of " + k + " is :\t" + Ranks[k] + "\n");		
            }
        double DampingFactor=0.85;//damping factor needed for calculations in the algorithm

		List<URL> links = DBManager.getAllURLs();
	
		for(int j =0; j<maxNbIterations;j++)
		{
            for(int i=0; i<nbOfURLs;i++)
            {
                calculationRanks[i]=Ranks[i];
                Ranks[i]=0;
            }
            int l=0;
			for(URL link : links)
			{
                List<URL> inboundURLs = DBManager.getInbounds(link.toString());
				// loop on this array (inboundURLS)
                int k=0;
				for(URL inbound : inboundURLs)
				{
                    //getting the number of outbounds for each link that references our original link
					int outbounds = DBManager.getCountOutbounds(inbound.toString());
                    Ranks[l]+=calculationRanks[k]/outbounds;
					k++;
				}
				l++;
			}
            
        }
		for (int i=0; i<nbOfURLs; i++) {
            Ranks[i] = (1 - DampingFactor) + DampingFactor * Ranks[i];
            URL current =links.get(i);
            DBManager.updatePopularity(current.toString(), Ranks[i]);
           }
         	// Display PageRank
        System.out.printf("\n Final Page Rank : \n");
        for (int i=0; i<nbOfURLs; i++) {
        System.out.printf(" Page Rank of " + i + " is :\t" + Ranks[i] + "\n");
        }
                
	}
    
    public static double  calculatingIDF( String queryWord) throws SQLException
    {
      int totalNumberURLs=DBManager.getURLCount();
      int countForWord=DBManager.getCountURLsForWord(queryWord);
       return Math.log((double)((double)totalNumberURLs/(double)countForWord));
    }

    public static double calculatingTF(URL url, String queryWord) throws SQLException
    {
        int totalWords=DBManager.getCountWordsForURL(url.toString());

        //getting the count of the word according to its position
        int titleCount=DBManager.getCountTitle(queryWord, url.toString());
        int h1Count=DBManager.getCountH1(queryWord, url.toString());
        int h2Count=DBManager.getCountH2(queryWord, url.toString());
        int h3Count=DBManager.getCountH3(queryWord, url.toString());
        int h4Count=DBManager.getCountH4(queryWord, url.toString());
        int h5Count=DBManager.getCountH5(queryWord, url.toString());
        int h6Count=DBManager.getCountH6(queryWord, url.toString());
        int boldCount=DBManager.getCountBold(queryWord, url.toString());
        int italicCount=DBManager.getCountItalic(queryWord, url.toString());
        int pCount= DBManager.getCountP(queryWord, url.toString());
       // System.out.println(titleCount +" "+h1Count+" "+h2Count+" "+h3Count+" "+h4Count+" "+h5Count+" "+boldCount+" "+italicCount+" ");
        double wordFrequency=0.3*titleCount+0.15*h1Count+0.1*(h2Count+h3Count)+0.04*(h4Count+h5Count+h6Count)+0.1*(italicCount+boldCount)+0.03*pCount;
        //System.out.println(wordFrequency);
        return (double)((double)wordFrequency/(double)totalWords);
    }
	// relavance algorithm tf-idf to calculate relevance
    public static void pageRelevance(ArrayList<String> query) throws MalformedURLException, SQLException
    {
        DBManager.addPopularity();
        //getting IDF
        List<URL> links = DBManager.getAllURLs();//to get revelance to all links
        for(String word :query)
        {
            double idf=calculatingIDF(word);
            for(URL link : links)
                {
                    double tf=calculatingTF(link, word);
                    double rank=idf*tf;
                    DBManager.updateRank(link.toString(), rank);//addition(tf-idf of all query words) is done inside the database
                }
        }

    }
    public static void phraseSearchRanking(ArrayList<String> query) throws MalformedURLException, SQLException
    {
        DBManager.resetRank();//setting all ranks =0
        //getting IDF
        List<URL> links = DBManager.getAllURLs();//to get revelance to all links
        int length=query.size();
        int count=0;
        double rank=0;
        
        double calculationRanks[] = new double[length];
        for(URL link :links)
        {
            Arrays.fill(calculationRanks,0);
            int i=0;
            count=0;
            for(String word:query)
            {
                double tf=calculatingTF(link, word);
                double idf=calculatingIDF(word);
                rank=idf*tf;
                calculationRanks[i]=rank;
                i++;
                if(tf>0)
                {
                    count++;
                }
            }
            if(count==length)
            {
                DBManager.addPopularityForURL(link.toString());
               for(int j=0; j<calculationRanks.length; j++)
                {
                    DBManager.updateRank(link.toString(), calculationRanks[j]);
                }
            }
        }

    }
	public static void main(String[] args) throws MalformedURLException, SQLException {
        pageRanker();
        ArrayList<String> q=new ArrayList<>();
        q.add("habiba");
        q.add("zeh2et");
        phraseSearchRanking(q);
    }

}

