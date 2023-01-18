package com.github.behooked;

import com.github.behooked.client.AdministrationInformant;
import com.github.behooked.client.NotificationSender;
import com.github.behooked.core.Event;
import com.github.behooked.db.EventDAO;
import com.github.behooked.resources.EventResource;
import com.github.behooked.resources.ExternalResourceTestClient;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
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
		environment.jersey().register(new EventResource(eventDao, new AdministrationInformant(client),configuration.getAdminUrl(), new NotificationSender(client)));

		if(configuration.isTestRun())
		{   
			environment.jersey().register(new ExternalResourceTestClient());

		}
	}
}
