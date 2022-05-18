package com.springboot;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller

public class WelcomeController {
	
	@RequestMapping("/homepage")
	public String homepage()
	{
		return "homepage";
	}
	
	
	@PostMapping(value = "/getQuery/{query}")
	public ResponseEntity <?> getSearchQuery(@PathVariable String query)
	{
		System.out.println(query);
		return new ResponseEntity<>(new Query(), HttpStatus.OK);
	}
}
