/**
 * 
 */
package com.strandls.geoentities.dao;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * @author Abhishek Rudra
 *
 */
public class GeoentitiesDaoModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(GeoentitiesDao.class).in(Scopes.SINGLETON);
	}
}
