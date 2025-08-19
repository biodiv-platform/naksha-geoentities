package com.strandls.geoentities.dto;

import java.util.Map;

public class ImageUrlResponse {
	private Map<String, Object> imageUrl;

	public ImageUrlResponse() {
	}

	public ImageUrlResponse(Map<String, Object> imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Map<String, Object> getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(Map<String, Object> imageUrl) {
		this.imageUrl = imageUrl;
	}
}
