/**
 * 
 */
package com.strandls.geoentities.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.bedatadriven.jackson.datatype.jts.serialization.GeometryDeserializer;
import com.bedatadriven.jackson.datatype.jts.serialization.GeometrySerializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vividsolutions.jts.geom.Geometry;

/**
 * @author Abhishek Rudra
 *
 */

@Entity
@Table(name = "geoentities")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Geoentities {

	public Long id;
	public String placeName;
	public Geometry topology;

	/**
	 * 
	 */
	public Geoentities() {
		super();
	}

	/**
	 * @param id
	 * @param placeName
	 * @param topology
	 */
	public Geoentities(Long id, String placeName, Geometry topology) {
		super();
		this.id = id;
		this.placeName = placeName;
		this.topology = topology;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "placename")
	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	@Column(name = "topology", columnDefinition = "Geometry", nullable = false)
	@JsonSerialize(using = GeometrySerializer.class)
	@JsonDeserialize(contentUsing = GeometryDeserializer.class)
	public Geometry getTopology() {
		return topology;
	}

	public void setTopology(Geometry topology) {
		this.topology = topology;
	}

}
