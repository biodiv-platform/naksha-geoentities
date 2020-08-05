/**
 * 
 */
package com.strandls.geoentities.pojo;

/**
 * @author Abhishek Rudra
 *
 */
public class GeoentitiesWKTData {

	public String placeName;
	public String wktData;

	/**
	 * 
	 */
	public GeoentitiesWKTData() {
		super();
	}

	/**
	 * @param placeName
	 * @param wktData
	 */
	public GeoentitiesWKTData(String placeName, String wktData) {
		super();
		this.placeName = placeName;
		this.wktData = wktData;
	}

	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	public String getWktData() {
		return wktData;
	}

	public void setWktData(String wktData) {
		this.wktData = wktData;
	}

}
