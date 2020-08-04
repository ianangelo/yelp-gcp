package org.itappa.yelp.service.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.itappa.yelp.dto.ReviewDto;
import org.itappa.yelp.dto.ReviewScrapeDto;
import org.itappa.yelp.model.UserReview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.FaceAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;

@Component
public class YelpUtil {
	@Autowired
	private ResourceLoader resourceLoader;
	
	public ReviewDto from(UserReview review) {
		ReviewDto dto = new ReviewDto();
		dto.setId(review.getId());
		dto.setRating(review.getRating());
		dto.setRatingUrl(review.getUrl());
		dto.setRaviewText(review.getText());
		dto.setUserName(review.getUser().getName());
		dto.setUserProfileUrl(review.getUser().getProfileUrl());
		dto.setTimeCreated(review.getTimeCreated());
		if (!review.getUser().getImageUrl().isEmpty()) {
			try {
				detectFaces(review.getUser().getImageUrl(), dto, null, true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return dto;
	}

	public ReviewScrapeDto from(String rating, String name, String review, String imageUrl, String date) {
		ReviewScrapeDto dto = new ReviewScrapeDto();
		dto.setRating(rating);
		dto.setImageUrl(imageUrl);
		dto.setRaviewText(review);
		dto.setTimeCreated(date);
		dto.setUserName(name);
		if (!imageUrl.isEmpty()) {
			try {
				detectFaces(imageUrl, null, dto, false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return dto;
	}
	
	public void detectFaces(String url, ReviewDto reviewDto, ReviewScrapeDto scrapeDto, boolean fromApi)
			throws IOException {
		List<AnnotateImageRequest> requests = new ArrayList<>();

		Resource imgResource = resourceLoader.getResource(url);
		ByteString imgBytes = ByteString.readFrom(imgResource.getInputStream());

		Image img = Image.newBuilder().setContent(imgBytes).build();
		Feature feat = Feature.newBuilder().setType(Feature.Type.FACE_DETECTION).build();
		AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
		requests.add(request);

		try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
			BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
			List<AnnotateImageResponse> responses = response.getResponsesList();

			for (AnnotateImageResponse res : responses) {
				if (res.hasError()) {
					System.out.format("Error: %s%n", res.getError().getMessage());
					return;
				}

				for (FaceAnnotation annotation : res.getFaceAnnotationsList()) {
					if (fromApi) {
						reviewDto.setAnger(annotation.getAngerLikelihood());
						reviewDto.setJoy(annotation.getJoyLikelihood());
						reviewDto.setSorrow(annotation.getSorrowLikelihood());
						reviewDto.setSurprise(annotation.getSurpriseLikelihood());
					} else {
						scrapeDto.setAnger(annotation.getAngerLikelihood());
						scrapeDto.setJoy(annotation.getJoyLikelihood());
						scrapeDto.setSorrow(annotation.getSorrowLikelihood());
						scrapeDto.setSurprise(annotation.getSurpriseLikelihood());
					}

				}
			}
		}
	}

}
