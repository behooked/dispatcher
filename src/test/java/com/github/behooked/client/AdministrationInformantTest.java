package com.github.behooked.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.junit5.DropwizardClientExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.Client;


@ExtendWith(DropwizardExtensionsSupport.class)
public class AdministrationInformantTest {

	@Path("/dummyAdministrationService")
	public static class NotificationReceivingResource
	{
		@POST
		public ArrayNode receiveNotification (final String eventName) {
			
			// testData
			final ObjectMapper mapper = new ObjectMapper();
			final ArrayNode testClientData = mapper.createArrayNode();
			ObjectNode dataSetEntry= mapper.createObjectNode();

			dataSetEntry.put("url", "https://example.io");
			dataSetEntry.put("secret", "testSecret");
			testClientData.add(dataSetEntry);

			return testClientData;
		}
	}
	
	private static final DropwizardClientExtension EXT = new DropwizardClientExtension(
			new NotificationReceivingResource());

	@Test
	void testAdminInformantSendsNotification() throws IOException {
		
		final ObjectMapper mapper = new ObjectMapper();
		final ArrayNode expectedClientData = mapper.createArrayNode();
		ObjectNode dataSetEntry= mapper.createObjectNode();

		dataSetEntry.put("url", "https://example.io");
		dataSetEntry.put("secret", "testSecret");
		expectedClientData.add(dataSetEntry);
		
		final Client client = new JerseyClientBuilder(EXT.getEnvironment()).build("test");
		final String eventName= "testName";

		final AdministrationInformant adminInformant = new AdministrationInformant(client);
		
		ArrayNode receivedClientData= adminInformant.getClientData(
				EXT.baseUri() + "/dummyAdministrationService", eventName);

		 assertThat(receivedClientData).isEqualTo(expectedClientData);
	}

}
