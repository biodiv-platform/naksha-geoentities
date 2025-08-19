package com.strandls.geoentities.controllers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.strandls.geoentities.ApiConstants;
import com.strandls.geoentities.dto.BoundingBoxDto;
import com.strandls.geoentities.dto.ImageUrlResponse;
import com.strandls.geoentities.pojo.GeoentitiesWKTData;
import com.strandls.geoentities.services.GeoentitiesServices;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;

@Tag(name = "Geoentities Services")
@Path(ApiConstants.V1 + ApiConstants.SERVICES)
public class GeoentitiesController {

	@Inject
	private GeoentitiesServices services;

	@GET
	@Path(ApiConstants.PING)
	@Produces(MediaType.TEXT_PLAIN)
	@Operation(summary = "Ping pong", description = "Returns pong")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "PONG", content = @Content(mediaType = "text/plain")), })
	public Response getPing() {
		return Response.status(Response.Status.OK).entity("PONG").build();
	}

	@GET
	@Path(ApiConstants.READ + ApiConstants.PLACENAME)
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Read placename", description = "Suggest geoentities for the placename")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = GeoentitiesWKTData.class)))),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content) })
	public Response getGeoentities(@QueryParam("palcename") String placename) {
		try {
			List<GeoentitiesWKTData> result = services.readPlaceName(placename);
			return result != null ? Response.ok(result).build()
					: Response.status(Response.Status.NOT_FOUND).entity("Cannot find place name").build();
		} catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.CREATE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Create geoentity", description = "Creates and returns the geoentity")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GeoentitiesWKTData.class))),
			@ApiResponse(responseCode = "406", description = "Not Acceptable"),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content) })
	public Response createGeoentities(
			@Parameter(description = "Geoentity data") GeoentitiesWKTData geoentitiesCreateData) {
		try {
			GeoentitiesWKTData result = services.createGeoenties(geoentitiesCreateData);
			return result != null ? Response.ok(result).build()
					: Response.status(Response.Status.NOT_ACCEPTABLE).build();
		} catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.READ + "/{id}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Find geoentity by ID", description = "Returns geoentity by ID")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GeoentitiesWKTData.class))),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "400", description = "Bad Request") })
	public Response findGeoentitiesById(@PathParam("id") String id) {
		try {
			Long geoentitiesId = Long.parseLong(id);
			GeoentitiesWKTData result = services.fetchById(geoentitiesId);
			return Response.ok(result).build();
		} catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Path(ApiConstants.UPDATE + "/{id}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Update geoentity by ID", description = "Updates and returns geoentity")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GeoentitiesWKTData.class))),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content) })
	public Response updateGeoentitiesById(@PathParam("id") String id, @QueryParam("wktData") String wktData) {
		if (wktData == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid parameter").build();
		}
		try {
			Long geoentitiesId = Long.parseLong(id);
			GeoentitiesWKTData result = services.updateGeoenties(geoentitiesId, wktData);
			return Response.ok(result).build();
		} catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.GEO_JSON + "/{id}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Get GeoJSON", description = "Returns geoentity GeoJSON")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content) })
	public Response getGeoJsonById(@PathParam("id") String id) {
		try {
			Long geoentitiesId = Long.parseLong(id);
			String result = services.getGeoJson(geoentitiesId);
			return Response.ok(result).build();
		} catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.BOUNDING_BOX + "/{id}")
	@Consumes(MediaType.TEXT_HTML)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Get bounding box", description = "Returns bounding box of geoentity")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Bounding Box", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BoundingBoxDto.class))),
			@ApiResponse(responseCode = "400", description = "Bad Request") })
	public Response getBoundingBox(@PathParam("id") Long id) {
		BoundingBoxDto boundingBox = new BoundingBoxDto(services.getBoundingBox(id));
		return Response.ok(boundingBox).build();
	}

	@GET
	@Path("/image/{id}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces("image/png")
	@Operation(summary = "Get geoentity image", description = "Returns image of geoentity")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Geoentity image", content = @Content(mediaType = "image/png", schema = @Schema(type = "string", format = "binary") // <---
			// Important
			)), @ApiResponse(responseCode = "400", description = "Bad Request") })
	public Response getImageFromGeoEntities(@PathParam("id") Long id, @QueryParam("width") Integer width,
			@QueryParam("height") Integer height, @QueryParam("backgroundColor") String backgroundColorHex,
			@QueryParam("fillColor") String fillColorHex) throws IOException {
		BufferedImage image = services.getImageFromGeoEntities(id, width, height, backgroundColorHex, fillColorHex);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "png", baos);

		StreamingOutput output = out -> baos.writeTo(out);
		return Response.ok(output).type("image/png").build();
	}

	@GET
	@Path("image")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Get image URL", description = "Returns image URL for geoentity")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Image URL", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ImageUrlResponse.class))),
			@ApiResponse(responseCode = "400", description = "Bad Request") })
	public Response getImagePathFromGeoEntities(@Context HttpServletRequest request, @QueryParam("id") String id) {
		Map<String, Object> result = new HashMap<>();
		result.put("url", request.getRequestURL() + "/" + id);
		result.put("uri", request.getRequestURI() + "/" + id);
		return Response.ok(new ImageUrlResponse(result)).build();
	}
}
