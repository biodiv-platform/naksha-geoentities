/**
 * 
 */
package com.strandls.geoentities.controllers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import com.strandls.geoentities.ApiConstants;
import com.strandls.geoentities.pojo.GeoentitiesWKTData;
import com.strandls.geoentities.services.GeoentitiesServices;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Abhishek Rudra
 *
 */
@Api("Geoentities Services")
@Path(ApiConstants.V1 + ApiConstants.SERVICES)
public class GeoentitiesController {

	@Inject
	private GeoentitiesServices services;

	@GET
	@Path(ApiConstants.PING)
	@Produces(MediaType.TEXT_PLAIN)

	@ApiOperation(value = "ping pong", notes = "returns pong", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to get pong", response = String.class) })

	public Response getPing() {
		return Response.status(Status.OK).entity("PONG").build();
	}

	@GET
	@Path(ApiConstants.READ + ApiConstants.PLACENAME)
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "read the placename and suggest geoentities", notes = "return the suggested geoentities", response = GeoentitiesWKTData.class, responseContainer = "List")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "unable to fetch the geoentities", response = String.class) })

	public Response getGeoentities(@QueryParam("palcename") String placename) {
		try {
			List<GeoentitiesWKTData> result = services.readPlaceName(placename);
			if (result != null)
				return Response.status(Status.OK).entity(result).build();
			return Response.status(Status.NOT_FOUND).entity("cannot find palce name").build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.CREATE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "create the geoentites", notes = "Returns the geoentity created", response = GeoentitiesWKTData.class)
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "unable to create the geoentity", response = String.class) })

	public Response createGeoentities(
			@ApiParam(name = "geoentitiesCreateData") GeoentitiesWKTData geoentitiesCreateData) {
		try {
			GeoentitiesWKTData result = services.createGeoenties(geoentitiesCreateData);
			if (result != null)
				return Response.status(Status.OK).entity(result).build();
			return Response.status(Status.NOT_ACCEPTABLE).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.READ + "/{id}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "find by geoentity id", notes = "return the geoentity object", response = GeoentitiesWKTData.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to fetch the data", response = String.class) })

	public Response findGeoentitiesById(@PathParam("id") String id) {
		try {
			Long geoentitiesId = Long.parseLong(id);
			GeoentitiesWKTData result = services.fetchById(geoentitiesId);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Path(ApiConstants.UPDATE + "/{id}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "update by geoentity id", notes = "return the geoentity object", response = GeoentitiesWKTData.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to fetch the data", response = String.class) })

	public Response updateGeoentitiesById(@PathParam("id") String id, @QueryParam("wktData") String wktData) {

		if (wktData == null)
			return Response.status(Status.BAD_REQUEST).entity("Invalid parameter").build();

		try {
			Long geoentitiesId = Long.parseLong(id);
			GeoentitiesWKTData result = services.updateGeoenties(geoentitiesId, wktData);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.GEO_JSON + "/{id}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "get the geoJson by geoentity id", notes = "return the geoentity object", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to fetch the geoJsonData", response = String.class) })

	public Response getGeoJsonById(@PathParam("id") String id) {
		try {
			Long geoentitiesId = Long.parseLong(id);
			String result = services.getGeoJson(geoentitiesId);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
	
	@GET
	@Path(ApiConstants.BOUNDING_BOX + "/{id}")
	@Consumes(MediaType.TEXT_HTML)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Get bounding box of the geoentity by id", notes = "return the Bounding box", response = List.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to fetch the data", response = String.class) })

	public Response getBoundingBox(@PathParam("id") Long id) {
		List<List<Double>> boundingBox = services.getBoundingBox(id);
		return Response.ok().entity(boundingBox).build();
	}

	/**
	 * Generate the image for given geoentities on the fly.
	 * 
	 * @param id - Id of the geoEntitities image needs to be generated.
	 * @return
	 * @throws IOException
	 */
	@GET
	@Path("/image" + "/{id}")
	@Consumes(MediaType.TEXT_PLAIN)

	@ApiOperation(value = "Get the image of geoentity by id", notes = "return the Image of the geoEntity", response = File.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to fetch the data", response = String.class) })

	public Response getImageFromGeoEntities(@PathParam("id") Long id, @QueryParam("width") Integer width,
			@QueryParam("height") Integer height, @QueryParam("backgroundColor") String backgroundColorHex,
			@QueryParam("fillColor") String fillColorHex) throws IOException {

		BufferedImage imagePath = services.getImageFromGeoEntities(id, width, height, backgroundColorHex, fillColorHex);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(imagePath, "png", baos);

		StreamingOutput sout;
		sout = new StreamingOutput() {
			@Override
			public void write(OutputStream out) throws IOException, WebApplicationException {
				baos.writeTo(out);
			}
		};
		return Response.ok(sout).type("image/png").build();
	}

	/**
	 * This method is written to just to get the url. Can be useful if we wish to
	 * keep the track of image and location on the file system.
	 * 
	 * @param request
	 * @param id
	 * @return
	 */
	@GET
	@Path("image")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Thumbnail url", notes = "returns thumbnail url", response = Map.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to get url", response = String.class) })

	public Response getImagePathFromGeoEntities(@Context HttpServletRequest request, @QueryParam("id") String id) {
		Map<String, Object> result = new HashMap<String, Object>();

		result.put("url", request.getRequestURL() + "/" + id);
		result.put("uri", request.getRequestURI() + "/" + id);

		return Response.status(Status.OK).entity(result).build();
	}
}
