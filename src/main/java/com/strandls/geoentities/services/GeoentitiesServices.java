/**
 * 
 */
package com.strandls.geoentities.services;

import java.util.List;

import com.strandls.geoentities.pojo.Geoentities;
import com.strandls.geoentities.pojo.GeoentitiesCreateData;

/**
 * @author Abhishek Rudra
 *
 */
public interface GeoentitiesServices {

	public Geoentities createGeoenties(GeoentitiesCreateData geoentitiesCreateData);

	public List<Geoentities> readPlaceName(String placename);

	public GeoentitiesCreateData fetchById(Long id);

	public List<List<Double>> getBoundingBox(Long id);

	Geoentities updateGeoenties(Long geoId, String wkt);
	
}
