package com.github.behooked.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

//This resource is used for testing purpose only. ExternalResourceTestClient functions as client when data is send to the administration service. 
//It returns a list with dummy client data.

@Produces(MediaType.APPLICATION_JSON)
@Path("/testClient")
public class ExternalResourceTestClient {

	@POST
	@UnitOfWork
	ArrayNode testReceiveNotificationFromAdminInformant(final String eventName) 
	{
	
		//create dummyClientData
		
		final ObjectMapper mapper = new ObjectMapper();
		final ArrayNode testClientData = mapper.createArrayNode();
		ObjectNode dataSetEntry= mapper.createObjectNode();

		dataSetEntry.put("url", "https://example.io");
		dataSetEntry.put("secret", "testSecret");
		testClientData.add(dataSetEntry);

			return testClientData;
	}
}
