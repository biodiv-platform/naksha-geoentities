/**
 * 
 */
package com.strandls.geoentities.services.impl;

import java.util.ArrayList;
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
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

/**
 * @author Abhishek Rudra
 *
 */
public class GeoentitiesServicesImpl implements GeoentitiesServices {

	private final Logger logger = LoggerFactory.getLogger(GeoentitiesServicesImpl.class);

	@Inject
	private GeoentitiesDao geoentitiesDao;

	@Inject
	private GeometryFactory geoFactory;

	@Override
	public Geoentities createGeoenties(GeoentitiesCreateData geoentitiesCreateData) {

		try {
			if (geoentitiesCreateData.getPlaceName() != null && geoentitiesCreateData.getWktData() != null) {
				WKTReader reader = new WKTReader(geoFactory);
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
	public Geoentities updateGeoenties(Long geoId, String wkt) {

		try {
			WKTReader reader = new WKTReader(geoFactory);
			Geometry topology = reader.read(wkt);
			Geoentities geoEntities = geoentitiesDao.findById(geoId);
			if(geoEntities == null) return null;
			geoEntities.setTopology(topology);
			geoEntities = geoentitiesDao.update(geoEntities);
			return geoEntities;
		} catch (ParseException e) {
			logger.error(e.getMessage());
		}
		return null;
	}
	
	@Override
	public List<Geoentities> readPlaceName(String placename) {
		List<Geoentities> result = geoentitiesDao.findByNameLike(placename);
		return result;
	}

	@Override
	public GeoentitiesCreateData fetchById(Long id) {
		Geoentities result = geoentitiesDao.findById(id);
		WKTWriter writer = new WKTWriter();
		String wktData = writer.write(result.getTopology());
		return new GeoentitiesCreateData(result.getPlaceName(), wktData);
	}

	@Override
	public List<List<Double>> getBoundingBox(Long id) {

		Geoentities geoEntity = geoentitiesDao.findById(id);
		Geometry topology = geoEntity.getTopology();
		Geometry envelop = topology.getEnvelope();

		Double  top = envelop.getCoordinates()[0].x, 
				left = envelop.getCoordinates()[0].y,
				bottom = envelop.getCoordinates()[3].x, 
				right = envelop.getCoordinates()[3].y;

		List<List<Double>> boundingBox = new ArrayList<List<Double>>();

		List<Double> topLeft = new ArrayList<Double>();
		List<Double> bottomRight = new ArrayList<Double>();

		topLeft.add(top);
		topLeft.add(left);

		bottomRight.add(bottom);
		bottomRight.add(right);

		boundingBox.add(topLeft);
		boundingBox.add(bottomRight);

		return boundingBox;
	}
}
