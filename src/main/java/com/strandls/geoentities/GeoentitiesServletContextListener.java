package com.strandls.geoentities;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.glassfish.jersey.servlet.ServletContainer;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.strandls.geoentities.controllers.GeoentitiesControllerModule;
import com.strandls.geoentities.dao.GeoentitiesDaoModule;
import com.strandls.geoentities.services.impl.GeoentitiesServiceModule;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

import jakarta.persistence.Entity;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

/**
 * @author Abhishek Rudra
 */
public class GeoentitiesServletContextListener extends GuiceServletContextListener implements ServletContextListener {

	private static final Logger logger = LoggerFactory.getLogger(GeoentitiesServletContextListener.class);

	@Override
	protected Injector getInjector() {

		return Guice.createInjector(new ServletModule() {
			@Override
			protected void configureServlets() {

				Configuration configuration = new Configuration();

				try {
					for (Class<?> cls : getEntityClassesFromPackage("com")) {
						configuration.addAnnotatedClass(cls);
					}
				} catch (ClassNotFoundException | IOException | URISyntaxException e) {
					logger.error("Error adding annotated classes: ", e);
				}

				configuration = configuration.configure();
				SessionFactory sessionFactory = configuration.buildSessionFactory();

				GeometryFactory geofactory = new GeometryFactory(new PrecisionModel(), 4326);
				bind(GeometryFactory.class).toInstance(geofactory);

				Map<String, String> props = new HashMap<>();
				props.put("jakarta.ws.rs.Application", ApplicationConfig.class.getName());
				props.put("jersey.config.server.provider.packages", "com");
				props.put("jersey.config.server.wadl.disableWadl", "true");

				bind(SessionFactory.class).toInstance(sessionFactory);
				bind(ServletContainer.class).in(Scopes.SINGLETON);

				serve("/api/*").with(ServletContainer.class, props);
			}
		}, new GeoentitiesControllerModule(), new GeoentitiesDaoModule(), new GeoentitiesServiceModule());
	}

	protected List<Class<?>> getEntityClassesFromPackage(String packageName)
			throws URISyntaxException, IOException, ClassNotFoundException {

		List<String> classNames = getClassNamesFromPackage(packageName);
		List<Class<?>> classes = new ArrayList<>();
		for (String className : classNames) {
			Class<?> cls = Class.forName(className);
			Annotation[] annotations = cls.getAnnotations();

			for (Annotation annotation : annotations) {
				if (annotation instanceof Entity) {
					logger.info("Mapping entity: {}", cls.getCanonicalName());
					classes.add(cls);
				}
			}
		}

		return classes;
	}

	private static ArrayList<String> getClassNamesFromPackage(final String packageName)
			throws URISyntaxException, IOException {

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		ArrayList<String> names = new ArrayList<>();
		URL packageURL = classLoader.getResource(packageName.replace('.', '/')); // fixed resource path

		if (packageURL == null) {
			throw new IOException("Package URL not found for package: " + packageName);
		}

		URI uri = new URI(packageURL.toString());
		File folder = new File(uri.getPath());

		Files.find(Path.of(folder.getAbsolutePath()), Integer.MAX_VALUE, (p, bfa) -> bfa.isRegularFile())
				.forEach(file -> {
					String name = file.toFile().getAbsolutePath()
							.replace(folder.getAbsolutePath() + File.separatorChar, "")
							.replace(File.separatorChar, '.');
					if (name.indexOf('.') != -1) {
						name = packageName + '.' + name.substring(0, name.lastIndexOf('.'));
						names.add(name);
					}
				});

		return names;
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {

		Injector injector = (Injector) servletContextEvent.getServletContext().getAttribute(Injector.class.getName());

		if (injector != null) {
			SessionFactory sessionFactory = injector.getInstance(SessionFactory.class);
			if (sessionFactory != null && !sessionFactory.isClosed()) {
				sessionFactory.close();
			}
		}

		super.contextDestroyed(servletContextEvent);

		// Deregister JDBC drivers loaded by this webapp's ClassLoader to prevent memory
		// leaks
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		Enumeration<Driver> drivers = DriverManager.getDrivers();

		while (drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
			if (driver.getClass().getClassLoader() == cl) {
				try {
					logger.info("Deregistering JDBC driver {}", driver);
					DriverManager.deregisterDriver(driver);
				} catch (SQLException ex) {
					logger.error("Error deregistering JDBC driver {}", driver, ex);
				}
			} else {
				logger.trace("Not deregistering JDBC driver {} as it does not belong to this webapp's ClassLoader",
						driver);
			}
		}
	}
}
