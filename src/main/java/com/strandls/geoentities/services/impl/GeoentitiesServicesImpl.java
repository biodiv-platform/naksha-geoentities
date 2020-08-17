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

import com.strandls.geoentities.GeoentitiesConfig;
import com.strandls.geoentities.dao.GeoentitiesDao;
import com.strandls.geoentities.pojo.Geoentities;
import com.strandls.geoentities.pojo.GeoentitiesWKTData;
import com.strandls.geoentities.services.GeoentitiesServices;
import com.strandls.geoentities.util.ColorUtil;
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

	private final Logger logger = LoggerFactory.getLogger(GeoentitiesServicesImpl.class);

	private static int IMAGE_WIDTH;
	private static int IMAGE_HEIGHT;
	private static String BACKGROUND_COLOR;
	private static String FILL_COLOR;

	static {
		IMAGE_WIDTH      = GeoentitiesConfig.getInt("geoentities.image.width");
		IMAGE_HEIGHT     = GeoentitiesConfig.getInt("geoentities.image.height");
		BACKGROUND_COLOR = GeoentitiesConfig.getString("geoentities.image.color.background");
		FILL_COLOR       = GeoentitiesConfig.getString("geoentities.image.color.fill");
	}
	
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
	public BufferedImage getImageFromGeoEntities(Long id, Integer width, Integer height, String backgroundColorHex, String fillColorHex) throws IOException {
				
		width = width == null ? IMAGE_WIDTH : width;
		height = height == null ? IMAGE_HEIGHT : height;
		backgroundColorHex = backgroundColorHex == null ? BACKGROUND_COLOR : backgroundColorHex;
		fillColorHex = fillColorHex == null ? FILL_COLOR : fillColorHex;
		
		Color fillColor = ColorUtil.hex2Rgb(fillColorHex);
		Color backgroundColor = ColorUtil.hex2Rgb(backgroundColorHex);
		
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
		
		// Get the required scale and shift 
		double scaleDimension = ((width < height ? width : height) * 98.0 ) / 100.0;
		double maxBound = (maxX-minX) > (maxY - minY) ? (maxX-minX) : (maxY - minY);
		double scale = scaleDimension / maxBound;
		
		double shiftX = minX * -1;
		double shiftY = maxY * -1;
		
		// General path creation with shift and scaling required.
		GeneralPath generalPath = new GeneralPath(shape);
		shape = generalPath.createTransformedShape(AffineTransform.getTranslateInstance(shiftX, shiftY));
		generalPath = new GeneralPath(shape);
		shape = generalPath.createTransformedShape(AffineTransform.getScaleInstance(scale, -scale));
		generalPath = new GeneralPath(shape);

		// Image creation
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2D = image.createGraphics();
		
		graphics2D.setBackground(backgroundColor);
		graphics2D.clearRect(0, 0, width, height);
		graphics2D.setColor(fillColor);
		graphics2D.fill(generalPath);
		graphics2D.draw(generalPath);
		
		return image;
	}
}
