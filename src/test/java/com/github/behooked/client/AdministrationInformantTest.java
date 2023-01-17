package com.github.behooked.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.junit5.DropwizardClientExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Response;

@ExtendWith(DropwizardExtensionsSupport.class)
public class AdministrationInformantTest {

	@Path("/dummyAdministrationService")
	public static class NotificationReceivingResource
	{
		@POST
		public String receiveNotification (final String eventName, @HeaderParam("Behooked-Dispatcher-Notification-EventId")final Long eventId) {
			
			return "Notification received.";
		}
	}
	private static final DropwizardClientExtension EXT = new DropwizardClientExtension(
			new NotificationReceivingResource());

	
	@Test
	void testSendNotification() throws IOException {
		
		final Client client = new JerseyClientBuilder(EXT.getEnvironment()).build("test");
		final Long eventId = 1L;
		final String eventName= "testName";

		final AdministrationInformant adminInformant = new AdministrationInformant(client);
		
		Response response = adminInformant.sendNotification(
				EXT.baseUri() + "/dummyAdministrationService", eventName,eventId);
		
		 assertEquals("Notification received.", response.readEntity(String.class));
	}

}
