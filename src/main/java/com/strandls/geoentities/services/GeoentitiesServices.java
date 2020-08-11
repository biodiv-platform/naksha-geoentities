/**
 * 
 */
package com.strandls.geoentities.services;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.imageio.stream.ImageOutputStream;

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

	public BufferedImage getImageFromGeoEntities(Long id) throws IOException;

}
