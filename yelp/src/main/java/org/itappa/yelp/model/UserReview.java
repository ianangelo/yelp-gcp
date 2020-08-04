package org.itappa.yelp.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserReview {
	
	private String id;
	private int rating;
	private String text;
	@JsonProperty("time_created")
	private String timeCreated;
	private String url;
	private User user;

}