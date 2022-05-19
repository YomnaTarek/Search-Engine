package com.springboot;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class ResultsController {

	public static String query;
	
	@GetMapping("/results")
	public String displayResults() throws MalformedURLException, SQLException
	{
		WelcomeController w = new WelcomeController();
		String query = null;
		//Model model = null;
		//w.getQuery(query);
		ArrayList<String> stopwords = new ArrayList<String>();
		stopwords = QueryProcessor.QueryProcessor.fetchStopWords();
		ArrayList<String> processedQuery = new ArrayList<String>();
		//System.out.println(query);
		//System.out.println(stopwords);

		processedQuery = QueryProcessor.QueryProcessor.preProcessing(query, stopwords);
		Ranker.Ranker.pageRelevance(processedQuery);
		//ArrayList<String> results = DBManager.DBManager.sortResults(limit, pageNumber)
		
	
		return "results";
	}
	
	
}
