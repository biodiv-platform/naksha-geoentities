package com.strandls.geoentities;

import java.util.HashSet;
import java.util.Set;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;
import org.glassfish.jersey.servlet.ServletContainer;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.ws.rs.core.Application;

@OpenAPIDefinition(info = @Info(title = "Geoentities API", version = "1.0.0", description = "API for geoentities"), servers = {
		@Server(url = "http://localhost:8080/geoentities-api/api/") })
public class ApplicationConfig extends Application {
	private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

	@Override
	public Set<Object> getSingletons() {
		Set<Object> singletons = new HashSet<>();

		// Guice<→>HK2 integration
		singletons.add(new ContainerLifecycleListener() {
			@Override
			public void onStartup(Container container) {
				try {
					ServletContainer servlet = (ServletContainer) container;
					ServiceLocator svcLocator = container.getApplicationHandler().getInjectionManager()
							.getInstance(ServiceLocator.class);

					GuiceBridge.getGuiceBridge().initializeGuiceBridge(svcLocator);
					GuiceIntoHK2Bridge bridge = svcLocator.getService(GuiceIntoHK2Bridge.class);
					Injector injector = (Injector) servlet.getServletContext().getAttribute(Injector.class.getName());
					bridge.bridgeGuiceInjector(injector);
					logger.info("Guice injector bridged into HK2");
				} catch (Exception e) {
					logger.error("Bridge initialization failed", e);
				}
			}

			@Override
			public void onShutdown(Container container) {
				/* No-op */ }

			@Override
			public void onReload(Container container) {
				/* No-op */ }
		});

		return singletons;
	}

	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<>();
		classes.add(MultiPartFeature.class);
		return classes;
	}
}
