package org.itappa.yelp.dto;

import com.google.cloud.vision.v1.Likelihood;

import lombok.Data;

@Data
public class ReviewDto {
	private String id;
	private int rating;
	private String userName;
	private String userProfileUrl;
	private String raviewText;
	private String timeCreated;
	private String ratingUrl;
	private Likelihood sorrow;
	private Likelihood joy;
	private Likelihood surprise;
	private Likelihood anger;
	
}
