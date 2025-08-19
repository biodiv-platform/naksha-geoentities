package com.strandls.geoentities.dto;

import java.util.List;

public class BoundingBoxDto {
	private List<List<Double>> boundingBox;

	public BoundingBoxDto() {
	}

	public BoundingBoxDto(List<List<Double>> boundingBox) {
		this.boundingBox = boundingBox;
	}

	public List<List<Double>> getBoundingBox() {
		return boundingBox;
	}

	public void setBoundingBox(List<List<Double>> boundingBox) {
		this.boundingBox = boundingBox;
	}
}
