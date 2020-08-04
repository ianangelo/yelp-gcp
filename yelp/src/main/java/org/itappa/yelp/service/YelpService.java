package org.itappa.yelp.service;

import java.io.IOException;
import java.util.List;

import org.itappa.yelp.dto.ReviewDto;
import org.itappa.yelp.dto.ReviewScrapeDto;

public interface YelpService {
	
	public List<ReviewDto> getReviews(String businessId);
	public String searchBusiness(String searchKey);
	public List<ReviewScrapeDto> scrapeReviews(String restaurant) throws IOException;

}
