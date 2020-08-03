/**
 * 
 */
package com.strandls.geoentities.controllers;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.strandls.geoentities.ApiConstants;
import com.strandls.geoentities.pojo.Geoentities;
import com.strandls.geoentities.pojo.GeoentitiesCreateData;
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

	@ApiOperation(value = "read the placename and suggest geoentities", notes = "return the suggested geoentities", response = Geoentities.class, responseContainer = "List")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "unable to fetch the geoentities", response = String.class) })

	public Response getGeoentities(@QueryParam("palcename") String placename) {
		try {
			List<Geoentities> result = services.readPlaceName(placename);
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

	@ApiOperation(value = "create the geoentites", notes = "Returns the geoentity created", response = Geoentities.class)
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "unable to create the geoentity", response = String.class) })

	public Response createGeoentities(
			@ApiParam(name = "geoentitiesCreateData") GeoentitiesCreateData geoentitiesCreateData) {
		try {
			Geoentities result = services.createGeoenties(geoentitiesCreateData);
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

	@ApiOperation(value = "find by geoentity id", notes = "return the geoentity object", response = GeoentitiesCreateData.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to fetch the data", response = String.class) })

	public Response findGeoentitiesById(@PathParam("id") String id) {
		try {
			Long geoentitiesId = Long.parseLong(id);
			GeoentitiesCreateData result = services.fetchById(geoentitiesId);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
	
	@PUT
	@Path(ApiConstants.UPDATE + "/{id}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "find by geoentity id", notes = "return the geoentity object", response = GeoentitiesCreateData.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to fetch the data", response = String.class) })

	public Response updateGeoentitiesById(@PathParam("id") String id, @QueryParam("wktData")String wktData) {

		if(wktData == null)
			return Response.status(Status.BAD_REQUEST).entity("Invalid parameter").build();

		try {
			Long geoentitiesId = Long.parseLong(id);
			Geoentities result = services.updateGeoenties(geoentitiesId, wktData);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.BOUNDING_BOX+"/{id}")
	@Consumes(MediaType.TEXT_HTML)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBoundingBox(@PathParam("id") Long id) {
		List<List<Double>> boundingBox = services.getBoundingBox(id);
		return Response.ok().entity(boundingBox).build();
	}
}
