/**
 * 
 */
package com.strandls.geoentities.pojo;

/**
 * @author Abhishek Rudra
 *
 */
public class GeoentitiesWKTData {

	public Long id;
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
	public GeoentitiesWKTData(Long id, String placeName, String wktData) {
		super();
		this.id = id;
		this.placeName = placeName;
		this.wktData = wktData;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
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
