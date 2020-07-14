/**
 * 
 */
package com.strandls.geoentities.services.impl;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.geoentities.dao.GeoentitiesDao;
import com.strandls.geoentities.pojo.Geoentities;
import com.strandls.geoentities.pojo.GeoentitiesCreateData;
import com.strandls.geoentities.services.GeoentitiesServices;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.WKTReader;

/**
 * @author Abhishek Rudra
 *
 */
public class GeoentitiesServicesImpl implements GeoentitiesServices {

	private final Logger logger = LoggerFactory.getLogger(GeoentitiesServicesImpl.class);

	@Inject
	private GeoentitiesDao geoentitiesDao;

	@Override
	public Geoentities createGeoenties(GeoentitiesCreateData geoentitiesCreateData) {

		try {
			if (geoentitiesCreateData.getPlaceName() != null && geoentitiesCreateData.getWktData() != null) {
				GeometryFactory geofactory = new GeometryFactory(new PrecisionModel(), 4326);
				WKTReader reader = new WKTReader(geofactory);
				Geometry topology = reader.read(geoentitiesCreateData.getWktData());
				System.out.println(topology.getGeometryType());
				Geoentities geoentities = new Geoentities(null, geoentitiesCreateData.getPlaceName(), topology);
				geoentities = geoentitiesDao.save(geoentities);
				return geoentities;
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return null;
	}

	@Override
	public List<Geoentities> readPlaceName(String placename) {
		List<Geoentities> result = geoentitiesDao.findByNameLike(placename);
		return result;
	}

}
