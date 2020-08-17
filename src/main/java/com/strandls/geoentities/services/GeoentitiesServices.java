/**
 * 
 */
package com.strandls.geoentities.services;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import com.strandls.geoentities.pojo.GeoentitiesWKTData;

/**
 * @author Abhishek Rudra
 *
 */
public interface GeoentitiesServices {

	public GeoentitiesWKTData createGeoenties(GeoentitiesWKTData geoentitiesCreateData);

	public List<GeoentitiesWKTData> readPlaceName(String placename);

	public GeoentitiesWKTData fetchById(Long id);

	public List<List<Double>> getBoundingBox(Long id);

	public GeoentitiesWKTData updateGeoenties(Long geoId, String wkt);

	public BufferedImage getImageFromGeoEntities(Long id, Integer width, Integer height, String backgroundColorHex,
			String fillColorHex) throws IOException;

	public String getGeoJson(Long id);

}
