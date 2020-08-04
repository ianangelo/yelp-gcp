package org.itappa.yelp.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.itappa.yelp.config.YelpProperties;
import org.itappa.yelp.config.YelpScraperProperties;
import org.itappa.yelp.dto.ReviewDto;
import org.itappa.yelp.dto.ReviewScrapeDto;
import org.itappa.yelp.exception.BadRequestException;
import org.itappa.yelp.exception.InternalServerException;
import org.itappa.yelp.exception.NotFoundException;
import org.itappa.yelp.model.UserReviews;
import org.itappa.yelp.service.YelpService;
import org.itappa.yelp.service.util.YelpUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class YelpServiceImpl implements YelpService {

	public static final String PARAM_ID = "{id}";
	public static final String PARAM_SEARCH_TERM = "term";
	public static final String BEARER = "Bearer ";

	@Autowired
	private YelpProperties properties;

	@Autowired
	private YelpScraperProperties scraperProperties;
	
	@Autowired
	private YelpUtil util;

	

	RestTemplate restTemplate = new RestTemplate();

	@Override
	public List<ReviewDto> getReviews(String businessId) {

		String url = properties.getUrl() + properties.getApi().getReviews();
		url = url.replace(PARAM_ID, businessId);

		ParameterizedTypeReference<UserReviews> responseType = new ParameterizedTypeReference<UserReviews>() {
		};
		ResponseEntity<UserReviews> response = restTemplate.exchange(url, HttpMethod.GET,
				new HttpEntity<>(null, buildHttpAuthHeader()), responseType);

		UserReviews reviews = response.getBody();
		handleStatusCode(response.getStatusCode(), reviews.getReviews().isEmpty());
		List<ReviewDto> reviewList = reviews.getReviews().stream().map(util::from).collect(Collectors.toList());

		return reviewList;
	}

	

	private void handleStatusCode(HttpStatus status, boolean isEmpty) {

		if (status == HttpStatus.BAD_REQUEST) {
			throw new BadRequestException();
		} else if (status == HttpStatus.NOT_FOUND || isEmpty) {
			throw new NotFoundException();
		} else if (status.value() >= 500) {
			throw new InternalServerException();
		}
	}

	private HttpHeaders buildHttpAuthHeader() {
		String authToken = BEARER + properties.getApp().getKey();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", authToken);
		return headers;
	}

	
	@Override
	public String searchBusiness(String searchKey) {
		String url = properties.getUrl() + properties.getApi().getSearch();
		UriComponents builder = UriComponentsBuilder.fromHttpUrl(url).queryParam(PARAM_SEARCH_TERM, searchKey)
				.queryParam("location", "Philippines").build();

		ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET,
				new HttpEntity<>(null, buildHttpAuthHeader()), String.class);

		handleStatusCode(response.getStatusCode(), null == response.getBody());
		return response.getBody();
	}

	@Override
	public List<ReviewScrapeDto> scrapeReviews(String restaurant) throws IOException {
		Document doc = Jsoup.connect(scraperProperties.getUrl() + restaurant).get();
		Elements repositories = doc.getElementsByClass(scraperProperties.getRepositories());

		List<ReviewScrapeDto> reviews = repositories
				.stream()
				.map(repository -> {
					String reviewTxt = repository.getElementsByClass(scraperProperties.getReview()).text();
					String date = repository.getElementsByClass(scraperProperties.getDate()).text();
					String rating = repository.getElementsByAttributeValueContaining("aria-label", "star rating")
							.attr("aria-label");
					String name = repository.getElementsByClass(scraperProperties.getName()).text();
					String imgUrl = repository.select(scraperProperties.getImage()).first().absUrl("src");
					
					return util.from(rating, name, reviewTxt, imgUrl, date);
				})
				.collect(Collectors.toList());

		return reviews;
	}

}
