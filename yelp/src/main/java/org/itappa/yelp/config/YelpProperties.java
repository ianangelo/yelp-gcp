package org.itappa.yelp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


import lombok.Data;

@Component
@ConfigurationProperties("yelp.server")
@Data
public class YelpProperties {

	private String url;
	private Api api;
	private App app;
	
	@Data
	public static class Api {
		private String reviews;
		private String search;
	}
	
	@Data
	public static class App {
		private String key;
		private String cliendId;
	}
}
