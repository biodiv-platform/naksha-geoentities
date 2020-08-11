/**
 * 
 */
package com.strandls.geoentities.services.impl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.geoentities.dao.GeoentitiesDao;
import com.strandls.geoentities.pojo.Geoentities;
import com.strandls.geoentities.pojo.GeoentitiesWKTData;
import com.strandls.geoentities.services.GeoentitiesServices;
import com.vividsolutions.jts.awt.ShapeWriter;
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

	private static final int THUMBNAIL_WIDTH = 230;

	private static final int THUMBNAIL_HEIGHT = 230;

	private final Logger logger = LoggerFactory.getLogger(GeoentitiesServicesImpl.class);

	@Inject
	private GeoentitiesDao geoentitiesDao;

	@Inject
	private GeometryFactory geoFactory;

	@Override
	public GeoentitiesWKTData createGeoenties(GeoentitiesWKTData geoentitiesCreateData) {

		try {
			if (geoentitiesCreateData.getPlaceName() != null && geoentitiesCreateData.getWktData() != null) {
				WKTReader reader = new WKTReader(geoFactory);
				Geometry topology = reader.read(geoentitiesCreateData.getWktData());
				System.out.println(topology.getGeometryType());
				Geoentities geoentities = new Geoentities(null, geoentitiesCreateData.getPlaceName(), topology);
				geoentities = geoentitiesDao.save(geoentities);
				WKTWriter writer = new WKTWriter();
				String wktData = writer.write(geoentities.getTopology());
				return new GeoentitiesWKTData(geoentities.getId(), geoentities.getPlaceName(), wktData);
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return null;
	}

	@Override
	public GeoentitiesWKTData updateGeoenties(Long geoId, String wkt) {

		try {
			WKTReader reader = new WKTReader(geoFactory);
			Geometry topology = reader.read(wkt);
			Geoentities geoEntities = geoentitiesDao.findById(geoId);
			if (geoEntities == null)
				return null;
			geoEntities.setTopology(topology);
			geoEntities = geoentitiesDao.update(geoEntities);
			WKTWriter writer = new WKTWriter();
			String wktData = writer.write(geoEntities.getTopology());
			return new GeoentitiesWKTData(geoEntities.getId(), geoEntities.getPlaceName(), wktData);
		} catch (ParseException e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public List<GeoentitiesWKTData> readPlaceName(String placename) {
		List<Geoentities> geoentitiesList = geoentitiesDao.findByNameLike(placename);
		List<GeoentitiesWKTData> result = new ArrayList<GeoentitiesWKTData>();
		for (Geoentities geoentities : geoentitiesList) {
			WKTWriter writer = new WKTWriter();
			String wktData = writer.write(geoentities.getTopology());
			result.add(new GeoentitiesWKTData(geoentities.getId(), geoentities.getPlaceName(), wktData));
		}
		return result;
	}

	@Override
	public GeoentitiesWKTData fetchById(Long id) {
		Geoentities result = geoentitiesDao.findById(id);
		WKTWriter writer = new WKTWriter();
		String wktData = writer.write(result.getTopology());
		return new GeoentitiesWKTData(result.getId(), result.getPlaceName(), wktData);
	}

	@Override
	public List<List<Double>> getBoundingBox(Long id) {

		Geoentities geoEntity = geoentitiesDao.findById(id);
		Geometry topology = geoEntity.getTopology();
		Geometry envelop = topology.getEnvelope();

		Double top = envelop.getCoordinates()[0].x, left = envelop.getCoordinates()[0].y,
				bottom = envelop.getCoordinates()[3].x, right = envelop.getCoordinates()[3].y;

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

	@Override
	public BufferedImage getImageFromGeoEntities(Long id) throws IOException {
		Geoentities geoEntity = geoentitiesDao.findById(id);
		Geometry topology = geoEntity.getTopology();

		// Convert geometry to shape object
		ShapeWriter shapeWriter = new ShapeWriter();
		Shape shape = shapeWriter.toShape(topology);
		
		// Image bounds
		Rectangle2D bounds = shape.getBounds2D();
		double minX = bounds.getMinX();
		double maxX = bounds.getMaxX();
		
		double minY = bounds.getMinY();
		double maxY = bounds.getMaxY();
		
		// Get the required scaling
		double scaleX = 1;
		double scaleY = 1;
		if((maxX-minX) > ((maxY - minY))) {
			scaleX = (THUMBNAIL_WIDTH-2) / (maxX - minX);
			scaleY = scaleX;
		}
		else {
			scaleY = (THUMBNAIL_HEIGHT-2) / (maxY - minY);
			scaleX = scaleY;
		}
		
		// Shift of the coordinates
		double shiftX = minX * -1;
		double shiftY = maxY * -1;
		
		// Image creation
		BufferedImage image = new BufferedImage(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2D = image.createGraphics();
		
		// Background setup
		graphics2D.setBackground(Color.WHITE);
		graphics2D.clearRect(0, 0, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
		graphics2D.setColor(Color.BLACK);
		
		// General path creation with shift and scaling required.
		GeneralPath generalPath = new GeneralPath(shape);
		shape = generalPath.createTransformedShape(AffineTransform.getTranslateInstance(shiftX, shiftY));
		generalPath = new GeneralPath(shape);
		shape = generalPath.createTransformedShape(AffineTransform.getScaleInstance(scaleX, -scaleY));
		generalPath = new GeneralPath(shape);

		graphics2D.draw(generalPath);
		
		return image;
	}
}
