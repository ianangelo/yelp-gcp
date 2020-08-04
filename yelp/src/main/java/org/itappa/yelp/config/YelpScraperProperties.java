package org.itappa.yelp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


import lombok.Data;

@Component
@ConfigurationProperties("yelp.client")
@Data
public class YelpScraperProperties {

	private String url;
	private String name;
	private String repositories;
	private String date;
	private String image;
	private String review;
	
}
