/**
 * 
 */
package com.strandls.geoentities.controllers;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * @author Abhishek Rudra
 *
 */
public class GeoentitiesControllerModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(GeoentitiesController.class).in(Scopes.SINGLETON);
	}
}
