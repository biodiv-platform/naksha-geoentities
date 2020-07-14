/**
 * 
 */
package com.strandls.geoentities.services.impl;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.strandls.geoentities.services.GeoentitiesServices;

/**
 * @author Abhishek Rudra
 *
 */
public class GeoentitiesServiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(GeoentitiesServices.class).to(GeoentitiesServicesImpl.class).in(Scopes.SINGLETON);
	}
}
