package org.itappa.yelp.controller;

import java.io.IOException;
import java.util.List;

import org.itappa.yelp.dto.ReviewDto;
import org.itappa.yelp.dto.ReviewScrapeDto;
import org.itappa.yelp.service.YelpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RequestMapping("/yelp")
@RestController
public class YelpController {
	
	@Autowired
	private YelpService service;
	
	/**
	 * Retrieves 3 reviews from Yelp API
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/{id}/reviews", produces = {
            MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<List<ReviewDto>> getReviews(@PathVariable String id) {
		
		List<ReviewDto> reviews= service.getReviews(id);
		
		return new ResponseEntity<>(reviews, HttpStatus.OK);
	}
	
	/**
	 * Search for businesses
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/search/{searchKey}", produces = {
            MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<String> searchBusinnes(@PathVariable String searchKey) {
		
		return new ResponseEntity<>(service.searchBusiness(searchKey), HttpStatus.OK);
	}
	
	/**
	 * Retrieve reviews by web scraping
	 * 
	 * @param restaurant
	 * @return
	 * @throws IOException
	 */
	@GetMapping("/scrape-reviews/{restaurant}")
	public ResponseEntity<List<ReviewScrapeDto>> scrapeReviews(@PathVariable String restaurant) throws IOException {
		List<ReviewScrapeDto> reviews = service.scrapeReviews(restaurant);
		if (CollectionUtils.isEmpty(reviews)) {
			return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<ReviewScrapeDto>>(reviews, HttpStatus.OK);
	}
	

	


}
