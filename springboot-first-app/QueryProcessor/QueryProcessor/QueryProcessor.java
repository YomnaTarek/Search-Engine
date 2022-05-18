package QueryProcessor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import Indexer.Stemmer;
import Ranker.Ranker;

import java.util.Scanner;

public class QueryProcessor {

//function to fetch the list of stopwords from stopword.txt
public static ArrayList<String> fetchStopWords()
{
    String filename="stopwords.txt";
    ArrayList<String> stopwords = new ArrayList<String>();
    try {
         File myObj = new File(filename);
         Scanner myReader = new Scanner(myObj);
         while (myReader.hasNextLine()) {
           String data = myReader.nextLine();
           stopwords.add(data);
           
         }
         myReader.close();
       } catch (FileNotFoundException e) {
         System.out.println("An error occurred.");
         e.printStackTrace();
       }

   return stopwords;  
}

    
//function for checking whether or not a given string is a number
public static boolean isNumber(String s)
{
    for (int i = 0; i < s.length(); i++)
        if (Character.isDigit(s.charAt(i)) == false)
            return false;

    return true;
}

//Function to apply the same preprocessing applied to the documents in the indexer to the search query:
public static ArrayList<String> preProcessing (String query, ArrayList<String> stopwords)
{
    //1) We split the search query into words
    String[] splittedText = query.split("[^a-zA-Z0-9]"); 
    ArrayList<String> arrayOfWords=new ArrayList<String>();
    for(int i = 0 ;i<splittedText.length;i++)
    {
        if(splittedText[i] != " ") //eliminating any empty strings
        {
            arrayOfWords.add(splittedText[i]);	
        }
    }    
    //2) Stemming and checking if any word in the search query is a stop word:
    ArrayList<String> stemmedWords=new ArrayList<String>();
    Stemmer s = new Stemmer();
    for (int i=0 ;i<arrayOfWords.size();i++)
    {
        arrayOfWords.set(i, arrayOfWords.get(i).toLowerCase()) ; 
        if(!stopwords.contains(arrayOfWords.get(i))) //Check if the current word is a stopword or not
            {
                    if(isNumber(arrayOfWords.get(i)) && arrayOfWords.get(i).length() == 4) //Check if the string is a year for example
                    {
                        //we add the number directly to the array of stemmed words because numbers are not stemmed
                        if(!stemmedWords.contains(arrayOfWords.get(i)))
                             stemmedWords.add(arrayOfWords.get(i));
                
                    }
                    if(!arrayOfWords.get(i).trim().isEmpty() && !arrayOfWords.get(i).isEmpty() && !isNumber(arrayOfWords.get(i))) //Check if the splittedText is not blank or empty or a number 
                    {
                        String stem = s.StemWord(arrayOfWords.get(i)); //Stem the splittedText
                        if(stem.length()>1)
                        {
                            stemmedWords.add(stem);
                        }

                    }
            }
    }
    return stemmedWords;
}

public static void main(String[] args) throws IOException, SQLException {

    //The user should enter the search query.
    System.out.println("Please enter the search query"); 
    Scanner scan = new Scanner(System.in);
    String query = scan.nextLine();
    ArrayList<String> stopWordsList=fetchStopWords();      //fetching the stop words.
    ArrayList<String> processedWords=preProcessing (query,stopWordsList); //preprocessing the search query.
    System.out.println(processedWords);
    if(query.startsWith("\"") && query.endsWith("\""))
    {
    Ranker.phraseSearchRanking(processedWords);
    }
    else
    {
    Ranker.pageRelevance(processedWords);
    }
    scan.close();

}

}

