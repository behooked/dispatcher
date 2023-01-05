package com.github.behooked;

import org.glassfish.jersey.servlet.ServletContainer;

import com.github.behooked.client.AdministrationInformant;
import com.github.behooked.core.Event;
import com.github.behooked.db.EventDAO;
import com.github.behooked.resources.EventResource;
import com.github.behooked.resources.NotificationResource;

import io.dropwizard.client.JerseyClientBuilder;

import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.DropwizardResourceConfig;
import io.dropwizard.jersey.jackson.JacksonMessageBodyProvider;
import io.dropwizard.jersey.setup.JerseyContainerHolder;
import jakarta.ws.rs.client.Client;

public class DispatcherApplication extends Application<DispatcherConfiguration> {

	public static void main(final String[] args) throws Exception {
		new DispatcherApplication().run(args);
	}

	private final HibernateBundle<DispatcherConfiguration> hibernate = new HibernateBundle<DispatcherConfiguration>(Event.class) {
		@Override
		public DataSourceFactory getDataSourceFactory(DispatcherConfiguration configuration) {
			return configuration.getDataSourceFactory();
		}
	};

	@Override
	public String getName() {
		return "Dispatcher";
	}

	@Override
	public void initialize(final Bootstrap<DispatcherConfiguration> bootstrap) {

		bootstrap.addBundle(hibernate);
	}




	@Override
	public void run(final DispatcherConfiguration configuration,
			final Environment environment) {
		final EventDAO eventDao = new EventDAO(hibernate.getSessionFactory());


		final Client client = new JerseyClientBuilder(environment).using(configuration.getJerseyClientConfiguration())
				.build(getName());

		environment.jersey().register(new EventResource(eventDao, new AdministrationInformant(client)));
        
	//	environment.jersey().register(new NotificationResource(eventDao,client));

		
		// create new jersey servlet for admin port
		DropwizardResourceConfig jerseyConfig = new DropwizardResourceConfig(environment.metrics());
		JerseyContainerHolder servletContainerHolder = new JerseyContainerHolder(new ServletContainer(jerseyConfig));

		// add servlet to admin-environment and map it to /admin/* 
		environment.admin().addServlet("admin resources", servletContainerHolder.getContainer()).addMapping("/api/*");


		// create + register proxy for NotificationResource

		Class<?> [] classArray = new Class<?>[2];
		classArray[0] = EventDAO.class;
		classArray[1]= Client.class;
		Object [] constructorArguments = new Object [2];
		constructorArguments[0] = eventDao;
		constructorArguments[1] = client;

		NotificationResource proxyNotificationResource = new UnitOfWorkAwareProxyFactory(hibernate).create(NotificationResource.class, classArray, constructorArguments);

		jerseyConfig.register(proxyNotificationResource);
		

		//jerseyConfig.register(new NotificationResource(eventDao,client));

		//enable Jackson
		jerseyConfig.register(new JacksonMessageBodyProvider(Jackson.newObjectMapper())); 
	}
}
