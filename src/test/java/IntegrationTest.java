import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.behooked.DispatcherApplication;
import com.github.behooked.DispatcherConfiguration;
import com.github.behooked.api.EventJSON;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import jakarta.ws.rs.core.Response;

@ExtendWith(DropwizardExtensionsSupport.class)
public class IntegrationTest {
	
	private static DropwizardAppExtension<DispatcherConfiguration> APP= new DropwizardAppExtension<>(
			DispatcherApplication.class, ResourceHelpers.resourceFilePath("test-config.yml"));

	// In this test data is send to ExternalResourceTestClient instead of Administration-Service
	// ExtrenalResourceTestClient functions as dummy administration service
	@ Test
	void createEvent() {
		
		//create date for eventJson
		final long MILLIS=86400000;// random date for test purpose 02 January 1970
		final Date dummyDate=new Date(MILLIS); 

		
		// test createEvent and request client data from (dummy) administration service
		
		final Response responseCreateEvent= APP.client().target("http://localhost:8082/events")
				.request(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.json(new EventJSON(1l,"eventName",dummyDate,"This is a test.")));


		EventJSON postedEvent = responseCreateEvent.readEntity(EventJSON.class);

		assertThat(postedEvent.getName()).isEqualTo("eventName");
		assertThat(postedEvent.getTimestamp()).isEqualTo(dummyDate);
		assertThat(postedEvent.getData()).isEqualTo("This is a test.");
		
		
		assertThat(responseCreateEvent).extracting(Response::getStatus).isEqualTo(Response.Status.OK.getStatusCode());
		
		// create testData
		final ObjectMapper mapper = new ObjectMapper();

		final ArrayNode testClientData = mapper.createArrayNode();
		ObjectNode dataSetEntry= mapper.createObjectNode();

		dataSetEntry.put("url", "http://localhost:8082/dummyClient");
		dataSetEntry.put("secret", "seeecret");
		testClientData.add(dataSetEntry);
		
		final Long testEventId = 1L;
		
		// test receiveNotification from (dummy) administration service
		// tests if data is send to registered clients
		Response responseNotifyClients= APP.client().target("http://localhost:8082/events").path("dispatch")
				.request(MediaType.APPLICATION_JSON).header("Behooked-Administration-EventId",testEventId).post(Entity.json(testClientData));
		
		assertThat(responseNotifyClients).extracting(Response::getStatus).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());
	}
	
}
