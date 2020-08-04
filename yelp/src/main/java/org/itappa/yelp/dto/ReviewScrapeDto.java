package org.itappa.yelp.dto;

import com.google.cloud.vision.v1.Likelihood;

import lombok.Data;

@Data
public class ReviewScrapeDto {
	private String rating;
	private String userName;
	private String imageUrl;
	private String raviewText;
	private String timeCreated;
	private Likelihood sorrow;
	private Likelihood joy;
	private Likelihood surprise;
	private Likelihood anger;
	
}
